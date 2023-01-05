package de.hsos.nearbychat.service.bluetooth

interface ScannerObserver {
    fun onMessage(macAddress: String, message: String)
}
