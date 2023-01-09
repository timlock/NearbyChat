package de.hsos.nearbychat.service.bluetooth

import android.bluetooth.le.AdvertisingSetParameters
import android.os.Handler
import android.os.Looper
import android.util.Log
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.advertise.Client
import de.hsos.nearbychat.service.bluetooth.advertise.AdvertisementExecutor
import de.hsos.nearbychat.service.bluetooth.scan.ScannerObserver
import de.hsos.nearbychat.service.bluetooth.util.*

class MeshController(
    private var observer: MeshObserver,
    private var advertiser: Advertiser,
    private var ownProfile: OwnProfile,
    private var scanner: Scanner
    ) : ScannerObserver {
    private val TAG: String = MeshController::class.java.simpleName
    private var advertisementExecutor: AdvertisementExecutor
    private var idGenerator: AtomicIdGenerator = AtomicIdGenerator()
    private var neighbourTable: NeighbourTable = NeighbourTable(TIMEOUT)
    private var messageBuffer: MessageBuffer = MessageBuffer()

    init {
        this.advertisementExecutor = AdvertisementExecutor(
            this.advertiser as Client,
            AdvertisingSetParameters.INTERVAL_MEDIUM.toLong(),
            this.advertiser.getMaxMessageSize()
        )
        this.scanner.subscribe(this)
    }

    fun startScan() {
        Log.d(TAG, "startScan: ")
        this.scanner.start()
    }
    fun stopScan(){
        Log.d(TAG, "stopScan: ")
        this.scanner.stop()
    }

    fun startAdvertise() {
        Log.d(TAG, "startAdvertise: ")
        this.advertiser.start()
        this.advertisementExecutor.start()
    }

    fun stopAdvertising() {
        Log.d(TAG, "stopAdvertising: ")
        this.advertisementExecutor.stop()
        this.advertiser.stop()
    }

    fun sendMessage(message: Message) {
        this.advertisementExecutor.send(
            Advertisement.Builder()
                .type(MessageType.MESSAGE_MESSAGE)
                .id(this.idGenerator.next())
                .message(message.content)
                .build()
                .toString()
        )
    }

    override fun onPackage(macAddress: String, rssi: Int, advertisementPackage: String) {
        Log.i(
            TAG,
            "onPackage() called with: macAddress = $macAddress, rssi = $rssi, advertisementPackage = $advertisementPackage"
        )
        Handler(Looper.getMainLooper()).post {
            val timeStamp: Long = System.currentTimeMillis()
            val closestNeighbour: Neighbour? = this.neighbourTable.getDirectNeighbour(macAddress)
            closestNeighbour?.rssi = rssi
            closestNeighbour?.lastSeen = timeStamp
            val packageID: Char = advertisementPackage[0]
            var lastSeparator: Int = advertisementPackage.indexOf(':') + 1
            var nextSeparator: Int = advertisementPackage.indexOf('}')
            do {
                var message: String = advertisementPackage.substring(lastSeparator, nextSeparator)
                if (!message.contains('{') && !message.contains('}')) {
                    message = this.messageBuffer.add(packageID, message) ?: message
                }
                this.parseMessage(message, closestNeighbour, advertisementPackage)
                lastSeparator = nextSeparator + 1
                nextSeparator = advertisementPackage.indexOf('}', nextSeparator)
                if (nextSeparator == -1) {
                    this.messageBuffer.add(packageID, advertisementPackage.substring(lastSeparator))
                }
            } while (nextSeparator != -1)
        }
    }

    private fun parseMessage(
        message: String,
        closestNeighbour: Neighbour?,
        advertisementPackage: String
    ) {
        val advertisement =
            Advertisement.Builder().rawMessage(message).build()
        when (advertisement.type) {
            MessageType.MESSAGE_MESSAGE -> {
                handleMessage(advertisement)
            }
            MessageType.ACKNOWLEDGE_MESSAGE -> {
                handleAcknowledgment(advertisement)
            }
            MessageType.NEIGHBOUR_MESSAGE -> {
                handleNeighbour(advertisement, closestNeighbour)
            }
            else -> {
                Log.w(
                    TAG,
                    "onMessage: received faulty message: $advertisementPackage from: $closestNeighbour"
                )
            }
        }
    }


    private fun handleAcknowledgment(advertisement: Advertisement) {
        if (this.ownProfile.address == advertisement.receiver) {
            Log.d(TAG, "onMessage: received ack for this device")
            this.observer.onMessageAck(advertisement)
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
                this.advertisementExecutor.send(advertisement.toString())
            }
        }
    }

    private fun handleMessage(advertisement: Advertisement) {
        if (this.ownProfile.address == advertisement.receiver) {
            Log.d(TAG, "onMessage: received message for this device")
            this.observer.onMessage(advertisement)
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
                this.advertisementExecutor.send(advertisement.toString())
            }
        }
    }

    private fun handleNeighbour(
        advertisement: Advertisement,
        closestNeighbour: Neighbour?
    ) {
        advertisement.decrementHop()
        val neighbour: Neighbour = Neighbour(
            advertisement.address!!,
            advertisement.rssi!!,
            MAX_HOPS - advertisement.hops!!,
            closestNeighbour!!.lastSeen,
            closestNeighbour
        )
        this.neighbourTable.updateNeighbour(neighbour)
        this.observer.onNeighbour(advertisement)
        advertisement.decrementHop()
        if(advertisement.hops!! >= 0){
            this.advertisementExecutor.send(advertisement.toString())
        }
    }

    companion object {
        const val MAX_HOPS: Int = 10
        const val TIMEOUT: Long = 5000L
    }


}