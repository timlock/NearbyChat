package de.hsos.nearbychat.service.bluetooth

interface ScannerObserver {
    fun onMessage(macAddress: String, rssi: Int, message: String)
}
