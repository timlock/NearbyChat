package de.hsos.nearbychat.service.bluetooth.util

import android.util.Log
import de.hsos.nearbychat.service.bluetooth.MeshController
import de.hsos.nearbychat.service.bluetooth.advertise.AdvertisementQueue

class NeighbourTable(private val timeout: Long = 5000L) : AdvertisementQueue<Neighbour> {
    private val TAG: String = NeighbourTable::class.java.simpleName
    private var neighbourList: MutableList<Neighbour> = mutableListOf()
    private var firstAddressToAdvertise: Int = 0

    @Synchronized
    fun updateNeighbour(neighbour: Neighbour) {
        val entry: Neighbour? = this.neighbourList.firstOrNull { it.address == neighbour.address }
        if (entry == null) {
            Log.d(TAG, "updateNeighbour() discovered new neighbour = $neighbour")
            this.neighbourList.add(neighbour)
        } else if ((System.currentTimeMillis() - entry.lastSeen > this.timeout
                    && entry.lastSeen != 0L)
            || neighbour.hops == MeshController.MAX_HOPS
            || entry.hops < neighbour.hops
            || entry.hops == neighbour.hops && entry.rssi > neighbour.rssi
        ) {
            Log.d(TAG, "updateNeighbour() updated neighbour = $neighbour")
            this.neighbourList[this.neighbourList.indexOf(entry)] = neighbour
        }
    }

    fun getClosestNeighbour(address: String): String? {
        val entry: Neighbour? = this.getEntry(address)
        return entry?.closestNeighbour?.address
    }

    fun getEntry(address: String): Neighbour? =
        this.neighbourList.firstOrNull { item -> item.address == address }

    @Synchronized
    override fun getNextElement(): Neighbour? {
        return if (this.neighbourList.isEmpty()) {
            null
        } else {
            var result: Neighbour = this.neighbourList[this.firstAddressToAdvertise]
            this.firstAddressToAdvertise = (this.firstAddressToAdvertise + 1) % this.neighbourList.size
            result
        }
    }

    @Synchronized
    override fun getSize(): Int = this.neighbourList.size

    @Synchronized
    fun removeNeighboursWithTimeout(): List<String> {
        val timeoutList =
            this.neighbourList.filter { n -> System.currentTimeMillis() - n.lastSeen > this.timeout && n.lastSeen != 0L }
        this.neighbourList.removeAll(timeoutList)
        if (this.firstAddressToAdvertise >= this.neighbourList.size) {
            this.firstAddressToAdvertise = 0
        }
        return timeoutList.map { it.address }
    }

}