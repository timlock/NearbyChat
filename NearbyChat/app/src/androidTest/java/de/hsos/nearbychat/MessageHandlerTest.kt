package de.hsos.nearbychat


import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.hsos.nearbychat.service.bluetooth.MessageType
import de.hsos.nearbychat.service.bluetooth.advertise.MessageHandler
import de.hsos.nearbychat.service.bluetooth.util.AdvertisementMessage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessageHandlerTest {

    @Test
    fun send() {
        // Context of the app under test.
        var actual: String = ""
        var expected : String ="test"
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val messageHandler: MessageHandler = MessageHandler(
            { message ->
                actual = message.substring(2)
                true
            },
            1000L,
            10
        )
        messageHandler.start()
        messageHandler.send(expected)
        Thread.sleep(messageHandler.period * 2)
        assertEquals(expected,actual)
    }
    @Test
    fun messageCutOff() {
        // Context of the app under test.
        var actual: String = ""
        var expected : String ="test"
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val messageHandler: MessageHandler = MessageHandler(
            { message ->
                actual += message.substring(2)
                Log.d("Test", "messageCutOff: $message")
                true
            },
            1000L,
            4
        )
        messageHandler.start()
        messageHandler.send(expected)
        Thread.sleep(messageHandler.period * 2)
        assertEquals(expected,actual)
    }
}