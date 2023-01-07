package de.hsos.nearbychat.service.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertisingSetParameters
import android.os.Handler
import android.os.Looper
import android.util.Log
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.advertise.BluetoothAdvertiser
import de.hsos.nearbychat.service.bluetooth.advertise.MessageHandler
import de.hsos.nearbychat.service.bluetooth.scan.BluetoothScanner
import de.hsos.nearbychat.service.bluetooth.scan.ScannerObserver
import de.hsos.nearbychat.service.bluetooth.util.*

class MeshController(
    private var observer: MeshObserver,
    private var bluetoothAdapter: BluetoothAdapter,
    private var ownProfile: OwnProfile
) : ScannerObserver {
    private val TAG: String = MeshController::class.java.simpleName
    private var advertiser: BluetoothAdvertiser =
        BluetoothAdvertiser(this.bluetoothAdapter, AdvertisingSetParameters.INTERVAL_MEDIUM)
    private var scanner: BluetoothScanner =
        BluetoothScanner(this, this.bluetoothAdapter.bluetoothLeScanner)
    private var messageHandler: MessageHandler
    private var idGenerator: AtomicIdGenerator = AtomicIdGenerator()
    private var neighbourTable: NeighbourTable = NeighbourTable(TIMEOUT)
    private var messageBuffer: MessageBuffer = MessageBuffer()

    init {
        this.messageHandler = MessageHandler(
            this.advertiser,
            AdvertisingSetParameters.INTERVAL_MEDIUM.toLong(),
            this.advertiser.maxMessageLength
        )
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
        this.messageHandler.start()
    }

    fun stopAdvertising() {
        Log.d(TAG, "stopAdvertising: ")
        this.messageHandler.stop()
        this.advertiser.stop()
    }

    fun sendMessage(message: Message) {
        this.messageHandler.send(
            AdvertisementMessage.Builder()
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
        val advertisementMessage =
            AdvertisementMessage.Builder().rawMessage(message).build()
        when (advertisementMessage.type) {
            MessageType.MESSAGE_MESSAGE -> {
                handleMessage(advertisementMessage)
            }
            MessageType.ACKNOWLEDGE_MESSAGE -> {
                handleAcknowledgment(advertisementMessage)
            }
            MessageType.NEIGHBOUR_MESSAGE -> {
                handleNeighbour(advertisementMessage, closestNeighbour)
            }
            else -> {
                Log.w(
                    TAG,
                    "onMessage: received faulty message: $advertisementPackage from: $closestNeighbour"
                )
            }
        }
    }


    private fun handleAcknowledgment(advertisementMessage: AdvertisementMessage) {
        if (this.ownProfile.address == advertisementMessage.receiver) {
            Log.d(TAG, "onMessage: received ack for this device")
            this.observer.onMessageAck(advertisementMessage)
        } else {
            val nextTarget: String? =
                this.neighbourTable.getClosestNeighbour(advertisementMessage.receiver as String)
            if (nextTarget == null) {
                Log.w(
                    TAG,
                    "onMessage: cant forward message: $advertisementMessage ${advertisementMessage.receiver} is not reachable"
                )
            } else {
                this.messageHandler.send(advertisementMessage.toString())
            }
        }
    }

    private fun handleMessage(advertisementMessage: AdvertisementMessage) {
        if (this.ownProfile.address == advertisementMessage.receiver) {
            Log.d(TAG, "onMessage: received message for this device")
            this.observer.onMessage(advertisementMessage)
        } else {
            val nextTarget: String? =
                this.neighbourTable.getClosestNeighbour(advertisementMessage.receiver as String)
            if (nextTarget == null) {
                Log.w(
                    TAG,
                    "onMessage: cant forward message: $advertisementMessage ${advertisementMessage.receiver} is unknown"
                )
            } else {
                this.messageHandler.send(advertisementMessage.toString())
            }
        }
    }

    private fun handleNeighbour(
        advertisementMessage: AdvertisementMessage,
        closestNeighbour: Neighbour?
    ) {
        advertisementMessage.decrementHop()
        val neighbour: Neighbour = Neighbour(
            advertisementMessage.sender!!,
            advertisementMessage.rssi!!,
            MAX_HOPS - advertisementMessage.hops!!,
            closestNeighbour!!.lastSeen,
            closestNeighbour
        )
        this.neighbourTable.updateNeighbour(neighbour)
        this.observer.onNeighbour(advertisementMessage)
        advertisementMessage.decrementHop()
        if(advertisementMessage.hops!! >= 0){
            this.messageHandler.send(advertisementMessage.toString())
        }
    }

    companion object {
        const val MAX_HOPS: Int = 10
        const val TIMEOUT: Long = 5000L
    }


}