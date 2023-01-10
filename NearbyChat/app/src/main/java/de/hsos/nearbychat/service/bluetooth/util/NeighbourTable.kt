package de.hsos.nearbychat.service.bluetooth.util

import android.util.Log
import de.hsos.nearbychat.service.bluetooth.advertise.AdvertisementQueue
import java.util.LinkedList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class NeighbourTable(private val timeout: Long = 5000L) : AdvertisementQueue {
    private val TAG: String = NeighbourTable::class.java.simpleName
//    private var innerTable: LinkedHashMap<String, Neighbour> = LinkedHashMap()
    private var neighbourList: MutableList<Neighbour> = mutableListOf()
    private val directNeighbourTable: HashMap<String, Neighbour> =
        HashMap() //TODO durch liste ersetzen

    //    private var firstAddressToAdvertise: String? = null
    private var firstAddressToAdvertise: Int = 0

//    fun updateNeighbour(neighbour: Neighbour) {
//        val entry: Neighbour? = this.innerTable[neighbour.address]
//        if (entry == null
//            || System.currentTimeMillis() - entry.lastSeen > this.timeout
//            || entry.hops < neighbour.hops
//            || entry.hops == neighbour.hops && entry.rssi > neighbour.rssi
//        ) {
//            this.innerTable[neighbour.address] = neighbour
//            if (this.firstAddressToAdvertise == null) {
//                this.firstAddressToAdvertise = neighbour.address
//            }
//        }
//    }

    fun updateNeighbour(neighbour: Neighbour) {
        val entry: Neighbour? =
            this.neighbourList.firstOrNull { item -> item.address == neighbour.address }
        if (entry == null) {
            this.neighbourList.add(neighbour)
        } else if (System.currentTimeMillis() - entry.lastSeen > this.timeout
            || entry.hops < neighbour.hops
            || entry.hops == neighbour.hops && entry.rssi > neighbour.rssi
        ) {
            this.neighbourList[this.neighbourList.indexOf(entry)] = neighbour
        }
    }
//
//        fun getClosestNeighbour(address: String): String? {
//        val entry: Neighbour? = this.innerTable[address]
//        return entry?.closestNeighbour?.address
//    }
    fun getClosestNeighbour(address: String): String? {
        val entry: Neighbour? = this.getEntry(address)
        return entry?.closestNeighbour?.address
    }


    fun getDirectNeighbour(macAddress: String): Neighbour? = this.directNeighbourTable[macAddress]
    fun getEntry(address: String): Neighbour? = this.neighbourList.firstOrNull { item -> item.address == address }

//    override fun getNextElement(): Neighbour? {
//        if (this.firstAddressToAdvertise == null) {
//            return null
//        } else {
//            var result: Neighbour? = null
//            for (entry in this.innerTable.entries) {
//                if (result != null) {
//                    this.firstAddressToAdvertise = entry.key
//                    break
//                }
//                if (entry.key == this.firstAddressToAdvertise) {
//                    result = entry.value
//                }
//            }
//            return result
//        }
//    }

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

    //    override fun getNextElements(amount: Int): List<Neighbour> {
//        val result: MutableList<Neighbour> = LinkedList()
//        if (this.firstAddressToAdvertise == null) {
//            return result
//        }
//        var found: Boolean = false
//        for ((k, v) in this.innerTable) {
//            if (k == firstAddressToAdvertise) {
//                found = true
//            }
//            if (result.size == amount) {
//                this.firstAddressToAdvertise = k
//                break
//            }
//            if (found && result.size < amount) {
//                result.add(v)
//            }
//        }
//        if (result.size < amount) {
//            for ((k, v) in this.innerTable) {
//                if (k == this.firstAddressToAdvertise) {
//                    break
//                }
//                if (result.size == amount) {
//                    this.firstAddressToAdvertise = k
//                    break
//                }
//                result.add(v)
//            }
//        } else if (result.first().address == this.firstAddressToAdvertise) {
//            this.firstAddressToAdvertise = this.innerTable.entries.first().key
//        }
//        return result

    override fun getSize(): Int = this.neighbourList.size

}