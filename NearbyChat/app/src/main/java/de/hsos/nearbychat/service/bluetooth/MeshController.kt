package de.hsos.nearbychat.service.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertisingSetParameters
import android.util.Log
import de.hsos.nearbychat.service.bluetooth.util.MessageHandler

class MeshController(private var bluetoothAdapter: BluetoothAdapter) : ScannerObserver {
    private val TAG: String = MeshController::class.java.simpleName
    private var advertiser: BluetoothAdvertiser
    private var scanner: BluetoothScanner
    private var messageHandler: MessageHandler

    init {
        this.advertiser = BluetoothAdvertiser(this.bluetoothAdapter, AdvertisingSetParameters.INTERVAL_MEDIUM)
        this.scanner = BluetoothScanner(this, this.bluetoothAdapter.bluetoothLeScanner)
        this.messageHandler = MessageHandler(
            this.advertiser,
            AdvertisingSetParameters.INTERVAL_MEDIUM.toLong(),
            4,
            this.advertiser.maxMessageLength
        )
    }

    fun startScan() {
        Log.d(TAG, "startScan: ")
        this.scanner.start()
    }

    fun startAdvertise() {
        Log.d(TAG, "startAdvertise: ")
        this.advertiser.start("111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        this.messageHandler.start()
    }
    fun stopAdvertising(){
        Log.d(TAG, "stopAdvertising: ")
        this.messageHandler.stop()
        this.advertiser.stop()
    }

    fun sendMessage(message: String) {
        this.advertiser.send(message)
    }

    override fun onMessage(macAddress: String, message: String) {
        Log.d(TAG, "onMessage: ")
    }


}