package de.hsos.nearbychat.service.bluetooth

enum class MessageType(val type: Char) {
    NEIGHBOUR_MESSAGE('N'), MESSAGE_MESSAGE('M'), ACKNOWLEDGE_MESSAGE('A')
}