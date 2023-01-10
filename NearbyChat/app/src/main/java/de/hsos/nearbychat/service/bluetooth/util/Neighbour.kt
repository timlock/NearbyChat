package de.hsos.nearbychat.service.bluetooth.util

class Neighbour(
    val address: String,
    var rssi: Int,
    val hops: Int,
    var lastSeen: Long,
    var closestNeighbour: Neighbour? = null,
    var advertisement: Advertisement? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Neighbour) return false

        if (address != other.address) return false
        if (rssi != other.rssi) return false
        if (hops != other.hops) return false
        if (lastSeen != other.lastSeen) return false

        return true
    }

    override fun hashCode(): Int {
        var result = address.hashCode()
        result = 31 * result + rssi
        result = 31 * result + hops
        result = 31 * result + lastSeen.hashCode()
        return result
    }

    override fun toString(): String {
        return "Neighbour(address='$address', rssi=$rssi, hops=$hops, lastSeen=$lastSeen, closestNeighbour=$closestNeighbour, advertisement=$advertisement)"
    }


}