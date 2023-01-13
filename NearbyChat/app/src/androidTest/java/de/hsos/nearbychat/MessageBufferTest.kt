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
        var id = idGenerator.next()
        val messageBuffer: MessageBuffer = MessageBuffer()
        var first = "{12"
        var second = "34"
        var third = "5}"
        var actual = messageBuffer.add(first,id,first)
        assertNull(actual)
        id = idGenerator.next()
        id = idGenerator.next()
        actual = messageBuffer.add(first,id,second)
        assertNull(actual)
        id = idGenerator.next()
        id = idGenerator.next()
        actual = messageBuffer.add(first,id,third)
        assertEquals("$first$second$third", actual)
    }

}