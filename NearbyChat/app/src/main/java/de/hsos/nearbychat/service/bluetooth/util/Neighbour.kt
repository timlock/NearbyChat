package de.hsos.nearbychat.service.bluetooth.util

data class Neighbour(
    val address: String,
    var rssi: Int,
    val hops: Int,
    var lastSeen: Long,
    var closestNeighbour: Neighbour? = null
)