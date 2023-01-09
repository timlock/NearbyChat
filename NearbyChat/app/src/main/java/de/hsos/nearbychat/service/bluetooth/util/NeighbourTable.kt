package de.hsos.nearbychat.service.bluetooth.util

class NeighbourTable(private val timeout: Long) {
    private val TAG: String = NeighbourTable::class.java.simpleName
    private val innerTable: HashMap<String, Neighbour> = HashMap()
    private val directNeighbourTable: HashMap<String, Neighbour> = HashMap()

    fun updateNeighbour(neighbour: Neighbour) {
        val entry: Neighbour? = this.innerTable[neighbour.address]
        if (entry == null) {
            this.innerTable[neighbour.address] = neighbour
        } else {
            if (System.currentTimeMillis() - entry.lastSeen > this.timeout
                || entry.hops > neighbour.hops
                || entry.hops == neighbour.hops && entry.rssi > neighbour.rssi
            ) {
                this.innerTable[neighbour.address] = neighbour
            }
        }
    }

    fun getClosestNeighbour(address: String): String? {
        val entry: Neighbour? = this.innerTable[address]
        return entry?.closestNeighbour?.address
    }

    fun getDirectNeighbour(macAddress: String): Neighbour? = this.directNeighbourTable[macAddress]
    fun getEntry(address: String) : Neighbour? = this.innerTable[address]

}