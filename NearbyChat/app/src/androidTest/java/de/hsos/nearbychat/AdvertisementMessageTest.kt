package de.hsos.nearbychat

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.hsos.nearbychat.service.bluetooth.MessageType
import de.hsos.nearbychat.service.bluetooth.util.AdvertisementMessage
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AdvertisementMessageTest {
    @Test
    fun message() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val advertisementMessage: AdvertisementMessage = AdvertisementMessage.Builder()
            .type(MessageType.MESSAGE_MESSAGE)
            .id('0')
            .address("address")
            .sender("sender")
            .receiver("receiver")
            .message("message")
            .build()
        var excepted: String = "{M:0;address;sender;receiver;message}"
        var actual: String = advertisementMessage.toString()
        assertEquals(excepted, actual)
        actual = AdvertisementMessage.Builder().rawMessage(excepted).build().toString()
        excepted = actual
        assertEquals(excepted, actual )
    }
    @Test
    fun ack() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val advertisementMessage: AdvertisementMessage = AdvertisementMessage.Builder()
            .type(MessageType.ACKNOWLEDGE_MESSAGE)
            .id('0')
            .address("address")
            .sender("sender")
            .receiver("receiver")
            .build()
        var excepted: String = "{A:0;address;sender;receiver}"
        var actual: String = advertisementMessage.toString()
        assertEquals(excepted, actual)
        actual = AdvertisementMessage.Builder().rawMessage(excepted).build().toString()
        excepted = actual
        assertEquals(excepted, actual )
    }

    @Test
    fun neighbour() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val advertisementMessage: AdvertisementMessage = AdvertisementMessage.Builder()
            .type(MessageType.NEIGHBOUR_MESSAGE)
            .hops(10)
            .rssi(-50)
            .address("address")
            .name("name")
            .description("description")
            .color(255)
            .build()
        var excepted: String = "{N:10;-50;address;name;description;255}"
        var actual: String = advertisementMessage.toString()
        assertEquals(excepted, actual)
        actual = AdvertisementMessage.Builder().rawMessage(excepted).build().toString()
        excepted = actual
        assertEquals(excepted, actual)
    }
}