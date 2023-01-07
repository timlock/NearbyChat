package de.hsos.nearbychat.service.bluetooth

interface ScannerObserver {
    fun onPackage(macAddress: String, rssi: Int, advertisementPackage: String)
}
