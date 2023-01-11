package de.hsos.nearbychat
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.service.bluetooth.util.AtomicIdGenerator
import de.hsos.nearbychat.service.bluetooth.util.SlidingWindow
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SlidingWindowTest {
    @Test
    fun add() {
        val slidingWindow: SlidingWindow = SlidingWindow(2)
        val result: Boolean = slidingWindow.add('0')
        assertTrue(result)
    }

    @Test
    fun idOutOfRange() {
        val slidingWindow: SlidingWindow = SlidingWindow(2)
        slidingWindow.add('0')
        val result: Boolean = slidingWindow.add('a')
        assertFalse(result)
    }

    @Test
    fun invalidID() {
        var slidingWindow: SlidingWindow = SlidingWindow(2)
        val result: Boolean = slidingWindow.add('!')
        assertFalse(result)
    }
    @Test
    fun initialID(){
        var slidingWindow: SlidingWindow = SlidingWindow(2)
        val result: Boolean = slidingWindow.add('A')
        assertTrue(result)
    }

    @Test
    fun oldestMessageGetsRemoved() {
        var slidingWindow: SlidingWindow = SlidingWindow(2)
        slidingWindow.add('0')
        slidingWindow.add('1')
        slidingWindow.add('2')
        assertEquals('1', slidingWindow.getOldestID())
    }

    @Test
    fun sameIDTwice() {
        var slidingWindow: SlidingWindow = SlidingWindow(2)
        slidingWindow.add('0')
        val result: Boolean = slidingWindow.add('0')
        assertFalse(result)
    }

    @Test
    fun oldestIDReset() {
        var slidingWindow: SlidingWindow = SlidingWindow(5)
        var idGenerator = AtomicIdGenerator()
        var id: Char = '0'
        while (id != 'z') {
            id = idGenerator.next()
            assertTrue(slidingWindow.add(id))
        }
        val result: Boolean = slidingWindow.add('0')
        assertTrue(result)
    }

    @Test
    fun addIDsInMixedOrder(){
        var slidingWindow: SlidingWindow = SlidingWindow(5)
        slidingWindow.add('1')
        slidingWindow.add('0')
        slidingWindow.add('4')
        assertEquals('1',slidingWindow.getOldestID())
    }
}