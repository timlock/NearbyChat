package de.hsos.nearbychat.service.bluetooth.advertise

fun interface Advertiser {
    fun send(message: String): Boolean

}
