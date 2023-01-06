package de.hsos.nearbychat.service.bluetooth

import de.hsos.nearbychat.service.bluetooth.util.AdvertisementMessage

interface MeshObserver {
    fun onMessage(advertisementMessage: AdvertisementMessage)
    fun onMessageAck(advertisementMessage: AdvertisementMessage)
}
