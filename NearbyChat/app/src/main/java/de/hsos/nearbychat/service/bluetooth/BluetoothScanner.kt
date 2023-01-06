package de.hsos.nearbychat.service.bluetooth

import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import android.util.Log
import java.util.*

class BluetoothScanner(private var observer : ScannerObserver, private var bluetoothLeScanner: BluetoothLeScanner) : ScanCallback() {
    private val TAG: String = BluetoothScanner::class.java.simpleName
    private var scanning = false
    private var advertiseUUID: ParcelUuid =
        ParcelUuid(UUID.fromString("e889813c-5d19-49e2-8bc4-d4596b4f5250"))
    private var scanSettings: ScanSettings
    private var scanFilters: MutableList<ScanFilter>

    init {
        this.scanSettings = ScanSettings.Builder()
            .setLegacy(false)
            .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
            .build()
        val scanFilter: ScanFilter = ScanFilter.Builder()
            .setServiceUuid(advertiseUUID)
            .build()
        this.scanFilters = mutableListOf(scanFilter)
    }

    fun start(): Boolean {
        Log.d(TAG, "startScan: ")
        if (scanning) {
            this.stop()
        }
        if (!this.scanning) {
            return try {
                this.bluetoothLeScanner.startScan(this.scanFilters,this.scanSettings, this)
                this.scanning = true
                true
            } catch (e: SecurityException) {
                Log.w(TAG, "startScan: ", e)
                false
            }
        }
        return false
    }

    fun stop(): Boolean {
        Log.d(TAG, "stopScan: ")
        if (this.scanning) {
            return try {
                this.bluetoothLeScanner.stopScan(this)
                this.scanning = false
                true
            } catch (e: SecurityException) {
                Log.w(TAG, "startScan: ", e)
                false
            }
        }
        return false
    }

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)
        try {
            var stringLog: String = result.device.name + " " + result.device.address + " "
            result.scanRecord?.serviceData?.forEach { (uuid, data) -> stringLog += " " + uuid.toString() + " " + data.decodeToString() }
            Log.i(TAG, "onScanResult: $stringLog")
            val message: String? = result.scanRecord?.serviceData?.get(this.advertiseUUID)?.decodeToString()
            this.observer.onMessage(result.device.address, result.rssi,message ?: "")
        } catch (e: SecurityException) {
            Log.w(TAG, "startScan: ", e)
        }
    }
}