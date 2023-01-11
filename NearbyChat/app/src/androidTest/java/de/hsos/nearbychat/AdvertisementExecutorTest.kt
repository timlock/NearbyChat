package de.hsos.nearbychat


import android.util.Log
import androidx.core.graphics.blue
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.service.bluetooth.Advertiser
import de.hsos.nearbychat.service.bluetooth.MessageType
import de.hsos.nearbychat.service.bluetooth.advertise.AdvertisementExecutor
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import de.hsos.nearbychat.service.bluetooth.util.Neighbour
import de.hsos.nearbychat.service.bluetooth.util.NeighbourTable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.LinkedList

@RunWith(AndroidJUnit4::class)
class AdvertisementExecutorTest {

    @Test
    fun send() {
        var actual: String = ""
        var expected: String = "test"
        val advertisementExecutor: AdvertisementExecutor = AdvertisementExecutor(
            object : Advertiser {
                override fun start(): Boolean {
                    TODO("Not yet implemented")
                }

                override fun stop() {
                    TODO("Not yet implemented")
                }

                override fun getMaxMessageSize(): Int {
                    TODO("Not yet implemented")
                }

                override fun send(message: String): Boolean {
                    actual = message.substring(2)
                    return true
                }
            },
            1000L,
            10,
            NeighbourTable(5000L)
        )
        advertisementExecutor.start()
        advertisementExecutor.addToQueue(expected)
        Thread.sleep(advertisementExecutor.period * 2)
        assertEquals(expected, actual)
    }

    @Test
    fun messageCutOff() {
        var actual: String = ""
        var expected: String = "test"
        val advertisementExecutor: AdvertisementExecutor = AdvertisementExecutor(
            object : Advertiser {
                override fun start(): Boolean {
                    TODO("Not yet implemented")
                }

                override fun stop() {
                    TODO("Not yet implemented")
                }

                override fun getMaxMessageSize(): Int {
                    TODO("Not yet implemented")
                }

                override fun send(message: String): Boolean {
                    actual += message.substring(2)
                    return true
                }
            },
            1000L,
            4,
            NeighbourTable(5000L),
        )
        advertisementExecutor.start()
        advertisementExecutor.addToQueue(expected)
        Thread.sleep(advertisementExecutor.period * 2)
        assertEquals(expected, actual)
    }

    @Test
    fun sendAdvertisements() {
        var advertisement: Advertisement = Advertisement.Builder()
            .type(MessageType.NEIGHBOUR_MESSAGE)
            .rssi(1)
            .hops(1)
            .address("eins")
            .name("eins")
            .description("eins")
            .color(1)
            .build()
        val neighbourTable: NeighbourTable = NeighbourTable(5000L)
        neighbourTable.updateNeighbour(Neighbour("eins", 1, 1, 1, advertisement = advertisement))
        var actual: MutableList<String> = LinkedList()
        val advertisementExecutor: AdvertisementExecutor = AdvertisementExecutor(
            object : Advertiser {
                override fun start(): Boolean {
                    TODO("Not yet implemented")
                }

                override fun stop() {
                    TODO("Not yet implemented")
                }

                override fun getMaxMessageSize(): Int {
                    TODO("Not yet implemented")
                }

                override fun send(message: String): Boolean {
                    actual.add(message.substring(2))
                    return true
                }
            },
            1000L,
            50,
            neighbourTable
        )
        advertisementExecutor.start()
        Thread.sleep(advertisementExecutor.period * 2)
        assertTrue(actual.contains(advertisement.toString()))
    }

    @Test
    fun sendMessageAndAdvertisements() {
        var advertisement: Advertisement = Advertisement.Builder()
            .type(MessageType.NEIGHBOUR_MESSAGE)
            .rssi(1)
            .hops(1)
            .address("eins")
            .name("eins")
            .description("eins")
            .color(1)
            .build()
        val neighbourTable: NeighbourTable = NeighbourTable(5000L)
        neighbourTable.updateNeighbour(Neighbour("eins", 1, 1, 1, advertisement = advertisement))
        val message = "test"
        var actual: MutableList<String> = mutableListOf()
        val advertisementExecutor: AdvertisementExecutor = AdvertisementExecutor(
            object : Advertiser {
                override fun start(): Boolean {
                    TODO("Not yet implemented")
                }

                override fun stop() {
                    TODO("Not yet implemented")
                }

                override fun getMaxMessageSize(): Int {
                    TODO("Not yet implemented")
                }

                override fun send(message: String): Boolean {
                    actual.add(message.substring(2))
                    return true
                }
            },
            1000L,
            50,
            neighbourTable
        )
        advertisementExecutor.start()
        advertisementExecutor.addToQueue(message)
        Thread.sleep(advertisementExecutor.period * 2)
        advertisementExecutor.stop()
        Thread.sleep(advertisementExecutor.period)
        assertTrue(actual.contains(advertisement.toString()))
        var result: Boolean = false
        actual.forEach {
            if (it.contains(message)) result = true
        }
        assertTrue(result)
    }
}