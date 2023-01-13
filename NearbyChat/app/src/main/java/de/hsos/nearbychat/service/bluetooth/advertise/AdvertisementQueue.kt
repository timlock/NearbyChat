package de.hsos.nearbychat.service.bluetooth.advertise

interface AdvertisementQueue<out T> {
    fun getNextElement(): T?
    fun getSize(): Int
}
