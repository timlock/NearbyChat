package de.hsos.nearbychat.service.bluetooth

enum class AdvertisementType(var type: Char) {
    NEIGHBOUR_ADVERTISEMENT('N'), MESSAGE_ADVERTISEMENT('M'), ACKNOWLEDGE_ADVERTISEMENT('A')
}