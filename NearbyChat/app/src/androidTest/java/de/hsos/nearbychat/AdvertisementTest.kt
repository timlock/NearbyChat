package de.hsos.nearbychat

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.service.bluetooth.AdvertisementType
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AdvertisementTest {
    @Test
    fun message() {
        val advertisement: Advertisement = Advertisement.Builder()
            .type(AdvertisementType.MESSAGE_ADVERTISEMENT.type)
            .nextHop("nextHop")
            .sender("sender")
            .receiver("receiver")
            .message("message")
            .timestamp(1000L)
            .build()
        var excepted: String = "{M:nextHop;sender;receiver;1000;message}"
        var actual: String = advertisement.toString()
        assertEquals(excepted, actual)
        actual = Advertisement.Builder().rawMessage(excepted).build().toString()
        excepted = actual
        assertEquals(excepted, actual)
    }
    @Test
    fun ack() {
        val advertisement: Advertisement = Advertisement.Builder()
            .type(AdvertisementType.ACKNOWLEDGE_ADVERTISEMENT.type)
            .nextHop("address")
            .sender("sender")
            .receiver("receiver")
            .timestamp(0L)
            .build()
        var excepted: String = "{A:address;sender;receiver;0}"
        var actual: String = advertisement.toString()
        assertEquals(excepted, actual)
        actual = Advertisement.Builder().rawMessage(excepted).build().toString()
        excepted = actual
        assertEquals(excepted, actual )
    }

    @Test
    fun neighbour() {
        val advertisement: Advertisement = Advertisement.Builder()
            .type(AdvertisementType.NEIGHBOUR_ADVERTISEMENT.type)
            .sender("sender")
            .hops(10)
            .rssi(-50)
            .address("address")
            .name("name")
            .description("description")
            .color(255)
            .build()
        var excepted: String = "{N:sender;10;-50;address;name;description;255}"
        var actual: String = advertisement.toString()
        assertEquals(excepted, actual)
        actual = Advertisement.Builder().rawMessage(excepted).build().toString()
        excepted = actual
        assertEquals(excepted, actual)
    }
}