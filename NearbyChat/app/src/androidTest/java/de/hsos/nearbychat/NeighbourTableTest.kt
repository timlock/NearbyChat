package de.hsos.nearbychat
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.service.bluetooth.util.Neighbour
import de.hsos.nearbychat.service.bluetooth.util.NeighbourTable
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NeighbourTableTest {
    @Test
    fun getClosestNeighbourZeroHop() {
        var neighbourTable: NeighbourTable = NeighbourTable(5000L)
        var eins: Neighbour = Neighbour("eins",-50,10,500L)
        eins.closestNeighbour = eins
        neighbourTable.updateNeighbour(eins)
        val expected : String? = neighbourTable.getClosestNeighbour("eins")
        assertEquals(eins.address, expected)
    }

    @Test
    fun getClosestNeighbourOneHop() {
        var neighbourTable: NeighbourTable = NeighbourTable(5000L)
        var eins: Neighbour = Neighbour("eins",-50,10,500L)
        var zwei: Neighbour = Neighbour("zwei",-50,10,500L)
        zwei.closestNeighbour = zwei
        neighbourTable.updateNeighbour(zwei)
        eins.closestNeighbour = zwei
        neighbourTable.updateNeighbour(eins)
        val actual : String? = neighbourTable.getClosestNeighbour("eins")
        assertEquals(zwei.address, actual)
    }
    @Test
    fun replaceOneHopWithZeroHop(){
        var neighbourTable: NeighbourTable = NeighbourTable(5000L)
        var eins: Neighbour = Neighbour("eins",-50,9,System.currentTimeMillis())
        var zwei: Neighbour = Neighbour("zwei",-50,10,System.currentTimeMillis())
        zwei.closestNeighbour = zwei
        eins.closestNeighbour = zwei
        neighbourTable.updateNeighbour(zwei)
        neighbourTable.updateNeighbour(eins)
        eins = Neighbour("eins",-50,10,System.currentTimeMillis())
        eins.closestNeighbour = eins
        neighbourTable.updateNeighbour(eins)
        val actual: String? = neighbourTable.getClosestNeighbour(eins.address)
        assertEquals(eins.address,actual)

    }
}