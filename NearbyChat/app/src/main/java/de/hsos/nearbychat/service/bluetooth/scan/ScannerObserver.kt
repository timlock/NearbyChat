package de.hsos.nearbychat.service.bluetooth.scan

interface ScannerObserver {
    fun onPackage(macAddress: String,rssi: Int, packageString: String)
}
