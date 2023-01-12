package de.hsos.nearbychat
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.*
import de.hsos.nearbychat.service.bluetooth.scan.ScannerObserver
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import de.hsos.nearbychat.service.bluetooth.util.AdvertisementPackage
import de.hsos.nearbychat.service.bluetooth.util.AtomicIdGenerator
import de.hsos.nearbychat.service.bluetooth.util.MessageBuffer
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessageBufferTest {
    @Test
    fun add(){
        val idGenerator = AtomicIdGenerator()
        val messageBuffer: MessageBuffer = MessageBuffer()
        var first = "{12"
        var second = "34"
        var third = "5}"
        var actual = messageBuffer.add(idGenerator.next(),first)
        assertNull(actual)
        actual = messageBuffer.add(idGenerator.next(),second)
        assertNull(actual)
        actual = messageBuffer.add(idGenerator.next(),third)
        assertEquals("$first$second$third", actual)
    }

    @Test
    fun addReverseOrder(){
        val idGenerator = AtomicIdGenerator()
        val messageBuffer: MessageBuffer = MessageBuffer()
        var first = "{12"
        var second = "34"
        var third = "5}"
        var actual = messageBuffer.add('2',third)
        assertNull(actual)
        actual = messageBuffer.add('1',second)
        assertNull(actual)
        actual = messageBuffer.add('0',first)
        assertEquals("$first$second$third", actual)
    }

    @Test
    fun addMixedOrder(){
        val idGenerator = AtomicIdGenerator()
        val messageBuffer: MessageBuffer = MessageBuffer()
        var first = "{12"
        var second = "34"
        var third = "56"
        var fourth = "7}"
        var actual = messageBuffer.add('0',first)
        assertNull(actual)
        actual = messageBuffer.add('3',fourth)
        assertNull(actual)
        actual = messageBuffer.add('2',third)
        assertNull(actual)
        actual = messageBuffer.add('1',second)
        assertEquals("$first$second$third$fourth", actual)
    }
}