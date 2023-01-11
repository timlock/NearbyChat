package de.hsos.nearbychat.service.bluetooth

enum class MessageType(var type: Char) {
    NEIGHBOUR_MESSAGE('N'), MESSAGE_MESSAGE('M'), ACKNOWLEDGE_MESSAGE('A')
}