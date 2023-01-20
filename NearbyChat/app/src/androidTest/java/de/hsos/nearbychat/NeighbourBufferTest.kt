package de.hsos.nearbychat

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.service.bluetooth.AdvertisementType
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import de.hsos.nearbychat.service.bluetooth.util.Neighbour
import de.hsos.nearbychat.service.bluetooth.util.NeighbourBuffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NeighbourBufferTest {
    @Test
    fun getClosestNeighbourZeroHop() {
        val neighbourBuffer: NeighbourBuffer = NeighbourBuffer(5000L)
        val eins: Neighbour = Neighbour("eins", -50, 10, 500L)
        eins.closestNeighbour = eins
        neighbourBuffer.updateNeighbour(eins)
        val expected: String? = neighbourBuffer.getClosestNeighbour("eins")
        assertEquals(eins.address, expected)
    }

    @Test
    fun getClosestNeighbourOneHop() {
        val neighbourBuffer: NeighbourBuffer = NeighbourBuffer(5000L)
        val eins: Neighbour = Neighbour("eins", -50, 10, 500L)
        val zwei: Neighbour = Neighbour("zwei", -50, 10, 500L)
        zwei.closestNeighbour = zwei
        neighbourBuffer.updateNeighbour(zwei)
        eins.closestNeighbour = zwei
        neighbourBuffer.updateNeighbour(eins)
        val actual: String? = neighbourBuffer.getClosestNeighbour("eins")
        assertEquals(zwei.address, actual)
    }

    @Test
    fun replaceOneHopWithZeroHop() {
        val neighbourBuffer: NeighbourBuffer = NeighbourBuffer(5000L)
        var eins: Neighbour = Neighbour("eins", -50, 9, System.currentTimeMillis())
        val zwei: Neighbour = Neighbour("zwei", -50, 10, System.currentTimeMillis())
        zwei.closestNeighbour = zwei
        eins.closestNeighbour = zwei
        neighbourBuffer.updateNeighbour(zwei)
        neighbourBuffer.updateNeighbour(eins)
        eins = Neighbour("eins", -50, 10, System.currentTimeMillis())
        eins.closestNeighbour = eins
        neighbourBuffer.updateNeighbour(eins)
        val actual: String? = neighbourBuffer.getClosestNeighbour(eins.address)
        assertEquals(eins.address, actual)
    }


    @Test
    fun getNextElements() {
        val neighbourBuffer: NeighbourBuffer = NeighbourBuffer(500000L)
        val eins: Neighbour = Neighbour("eins", -50, 9, System.currentTimeMillis())
        neighbourBuffer.updateNeighbour(eins)
        val zwei: Neighbour = Neighbour("zwei", -50, 9, System.currentTimeMillis())
        neighbourBuffer.updateNeighbour(zwei)
        val drei: Neighbour = Neighbour("drei", -50, 9, System.currentTimeMillis())
        neighbourBuffer.updateNeighbour(drei)
        val vier: Neighbour = Neighbour("vier", -50, 9, System.currentTimeMillis())
        neighbourBuffer.updateNeighbour(vier)
        var result = neighbourBuffer.getNextElement()
        assertEquals(eins, result)
        result = neighbourBuffer.getNextElement()
        assertEquals(zwei, result)
        result = neighbourBuffer.getNextElement()
        assertEquals(drei, result)
        result = neighbourBuffer.getNextElement()
        assertEquals(vier, result)
        result = neighbourBuffer.getNextElement()
        assertEquals(eins, result)
        result = neighbourBuffer.getNextElement()
        assertEquals(zwei, result)
    }

    @Test
    fun tmp(){
        var first = Neighbour("eins", 1, 1, 0L, advertisement = Advertisement.Builder()
            .type(AdvertisementType.NEIGHBOUR_ADVERTISEMENT.type)
            .rssi(1)
            .hops(10)
            .sender("eins")
            .address("eins")
            .name("eins")
            .description("eins")
            .color(1)
            .build())
        var second = Neighbour("zwei", 1, 9, System.currentTimeMillis(), advertisement = Advertisement.Builder()
            .type(AdvertisementType.NEIGHBOUR_ADVERTISEMENT.type)
            .rssi(1)
            .hops(9)
            .sender("eins")
            .address("zwei")
            .name("zwei")
            .description("zwei")
            .color(1)
            .build())
        val neighbourBuffer: NeighbourBuffer = NeighbourBuffer(5000L)
        neighbourBuffer.updateNeighbour(first)
        neighbourBuffer.updateNeighbour(second)
        var restult : Neighbour? = neighbourBuffer.getNextElement()
        assertEquals(first.toString(), restult.toString())
        restult = neighbourBuffer.getNextElement()
        assertEquals(second.toString(),restult.toString())
    }


    @Test
    fun getNextElementsEmptyTable() {
        val neighbourBuffer: NeighbourBuffer = NeighbourBuffer()
        val result = neighbourBuffer.getNextElement()
        assertNull(result)
    }


    @Test
    fun getEntry(){
        val neighbourBuffer: NeighbourBuffer = NeighbourBuffer()
        val expected = Neighbour("eins",1,1,1)
        neighbourBuffer.updateNeighbour(expected)
        val result = neighbourBuffer.getEntry("eins")
        assertEquals(expected,result)
    }

    @Test
    fun removeNeighboursWithTimeout(){
        val neighbourBuffer: NeighbourBuffer = NeighbourBuffer()
        val expected = Neighbour("eins",1,1,1)
        val noTimeout = Neighbour("zwei",1,1,System.currentTimeMillis())
        neighbourBuffer.updateNeighbour(expected)
        neighbourBuffer.updateNeighbour(noTimeout)
        val result = neighbourBuffer.removeNeighboursWithTimeout()
        assertEquals(1, result.size)
        assertEquals(expected.address,result[0])
    }
}