package de.hsos.nearbychat

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.service.bluetooth.util.Neighbour
import de.hsos.nearbychat.service.bluetooth.util.NeighbourTable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NeighbourTableTest {
    @Test
    fun getClosestNeighbourZeroHop() {
        val neighbourTable: NeighbourTable = NeighbourTable(5000L)
        val eins: Neighbour = Neighbour("eins", -50, 10, 500L)
        eins.closestNeighbour = eins
        neighbourTable.updateNeighbour(eins)
        val expected: String? = neighbourTable.getClosestNeighbour("eins")
        assertEquals(eins.address, expected)
    }

    @Test
    fun getClosestNeighbourOneHop() {
        val neighbourTable: NeighbourTable = NeighbourTable(5000L)
        val eins: Neighbour = Neighbour("eins", -50, 10, 500L)
        val zwei: Neighbour = Neighbour("zwei", -50, 10, 500L)
        zwei.closestNeighbour = zwei
        neighbourTable.updateNeighbour(zwei)
        eins.closestNeighbour = zwei
        neighbourTable.updateNeighbour(eins)
        val actual: String? = neighbourTable.getClosestNeighbour("eins")
        assertEquals(zwei.address, actual)
    }

    @Test
    fun replaceOneHopWithZeroHop() {
        val neighbourTable: NeighbourTable = NeighbourTable(5000L)
        var eins: Neighbour = Neighbour("eins", -50, 9, System.currentTimeMillis())
        val zwei: Neighbour = Neighbour("zwei", -50, 10, System.currentTimeMillis())
        zwei.closestNeighbour = zwei
        eins.closestNeighbour = zwei
        neighbourTable.updateNeighbour(zwei)
        neighbourTable.updateNeighbour(eins)
        eins = Neighbour("eins", -50, 10, System.currentTimeMillis())
        eins.closestNeighbour = eins
        neighbourTable.updateNeighbour(eins)
        val actual: String? = neighbourTable.getClosestNeighbour(eins.address)
        assertEquals(eins.address, actual)
    }

//    @Test
//    fun getNextElements(){
//        val neighbourTable: NeighbourTable = NeighbourTable(5000L)
//        val eins: Neighbour = Neighbour("eins",-50,9,System.currentTimeMillis())
//        neighbourTable.updateNeighbour(eins)
//        val zwei: Neighbour = Neighbour("zwei",-50,9,System.currentTimeMillis())
//        neighbourTable.updateNeighbour(zwei)
//        val drei: Neighbour = Neighbour("drei",-50,9,System.currentTimeMillis())
//        neighbourTable.updateNeighbour(drei)
//        val vier: Neighbour = Neighbour("vier",-50,9,System.currentTimeMillis())
//        neighbourTable.updateNeighbour(vier)
//        var result = neighbourTable.getNextElements(2)
//        assertEquals(eins, result[0])
//        assertEquals(zwei, result[1])
//        result = neighbourTable.getNextElements(2)
//        assertEquals(drei, result[0])
//        assertEquals(vier, result[1])
//        result = neighbourTable.getNextElements(2)
//        assertEquals(eins, result[0])
//        assertEquals(zwei, result[1])
//    }

    @Test
    fun getNextElements() {
        val neighbourTable: NeighbourTable = NeighbourTable(500000L)
        val eins: Neighbour = Neighbour("eins", -50, 9, System.currentTimeMillis())
        neighbourTable.updateNeighbour(eins)
        val zwei: Neighbour = Neighbour("zwei", -50, 9, System.currentTimeMillis())
        neighbourTable.updateNeighbour(zwei)
        val drei: Neighbour = Neighbour("drei", -50, 9, System.currentTimeMillis())
        neighbourTable.updateNeighbour(drei)
        val vier: Neighbour = Neighbour("vier", -50, 9, System.currentTimeMillis())
        neighbourTable.updateNeighbour(vier)
        var result = neighbourTable.getNextElement()
        assertEquals(eins, result)
        result = neighbourTable.getNextElement()
        assertEquals(zwei, result)
        result = neighbourTable.getNextElement()
        assertEquals(drei, result)
        result = neighbourTable.getNextElement()
        assertEquals(vier, result)
        result = neighbourTable.getNextElement()
        assertEquals(eins, result)
        result = neighbourTable.getNextElement()
        assertEquals(zwei, result)
    }

//    @Test
//    fun getNextElementsNoNeighbours() {
//        val neighbourTable: NeighbourTable = NeighbourTable()
//        val result = neighbourTable.getNextElements(50)
//        assertEquals(0, result.size)
//    }

    @Test
    fun getNextElementsEmptyTable() {
        val neighbourTable: NeighbourTable = NeighbourTable()
        val result = neighbourTable.getNextElement()
        assertNull(result)
    }

//    @Test
//    fun getNextElementsRequestMoreThanAvailable() {
//        val neighbourTable: NeighbourTable = NeighbourTable(5000L)
//        val eins: Neighbour = Neighbour("eins", -50, 9, System.currentTimeMillis())
//        neighbourTable.updateNeighbour(eins)
//        val zwei: Neighbour = Neighbour("zwei", -50, 9, System.currentTimeMillis())
//        neighbourTable.updateNeighbour(zwei)
//        var result = neighbourTable.getNextElements(4)
//        assertEquals(2, result.size)
//    }
//
//    @Test
//    fun getNextElementsGetAllStartingFromCenter() {
//        val neighbourTable: NeighbourTable = NeighbourTable(5000L)
//        val eins: Neighbour = Neighbour("eins", -50, 9, System.currentTimeMillis())
//        neighbourTable.updateNeighbour(eins)
//        val zwei: Neighbour = Neighbour("zwei", -50, 9, System.currentTimeMillis())
//        neighbourTable.updateNeighbour(zwei)
//        val drei: Neighbour = Neighbour("drei", -50, 9, System.currentTimeMillis())
//        neighbourTable.updateNeighbour(drei)
//        val vier: Neighbour = Neighbour("vier", -50, 9, System.currentTimeMillis())
//        neighbourTable.updateNeighbour(vier)
//        neighbourTable.getNextElements(1)
//        val result = neighbourTable.getNextElements(4)
//        assertEquals(4, result.size)
//        assertEquals(zwei, result[0])
//        assertEquals(drei, result[1])
//        assertEquals(vier, result[2])
//        assertEquals(eins, result[3])
//    }

    @Test
    fun getEntry(){
        val neighbourTable: NeighbourTable = NeighbourTable()
        val expected = Neighbour("eins",1,1,1)
        neighbourTable.updateNeighbour(expected)
        val result = neighbourTable.getEntry("eins")
        assertEquals(expected,result)
    }
}