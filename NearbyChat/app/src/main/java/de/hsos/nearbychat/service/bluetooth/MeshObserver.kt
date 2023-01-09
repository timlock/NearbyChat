package de.hsos.nearbychat.service.bluetooth

import de.hsos.nearbychat.service.bluetooth.util.Advertisement

interface MeshObserver {
    fun onMessage(advertisement: Advertisement)
    fun onMessageAck(advertisement: Advertisement)
    fun onNeighbour(advertisement: Advertisement)
}
