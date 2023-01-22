package de.hsos.nearbychat.service.bluetooth

import android.bluetooth.le.AdvertisingSetParameters
import android.os.Handler
import android.os.Looper
import android.util.Log
import de.hsos.nearbychat.common.domain.Message
import de.hsos.nearbychat.common.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.advertise.AdvertisementExecutor
import de.hsos.nearbychat.service.bluetooth.scan.ScannerObserver
import de.hsos.nearbychat.service.bluetooth.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class MeshController(
    private var observer: MeshObserver,
    private var advertiser: Advertiser,
    private var scanner: Scanner,
    var ownProfile: OwnProfile,
) : ScannerObserver {
    private val TAG: String = MeshController::class.java.simpleName
    private var advertisementExecutor: AdvertisementExecutor
    private var neighbourBuffer: NeighbourBuffer = NeighbourBuffer(TIMEOUT)
    private var cutMessagesBuffer: CutMessagesBuffer = CutMessagesBuffer()
    private val unacknowledgedMessageBuffer: UnacknowledgedMessageBuffer =
        UnacknowledgedMessageBuffer()
    private val meshExecutor: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()
    private val packageIDMap: HashMap<String, Char> = HashMap()

    init {
        this.updateOwnProfile(this.ownProfile)
        this.advertisementExecutor = AdvertisementExecutor(
            this.advertiser,
            MeshController.ADVERTISING_UPDATE_INTERVAL,
            this.advertiser.getMaxMessageSize(),
            this.neighbourBuffer
        )
        this.scanner.subscribe(this)
        this.meshExecutor.scheduleAtFixedRate(
            this::refresh,
            0,
            MeshController.TIMEOUT,
            TimeUnit.MILLISECONDS
        )
    }

    private fun refresh() {
        val timeoutList = this.neighbourBuffer.removeNeighboursWithTimeout()
        if (timeoutList.isNotEmpty()) {
            this.observer.onNeighbourTimeout(timeoutList)
        }
        val unacknowledgedMessages = this.unacknowledgedMessageBuffer.getMessages()
        unacknowledgedMessages.forEach { this.sendMessage(it) }
        Log.d(TAG, "refresh() timeout: $timeoutList messages resend: $unacknowledgedMessages")
    }

    fun connect(): Boolean {
        Log.d(TAG, "connect() called")
        if (!this.scanner.start()) {
            Log.w(TAG, "connect: scanner failed")
            return false
        }
        if (!this.advertiser.start()) {
            Log.w(TAG, "connect: advertiser failed")
            return false
        }
        if (!this.advertisementExecutor.start()) {
            Log.w(TAG, "connect: advertisementExecutor failed")
            return false
        }
        return true
    }

    fun disconnect() {
        Log.d(TAG, "disconnect() called")
        this.meshExecutor.shutdown()
        this.advertisementExecutor.stop()
        this.advertiser.stop()
        this.scanner.stop()
    }


    fun sendMessage(message: Message) {
        Log.d(TAG, "sendMessage() called with: message = $message")
        val nextHop: String? = this.neighbourBuffer.getClosestNeighbour(message.address)
        if (nextHop == null) {
            Log.d(TAG, "sendMessage: ${message.address} is not reachable")
        } else {
            var messageAdvertisement = Advertisement.Builder()
                .type(AdvertisementType.MESSAGE_ADVERTISEMENT.type)
                .nextHop(nextHop)
                .sender(this.ownProfile.address)
                .receiver(message.address)
                .message(message.content)
                .timestamp(message.timeStamp)
                .build()
            this.advertisementExecutor.addToQueue(messageAdvertisement.toString())
        }
        this.unacknowledgedMessageBuffer.addMessage(message)
    }

    fun updateOwnProfile(ownProfile: OwnProfile) {
        Log.d(TAG, "updateOwnProfile() called with: ownProfile = $ownProfile")
        this.ownProfile = ownProfile
        val selfAdvertisement: Advertisement = Advertisement.Builder()
            .type(AdvertisementType.NEIGHBOUR_ADVERTISEMENT.type)
            .sender(ownProfile.address)
            .hops(MeshController.MAX_HOPS)
            .rssi(0)
            .address(ownProfile.address)
            .name(ownProfile.name)
            .description(ownProfile.description)
            .color(ownProfile.color)
            .build()
        val self: Neighbour =
            Neighbour(ownProfile.address, 0, MeshController.MAX_HOPS, 0, null, selfAdvertisement)
        self.closestNeighbour = self
        this.neighbourBuffer.updateNeighbour(self)
    }

    override fun onPackage(macAddress: String, rssi: Int, packageString: String) {
        Handler(Looper.getMainLooper()).post {
            Log.d(
                TAG,
                "onPackage() called with:  rssi = $rssi, advertisementPackage = $packageString"
            )
            val advertisementPackage = AdvertisementPackage.toPackage(packageString)
            when (this.packageIDMap[macAddress]) {
                null -> this.packageIDMap[macAddress] = advertisementPackage.id!!
                advertisementPackage.id -> return@post
                else -> this.packageIDMap[macAddress] = advertisementPackage.id!!
            }
            this.storeCutOffMessagesInBuffer(advertisementPackage, macAddress)
            advertisementPackage.getMessageList().forEach {
                when (it.type) {
                    AdvertisementType.MESSAGE_ADVERTISEMENT.type -> {
                        handleMessage(it)
                    }
                    AdvertisementType.ACKNOWLEDGE_ADVERTISEMENT.type -> {
                        handleAcknowledgment(it)
                    }
                    AdvertisementType.NEIGHBOUR_ADVERTISEMENT.type -> {
                        handleNeighbour(it, rssi)
                    }
                    else -> {
                        Log.w(
                            TAG,
                            "onPackage: received faulty message: $packageString"
                        )
                    }
                }
            }
        }
    }

    private fun storeCutOffMessagesInBuffer(
        advertisementPackage: AdvertisementPackage,
        macAddress: String
    ) {
        if (advertisementPackage.getRawMessageBegin() != null) {
            this.cutMessagesBuffer.add(
                macAddress,
                advertisementPackage.id!!,
                advertisementPackage.getRawMessageBegin()!!
            )
        }
        if (advertisementPackage.getRawMessageEnd() != null) {
            val result = this.cutMessagesBuffer.add(
                macAddress,
                advertisementPackage.id!!,
                advertisementPackage.getRawMessageEnd()!!
            )
            if (result != null) {
                advertisementPackage.addAdvertisement(
                    Advertisement.Builder().rawMessage(result).build()
                )
            }
        }
    }


    private fun handleAcknowledgment(advertisement: Advertisement) {
        if (this.ownProfile.address == advertisement.receiver) {
            if (this.unacknowledgedMessageBuffer.acknowledge(
                    advertisement.sender!!,
                    advertisement.timestamp!!
                )
            ) {
                Log.d(TAG, "handleAcknowledgment: received ack for this device")
                this.observer.onMessageAck(advertisement)
            }
        } else {
            val nextTarget: String? =
                this.neighbourBuffer.getClosestNeighbour(advertisement.receiver!!)
            if (nextTarget == null) {
                Log.w(
                    TAG,
                    "handleAcknowledgment: cant forward message: $advertisement ${advertisement.receiver} is not reachable"
                )
            } else {
                advertisement.nextHop = nextTarget
                this.advertisementExecutor.addToQueue(advertisement.toString())
            }
        }
    }

    private fun handleMessage(advertisement: Advertisement) {
        Log.d(TAG, "handleMessage() called with: advertisement = $advertisement")
        if (this.ownProfile.address == advertisement.receiver) {
            Log.d(TAG, "onMessage: received message for this device")
            this.observer.onMessage(advertisement)
            this.sendAck(advertisement)
        } else {
            val nextTarget: String? =
                this.neighbourBuffer.getClosestNeighbour(advertisement.receiver as String)
            if (nextTarget == null) {
                Log.w(
                    TAG,
                    "onMessage: cant forward message: $advertisement ${advertisement.receiver} is unknown"
                )
            } else {
                advertisement.nextHop = nextTarget
                this.advertisementExecutor.addToQueue(advertisement.toString())
            }
        }
    }

    private fun sendAck(advertisement: Advertisement) {
        Log.d(TAG, "sendAck() called with: advertisement = $advertisement")
        val nextHop = this.neighbourBuffer.getClosestNeighbour(advertisement.sender!!)
        if (nextHop != null) {
            val ack = Advertisement.Builder()
                .type(AdvertisementType.ACKNOWLEDGE_ADVERTISEMENT.type)
                .nextHop(nextHop)
                .sender(this.ownProfile.address)
                .receiver(advertisement.sender!!)
                .timestamp(advertisement.timestamp!!)
                .build()
                .toString()
            this.advertisementExecutor.addToQueue(ack)
        }
    }

    private fun handleNeighbour(advertisement: Advertisement, rssi: Int) {
        if (advertisement.address == this.ownProfile.address) {
            return
        } else {
            Log.d(
                TAG,
                "handleNeighbour() called with: advertisement = $advertisement, rssi = $rssi"
            )
            advertisement.decrementHop()
            if (advertisement.hops!! < 0) {
                return
            } else {
                this.updateNeighbour(advertisement, rssi)
                this.observer.onNeighbour(advertisement)
            }
        }
    }

    private fun updateNeighbour(
        advertisement: Advertisement,
        rssi: Int
    ) {
        val timeStamp: Long = System.currentTimeMillis()
        val neighbour: Neighbour = Neighbour(
            advertisement.address!!,
            advertisement.rssi!!,
            advertisement.hops!!,
            timeStamp,
            null,
            advertisement
        )
        if (advertisement.address == advertisement.sender) {
            neighbour.closestNeighbour = neighbour
            neighbour.rssi = rssi
            advertisement.rssi = rssi
        } else {
            this.updateDirectNeighbour(advertisement.sender!!, rssi, timeStamp)
            neighbour.closestNeighbour =
                this.neighbourBuffer.getEntry(advertisement.sender!!)
        }
        neighbour.advertisement?.sender = this.ownProfile.address
        this.neighbourBuffer.updateNeighbour(neighbour)
    }

    private fun updateDirectNeighbour(address: String, rssi: Int, timestamp: Long) {
        var neighbour = this.neighbourBuffer.getEntry(address)
        if (neighbour == null) {
            val advertisement = Advertisement.Builder()
                .type(AdvertisementType.NEIGHBOUR_ADVERTISEMENT.type)
                .sender(this.ownProfile.address)
                .address(address)
                .hops(MeshController.MAX_HOPS - 1)
                .rssi(rssi)
                .name("")
                .description("")
                .color(0)
                .build()
            neighbour = Neighbour(address, rssi, MeshController.MAX_HOPS - 1, timestamp)
            neighbour.advertisement = advertisement
            neighbour.closestNeighbour = neighbour
            this.neighbourBuffer.updateNeighbour(neighbour)
        } else {
            neighbour.lastSeen = timestamp
            neighbour.rssi = rssi
        }
    }

    fun addUnsentMessages(unsentMessages: List<Message>) {
        Log.d(TAG, "addUnsentMessages() called with: unsentMessages = $unsentMessages")
        this.unacknowledgedMessageBuffer.addMessages(unsentMessages)
    }

    companion object {
        const val MAX_HOPS: Int = 5
        const val TIMEOUT: Long = 5000L
        const val ADVERTISING_INTERVAL: Long = AdvertisingSetParameters.INTERVAL_MIN.toLong()
        const val ADVERTISING_UPDATE_INTERVAL: Long =
            AdvertisingSetParameters.INTERVAL_HIGH.toLong()

    }
}