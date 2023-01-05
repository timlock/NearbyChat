package de.hsos.nearbychat.service.bluetooth.util

interface Broadcaster {
    fun send(message: String): Boolean

}
