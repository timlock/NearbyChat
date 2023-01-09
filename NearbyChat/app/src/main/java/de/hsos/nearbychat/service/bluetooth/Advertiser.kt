package de.hsos.nearbychat.service.bluetooth

interface Advertiser {
    fun start() : Boolean
    fun stop()
    fun getMaxMessageSize(): Int
    fun send(message: String): Boolean
}
