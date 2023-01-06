package de.hsos.nearbychat.service.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertisingSetParameters
import android.util.Log
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.service.bluetooth.util.*

class MeshController(
    private var observer: MeshObserver,
    private var bluetoothAdapter: BluetoothAdapter,
    private var ownAddress: String
) : ScannerObserver {
    private val TAG: String = MeshController::class.java.simpleName
    private var advertiser: BluetoothAdvertiser =
        BluetoothAdvertiser(this.bluetoothAdapter, AdvertisingSetParameters.INTERVAL_MEDIUM)
    private var scanner: BluetoothScanner =
        BluetoothScanner(this, this.bluetoothAdapter.bluetoothLeScanner)
    private var messageHandler: MessageHandler
    private var idGenerator: AtomicIdGenerator = AtomicIdGenerator()
    private var neighbourTable: NeighbourTable = NeighbourTable(MeshController.TIMEOUT)

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
        )
    }

    override fun onMessage(macAddress: String, rssi: Int, message: String) {
        Log.d(TAG, "onMessage() called with: macAddress = $macAddress, message = $message")
        val timeStamp: Long = System.currentTimeMillis()
        val advertisementMessage = AdvertisementMessage.Builder().rawMessage(message).build()
        val closestNeighbour: Neighbour? = this.neighbourTable.getDirectNeighbour(macAddress)
        closestNeighbour?.rssi = rssi
        closestNeighbour?.lastSeen = timeStamp
        when (advertisementMessage.type) {
            MessageType.MESSAGE_MESSAGE -> {
                handleMessage(advertisementMessage, message)
            }
            MessageType.ACKNOWLEDGE_MESSAGE -> {
                handleAcknowledgment(advertisementMessage, message)
            }
            MessageType.NEIGHBOUR_MESSAGE -> {
                handleNeighbour(advertisementMessage, rssi, timeStamp, closestNeighbour)
            }
            else -> {
                Log.w(TAG, "onMessage: received faulty message: $message from: $macAddress")
            }
        }
    }


    private fun handleAcknowledgment(
        advertisementMessage: AdvertisementMessage,
        message: String
    ) {
        if (this.ownAddress == advertisementMessage.address) {
            Log.d(TAG, "onMessage: received ack for this device")
            this.observer.onMessageAck(advertisementMessage)
        } else {
            val nextTarget: String? =
                this.neighbourTable.getClosestNeighbour(advertisementMessage.address as String)
            if (nextTarget == null) {
                Log.w(
                    TAG,
                    "onMessage: cant forward message: $message ${advertisementMessage.address} is not reachable"
                )
            } else {
                this.messageHandler.send(advertisementMessage)
            }
        }
    }

    private fun handleMessage(
        advertisementMessage: AdvertisementMessage,
        message: String
    ) {
        if (this.ownAddress == advertisementMessage.address) {
            Log.d(TAG, "onMessage: received message for this device")
            this.observer.onMessage(advertisementMessage)
        } else {
            val nextTarget: String? =
                this.neighbourTable.getClosestNeighbour(advertisementMessage.address as String)
            if (nextTarget == null) {
                Log.w(
                    TAG,
                    "onMessage: cant forward message: $message ${advertisementMessage.address} is not reachable"
                )
            } else {
                this.messageHandler.send(advertisementMessage)
            }

        }
    }

    private fun handleNeighbour(
        advertisementMessage: AdvertisementMessage,
        rssi: Int,
        timeStamp: Long,
        closestNeighbour: Neighbour?
    ) {
        advertisementMessage.decrementHop()
        val neighbour: Neighbour = Neighbour(
            advertisementMessage.address!!,
            advertisementMessage.rssi ?: rssi,
            MAX_HOPS - advertisementMessage.hops!!,
            timeStamp,
            closestNeighbour
        )
        this.neighbourTable.updateNeighbour(neighbour)
    }

    companion object {
        const val MAX_HOPS: Int = 10
        const val TIMEOUT: Long = 5000L
    }


}