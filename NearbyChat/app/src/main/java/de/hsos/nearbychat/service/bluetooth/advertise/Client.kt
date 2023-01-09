package de.hsos.nearbychat.service.bluetooth.advertise

fun interface Client {
    fun send(message: String): Boolean

}
