package de.hsos.nearbychat
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.service.bluetooth.util.AtomicIdGenerator
import de.hsos.nearbychat.service.bluetooth.util.SlidingWindowTable
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SlidingWindowTest {
    @Test
    fun add() {
        val slidingWindow: SlidingWindowTable = SlidingWindowTable(2)
        val result: Boolean = slidingWindow.add("a",'0')
        assertTrue(result)
    }

    @Test
    fun idOutOfRange() {
        val slidingWindow: SlidingWindowTable = SlidingWindowTable(2)
        slidingWindow.add("a", '0')
        val result: Boolean = slidingWindow.add("a", 'a')
        assertFalse(result)
    }

    @Test
    fun invalidID() {
        var slidingWindow: SlidingWindowTable = SlidingWindowTable(2)
        val result: Boolean = slidingWindow.add("a", '!')
        assertFalse(result)
    }
    @Test
    fun initialID(){
        var slidingWindow: SlidingWindowTable = SlidingWindowTable(2)
        val result: Boolean = slidingWindow.add("a", 'A')
        assertTrue(result)
    }

    @Test
    fun oldestMessageGetsRemoved() {
        var slidingWindow: SlidingWindowTable = SlidingWindowTable(2)
        slidingWindow.add("a", '0')
        slidingWindow.add("a", '1')
        slidingWindow.add("a", '2')
        assertEquals('1', slidingWindow.getOldestID("a"))
    }

    @Test
    fun sameIDTwice() {
        var slidingWindow: SlidingWindowTable = SlidingWindowTable(2)
        slidingWindow.add("a", '0')
        val result: Boolean = slidingWindow.add("a", '0')
        assertFalse(result)
    }

    @Test
    fun oldestIDReset() {
        var slidingWindow: SlidingWindowTable = SlidingWindowTable(5)
        var idGenerator = AtomicIdGenerator()
        var id: Char = '0'
        while (id != 'z') {
            id = idGenerator.next()
            assertTrue(slidingWindow.add("a", id))
        }
        val result: Boolean = slidingWindow.add("a", '0')
        assertTrue(result)
    }

    @Test
    fun addIDsInMixedOrder(){
        var slidingWindow: SlidingWindowTable = SlidingWindowTable(5)
        slidingWindow.add("a", '1')
        slidingWindow.add("a", '0')
        slidingWindow.add("a", '4')
        assertEquals('1',slidingWindow.getOldestID("a"))
    }
}