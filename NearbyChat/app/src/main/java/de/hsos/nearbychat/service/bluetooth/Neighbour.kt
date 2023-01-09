package de.hsos.nearbychat.service.bluetooth

data class Neighbour(
    val address: String,
    var rssi: Int,
    val hops: Int,
    var lastSeen: Long,
    var closestNeighbour: Neighbour? = null
)