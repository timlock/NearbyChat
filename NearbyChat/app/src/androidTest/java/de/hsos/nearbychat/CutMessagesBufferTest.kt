package de.hsos.nearbychat
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.service.bluetooth.util.AtomicIdGenerator
import de.hsos.nearbychat.service.bluetooth.util.CutMessagesBuffer
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CutMessagesBufferTest {
    @Test
    fun add(){
        val idGenerator = AtomicIdGenerator()
        var id = idGenerator.next()
        val cutMessagesBuffer: CutMessagesBuffer = CutMessagesBuffer()
        var first = "{12"
        var second = "34"
        var third = "5}"
        var actual = cutMessagesBuffer.add(first,id,first)
        assertNull(actual)
        id = idGenerator.next()
        id = idGenerator.next()
        actual = cutMessagesBuffer.add(first,id,second)
        assertNull(actual)
        id = idGenerator.next()
        id = idGenerator.next()
        actual = cutMessagesBuffer.add(first,id,third)
        assertEquals("$first$second$third", actual)
    }

}