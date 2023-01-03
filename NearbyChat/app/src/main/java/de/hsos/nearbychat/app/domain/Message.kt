package de.hsos.nearbychat.app.domain

data class Message(val senderMAC: String, val receiverMAC: String, val content: String, val timeStamp: String) {
    var isReceived: Boolean = false
}