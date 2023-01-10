package de.hsos.nearbychat.service.bluetooth.advertise

import de.hsos.nearbychat.service.bluetooth.util.Neighbour

interface AdvertisementQueue {
    fun getNextElement(): Neighbour?
//    fun getNextElements(amount: Int): List<Neighbour>
    fun getSize(): Int
}
