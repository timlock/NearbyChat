package de.hsos.nearbychat

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.*
import de.hsos.nearbychat.service.bluetooth.scan.ScannerObserver
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import de.hsos.nearbychat.service.bluetooth.util.AdvertisementPackage
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MeshControllerTest {
    @Test
    fun connect() {
        var output: MutableList<String> = mutableListOf()
        lateinit var scannerObserver: ScannerObserver
        val meshObserver: MeshObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
            }

            override fun onMessageAck(advertisement: Advertisement) {
            }

            override fun onNeighbour(advertisement: Advertisement) {
                output.add(advertisement.toString())
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
            }

        }
        val advertiser: Advertiser = object : Advertiser {
            override fun start(): Boolean {
                return true
            }

            override fun stop() {
            }

            override fun getMaxMessageSize(): Int {
                return 150
            }

            override fun send(message: String): Boolean {
                scannerObserver.onPackage(-50, message)
                return true
            }

        }
        val scanner: Scanner = object : Scanner {
            override fun start(): Boolean {
                return true
            }

            override fun stop(): Boolean {
                return true
            }

            override fun subscribe(observer: ScannerObserver) {
                scannerObserver = observer
            }

        }
        val ownProfile = OwnProfile("eins")
        val meshController: MeshController =
            MeshController(meshObserver, advertiser, scanner, ownProfile)
        meshController.connect()
        Thread.sleep(MeshController.ADVERTISING_INTERVAL)
        meshController.disconnect()
        Thread.sleep(MeshController.ADVERTISING_INTERVAL)
        assertTrue(output.isNotEmpty())
        val expected: String = Advertisement.Builder()
            .type(MessageType.NEIGHBOUR_MESSAGE.type)
            .sender("eins")
            .hops(MeshController.MAX_HOPS - 1)
            .rssi(0)
            .address(ownProfile.address)
            .name(ownProfile.name)
            .description(ownProfile.description)
            .color(ownProfile.color)
            .build()
            .toString()
        assertTrue(output.contains(expected))
    }

    @Test
    fun sendMessage() {
        var neighbours: MutableList<String> = mutableListOf()
        var messages: MutableList<AdvertisementPackage> = mutableListOf()
        lateinit var scannerObserver: ScannerObserver
        val meshObserver: MeshObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
            }

            override fun onMessageAck(advertisement: Advertisement) {
            }

            override fun onNeighbour(advertisement: Advertisement) {
                neighbours.add(advertisement.toString())
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
            }

        }
        val advertiser: Advertiser = object : Advertiser {
            override fun start(): Boolean {
                return true
            }

            override fun stop() {
            }

            override fun getMaxMessageSize(): Int {
                return 150
            }

            override fun send(message: String): Boolean {
                messages.add(AdvertisementPackage.toPackage(message))
                return true
            }

        }
        val scanner: Scanner = object : Scanner {
            override fun start(): Boolean {
                return true
            }

            override fun stop(): Boolean {
                return true
            }

            override fun subscribe(observer: ScannerObserver) {
                scannerObserver = observer
            }

        }
        val ownProfile = OwnProfile("eins")
        val meshController: MeshController =
            MeshController(meshObserver, advertiser, scanner, ownProfile)
        meshController.connect()
        val advertisement: Advertisement = Advertisement.Builder()
            .type(MessageType.NEIGHBOUR_MESSAGE.type)
            .hops(MeshController.MAX_HOPS)
            .sender("zweiAddress")
            .rssi(-50)
            .address("zweiAddress")
            .name("zweiname")
            .description("zweidescription")
            .color(3)
            .build()
        scannerObserver.onPackage(-50, "1:$advertisement")
        Thread.sleep(MeshController.ADVERTISING_INTERVAL)
        val message: Message = Message("zweiAddress", "test", System.currentTimeMillis())
        meshController.sendMessage(message)
        Thread.sleep(MeshController.ADVERTISING_INTERVAL * 2)
        meshController.disconnect()
        Thread.sleep(MeshController.ADVERTISING_INTERVAL)
        assertTrue(neighbours.isNotEmpty())
        advertisement.decrementHop()
        advertisement.sender = ownProfile.address
        assertEquals(advertisement.toString(),neighbours[0])
        assertTrue(messages.isNotEmpty())
        var actual = false
        messages.forEach {
            if (it.getMessageList().map { m -> m.toString() }.contains(advertisement.toString())) {
                actual = true
            }
        }
        assertTrue(actual)
    }

}