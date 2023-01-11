package de.hsos.nearbychat.service.bluetooth

import android.bluetooth.le.AdvertisingSetParameters
import android.os.Handler
import android.os.Looper
import android.util.Log
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
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
    private var idGenerator: AtomicIdGenerator = AtomicIdGenerator()
    private var neighbourTable: NeighbourTable = NeighbourTable(TIMEOUT)
    private var messageBuffer: MessageBuffer = MessageBuffer()
    private var slidingWindowTable: SlidingWindow = SlidingWindow()
    private val unacknowledgedMessageList: UnacknowledgedMessageList = UnacknowledgedMessageList()
    private val meshExecutor: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()

    init {
        this.updateOwnProfile(this.ownProfile)
        this.advertisementExecutor = AdvertisementExecutor(
            this.advertiser,
            MeshController.ADVERTISING_INTERVAL,
            this.advertiser.getMaxMessageSize(),
            this.neighbourTable
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
        Log.d(TAG, "refresh() called")
        val timeoutList = this.neighbourTable.removeNeighboursWithTimeout()
        this.observer.onNeighbourTimeout(timeoutList)
        val unsentMessages = this.unacknowledgedMessageList.getMessages()
        Log.d(TAG, "Timeout for: $unsentMessages")
        unsentMessages.forEach { this@MeshController.advertisementExecutor.addToQueue(it.toString()) }
    }

    fun connect() :Boolean{
        Log.d(TAG, "connect() called")
        if (!this.scanner.start()) {
            Log.w(TAG, "connect: scanner failed")
            return false
        }
        if (!this.advertiser.start()
        ) {
            Log.w(TAG, "connect: advertiser failed")
            return false
        }
        if(!this.advertisementExecutor.start()){
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
        this.advertisementExecutor.addToQueue(
            Advertisement.Builder()
                .type(MessageType.MESSAGE_MESSAGE.type)
                .id(this.idGenerator.next())
                .message(message.content)
                .build()
                .toString()
        )
    }

    fun updateOwnProfile(ownProfile: OwnProfile) {
        Log.d(TAG, "updateOwnProfile() called with: ownProfile = $ownProfile")
        this.ownProfile = ownProfile
        val selfAdvertisement: Advertisement = Advertisement.Builder()
            .type(MessageType.NEIGHBOUR_MESSAGE.type)
            .hops(MeshController.MAX_HOPS)
            .rssi(0)
            .address(ownProfile.address)
            .name(ownProfile.name)
            .description(ownProfile.description)
            .color(ownProfile.color)
            .build()
        val self: Neighbour =
            Neighbour(ownProfile.address, 0, MeshController.MAX_HOPS, 0, null, selfAdvertisement)
        this.neighbourTable.updateNeighbour(self)
    }

    override fun onPackage(macAddress: String, rssi: Int, advertisementPackage: String) {
        Log.i(
            TAG,
            "onPackage() called with: macAddress = $macAddress, rssi = $rssi, advertisementPackage = $advertisementPackage"
        )
        Handler(Looper.getMainLooper()).post {
            val packageID: Char = advertisementPackage[0]
            var lastSeparator: Int = advertisementPackage.indexOf(':') + 1
            var nextSeparator: Int = advertisementPackage.indexOf('}') + 1
            do {
                var message: String = advertisementPackage.substring(lastSeparator, nextSeparator)
                if (!message.contains('{') && !message.contains('}')) {
                    message = this.messageBuffer.add(packageID, message) ?: message
                }
                this.parseMessage(message)
                lastSeparator = nextSeparator + 1
                nextSeparator = advertisementPackage.indexOf('}', nextSeparator + 1)
                if (nextSeparator == -1 && lastSeparator < advertisementPackage.length) {
                    this.messageBuffer.add(packageID, advertisementPackage.substring(lastSeparator))
                }
            } while (nextSeparator != -1)
        }
    }

    private fun parseMessage(
        message: String
    ) {
        val advertisement =
            Advertisement.Builder().rawMessage(message).build()
        when (advertisement.type) {
            MessageType.MESSAGE_MESSAGE.type -> {
                handleMessage(advertisement)
            }
            MessageType.ACKNOWLEDGE_MESSAGE.type -> {
                handleAcknowledgment(advertisement)
            }
            MessageType.NEIGHBOUR_MESSAGE.type -> {
                handleNeighbour(advertisement)
            }
            else -> {
                Log.w(
                    TAG,
                    "onMessage: received faulty message: $message"
                )
            }
        }
    }


    private fun handleAcknowledgment(advertisement: Advertisement) {
        if (this.ownProfile.address == advertisement.receiver) {
            if (this.unacknowledgedMessageList.acknowledge(
                    advertisement.sender!!,
                    advertisement.timestamp!!
                )
            ) {
                Log.d(TAG, "onMessage: received ack for this device")
                this.observer.onMessageAck(advertisement)
            }
        } else {
            val nextTarget: String? =
                this.neighbourTable.getClosestNeighbour(advertisement.receiver as String)
            if (nextTarget == null) {
                Log.w(
                    TAG,
                    "onMessage: cant forward message: $advertisement ${advertisement.receiver} is not reachable"
                )
            } else {
                advertisement.address = nextTarget
                this.advertisementExecutor.addToQueue(advertisement.toString())
            }
        }
    }

    private fun handleMessage(advertisement: Advertisement) {
        if (this.ownProfile.address == advertisement.receiver) {
            Log.d(TAG, "onMessage: received message for this device")
            if (this.slidingWindowTable.add(advertisement.id!!)) {
                this.observer.onMessage(advertisement)
            }
        } else {
            val nextTarget: String? =
                this.neighbourTable.getClosestNeighbour(advertisement.receiver as String)
            if (nextTarget == null) {
                Log.w(
                    TAG,
                    "onMessage: cant forward message: $advertisement ${advertisement.receiver} is unknown"
                )
            } else {
                advertisement.address = nextTarget
                this.advertisementExecutor.addToQueue(advertisement.toString())
            }
        }
    }

    private fun handleNeighbour(
        advertisement: Advertisement
    ) {
        advertisement.decrementHop()
        if (advertisement.hops!! < 0) {
            return
        } else {
            val timeStamp: Long = System.currentTimeMillis()
            val neighbour: Neighbour = Neighbour(
                advertisement.address!!,
                advertisement.rssi!!,
                advertisement.hops!!,
                timeStamp,
                null,
                advertisement
            )
            if(advertisement.hops!! >= MeshController.MAX_HOPS - 1){
                neighbour.closestNeighbour = neighbour
            }
            this.neighbourTable.updateNeighbour(neighbour)
            this.observer.onNeighbour(advertisement)
        }
    }

    companion object {
        const val MAX_HOPS: Int = 10
        const val TIMEOUT: Long = 5000L
        const val ADVERTISING_INTERVAL: Long = AdvertisingSetParameters.INTERVAL_HIGH.toLong()
    }
}