package de.hsos.nearbychat
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.service.bluetooth.util.AtomicIdGenerator
import de.hsos.nearbychat.service.bluetooth.util.SlidingWindowTable
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SlidingWindowTableTest {
    @Test
    fun add() {
        val slidingWindowTable: SlidingWindowTable = SlidingWindowTable(2)
        val result: Boolean = slidingWindowTable.add("a",'0')
        assertTrue(result)
    }

    @Test
    fun idOutOfRange() {
        val slidingWindowTable: SlidingWindowTable = SlidingWindowTable(2)
        slidingWindowTable.add("a",'0')
        val result: Boolean = slidingWindowTable.add("a",'a')
        assertFalse(result)
    }

    @Test
    fun invalidID() {
        var slidingWindowTable: SlidingWindowTable = SlidingWindowTable(2)
        val result: Boolean = slidingWindowTable.add("a",'!')
        assertFalse(result)
    }
    @Test
    fun initialID(){
        var slidingWindowTable: SlidingWindowTable = SlidingWindowTable(2)
        val result: Boolean = slidingWindowTable.add("a",'A')
        assertTrue(result)
    }

    @Test
    fun oldestMessageGetsRemoved() {
        var slidingWindowTable: SlidingWindowTable = SlidingWindowTable(2)
        slidingWindowTable.add("a",'0')
        slidingWindowTable.add("a",'1')
        slidingWindowTable.add("a",'2')
        assertEquals('1', slidingWindowTable.getOldestID("a"))
    }

    @Test
    fun sameIDTwice() {
        var slidingWindowTable: SlidingWindowTable = SlidingWindowTable(2)
        slidingWindowTable.add("a",'0')
        val result: Boolean = slidingWindowTable.add("a",'0')
        assertFalse(result)
    }

    @Test
    fun oldestIDReset() {
        var slidingWindowTable: SlidingWindowTable = SlidingWindowTable(5)
        var idGenerator = AtomicIdGenerator()
        var id: Char = '0'
        while (id != 'z') {
            id = idGenerator.next()
            assertTrue(slidingWindowTable.add("a",id))
        }
        val result: Boolean = slidingWindowTable.add("a",'0')
        assertTrue(result)
    }

    @Test
    fun addIDsInMixedOrder(){
        var slidingWindowTable: SlidingWindowTable = SlidingWindowTable(5)
        slidingWindowTable.add("a",'1')
        slidingWindowTable.add("a",'0')
        slidingWindowTable.add("a",'4')
        assertEquals('1',slidingWindowTable.getOldestID("a"))
    }
}