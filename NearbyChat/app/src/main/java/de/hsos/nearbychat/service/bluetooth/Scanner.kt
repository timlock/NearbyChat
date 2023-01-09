package de.hsos.nearbychat.service.bluetooth

import de.hsos.nearbychat.service.bluetooth.scan.ScannerObserver

interface Scanner {
    fun start(): Boolean
    fun stop():Boolean
    fun subscribe(observer: ScannerObserver)
}
