package de.hsos.nearbychat.service.bluetooth.scan

interface ScannerObserver {
    fun onPackage(rssi: Int, packageString: String)
}
