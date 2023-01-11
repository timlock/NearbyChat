package de.hsos.nearbychat.service.bluetooth.util

import de.hsos.nearbychat.service.bluetooth.advertise.AdvertisementQueue

class NeighbourTable(private val timeout: Long = 5000L) : AdvertisementQueue {
    private val TAG: String = NeighbourTable::class.java.simpleName
    private var neighbourList: MutableList<Neighbour> = mutableListOf()
    private val directNeighbourTable: HashMap<String, Neighbour> =
        HashMap()
    private var firstAddressToAdvertise: Int = 0


    fun updateNeighbour(neighbour: Neighbour) {
        val entry: Neighbour? =
            this.neighbourList.firstOrNull { item -> item.address == neighbour.address }
        if (entry == null) {
            this.neighbourList.add(neighbour)
        } else if ((System.currentTimeMillis() - entry.lastSeen > this.timeout
                    && entry.lastSeen != 0L)
            || entry.hops < neighbour.hops
            || entry.hops == neighbour.hops && entry.rssi > neighbour.rssi
        ) {
            this.neighbourList[this.neighbourList.indexOf(entry)] = neighbour
        }
    }

    fun getClosestNeighbour(address: String): String? {
        val entry: Neighbour? = this.getEntry(address)
        return entry?.closestNeighbour?.address
    }


    fun getDirectNeighbour(macAddress: String): Neighbour? = this.directNeighbourTable[macAddress]
    fun getEntry(address: String): Neighbour? =
        this.neighbourList.firstOrNull { item -> item.address == address }

    @Synchronized
    override fun getNextElement(): Neighbour? {
        return if (this.neighbourList.isEmpty()) {
            null
        } else {
            var result: Neighbour = this.neighbourList[this.firstAddressToAdvertise]
            var counter: Int = this.neighbourList.size
            while (System.currentTimeMillis() - result.lastSeen > this.timeout && counter > 0) {
                this.firstAddressToAdvertise =
                    (this.firstAddressToAdvertise + 1) % this.neighbourList.size
                result = this.neighbourList[firstAddressToAdvertise]
                counter--
            }
            this.firstAddressToAdvertise =
                (this.firstAddressToAdvertise + 1) % this.neighbourList.size
            result
        }
    }

    override fun getSize(): Int = this.neighbourList.size

    @Synchronized
    fun removeNeighboursWithTimeout(): List<String> {
        val timeoutList =
            this.neighbourList.filter { n -> System.currentTimeMillis() - n.lastSeen > this.timeout }
        this.neighbourList.removeAll(timeoutList)
        return timeoutList.map { it.address }
    }

}