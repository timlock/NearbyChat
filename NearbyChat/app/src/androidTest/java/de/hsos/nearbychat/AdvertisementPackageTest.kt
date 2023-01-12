package de.hsos.nearbychat
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.service.bluetooth.MessageType
import de.hsos.nearbychat.service.bluetooth.util.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdvertisementPackageTest {
    @Test
    fun toStringTest(){
        val expected: AdvertisementPackage = AdvertisementPackage()
        val idGenerator: AtomicIdGenerator = AtomicIdGenerator()
        expected.id = idGenerator.next()
        val advertisementNeighbour: Advertisement = Advertisement.Builder()
            .type(MessageType.NEIGHBOUR_MESSAGE.type)
            .sender("sender")
            .hops(10)
            .rssi(-50)
            .address("address")
            .name("name")
            .description("description")
            .color(255)
            .build()
        val advertisementAck: Advertisement = Advertisement.Builder()
            .type(MessageType.ACKNOWLEDGE_MESSAGE.type)
            .id('0')
            .nextHop("address")
            .sender("sender")
            .receiver("receiver")
            .timestamp(0L)
            .build()
        expected.addAdvertisement(advertisementNeighbour)
        expected.addAdvertisement(advertisementAck)
        val expectedToString = "${expected.id}:$advertisementNeighbour$advertisementAck"
        val actualString = expected.toString()
        assertEquals(expectedToString, actualString)
        val actualAdvertisementPackage = AdvertisementPackage.toPackage(actualString)
        assertEquals(expected, actualAdvertisementPackage)
    }

    @Test
    fun cutOffMessage(){
        val idGenerator: AtomicIdGenerator = AtomicIdGenerator()
        val advertisementPackage = AdvertisementPackage(idGenerator.next())
        var cutOffMessage = "test}"
        advertisementPackage.addCutMessageBegin(cutOffMessage)
        val advertisementAck: Advertisement = Advertisement.Builder()
            .type(MessageType.ACKNOWLEDGE_MESSAGE.type)
            .id('0')
            .nextHop("address")
            .sender("sender")
            .receiver("receiver")
            .timestamp(0L)
            .build()
        advertisementPackage.addAdvertisement(advertisementAck)
        cutOffMessage = "{test"
        advertisementPackage.addCutMessageEnd(cutOffMessage)
        var expected = "${advertisementPackage.id}:${advertisementPackage.getRawMessageBegin()}$advertisementAck${advertisementPackage.getRawMessageEnd()}"
        var actual = advertisementPackage.toString()
        assertEquals(expected,actual)
    }
}