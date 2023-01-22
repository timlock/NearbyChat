package de.hsos.nearbychat

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.common.domain.Message
import de.hsos.nearbychat.common.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.*
import de.hsos.nearbychat.service.bluetooth.Advertiser
import de.hsos.nearbychat.service.bluetooth.scan.ScannerObserver
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
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
                output.add(message.substring(2))
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
        Thread.sleep(MeshController.ADVERTISING_UPDATE_INTERVAL * 2)
        assertTrue(output.isNotEmpty())
        val expected: String = Advertisement.Builder()
            .type(AdvertisementType.NEIGHBOUR_ADVERTISEMENT.type)
            .sender("eins")
            .hops(MeshController.MAX_HOPS)
            .rssi(0)
            .address(ownProfile.address)
            .name(ownProfile.name)
            .description(ownProfile.description)
            .color(ownProfile.color)
            .build()
            .toString()
        meshController.disconnect()
        assertTrue(output.contains(expected))
    }

    @Test
    fun discoverNeighbour() {
        var resultEins = mutableListOf<Advertisement>()
        var resultZwei = mutableListOf<Advertisement>()
        var eins: MeshController? = null
        var zwei: MeshController? = null
        var einsSend: (String) -> Unit = {
            val rssi = -60
            val mac = "eins"
            zwei?.onPackage(mac, rssi, it)
        }
        var zweiSend: (String) -> Unit = {
            val rssi = -50
            val mac = "zwei"
            eins?.onPackage(mac, rssi, it)
        }
        var einsObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
                TODO("Not yet implemented")
            }

            override fun onMessageAck(advertisement: Advertisement) {
                TODO("Not yet implemented")
            }

            override fun onNeighbour(advertisement: Advertisement) {
                resultEins.add(advertisement)
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
                TODO("Not yet implemented")
            }

        }
        var zweiObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
                TODO("Not yet implemented")
            }

            override fun onMessageAck(advertisement: Advertisement) {
                TODO("Not yet implemented")
            }

            override fun onNeighbour(advertisement: Advertisement) {
                resultZwei.add(advertisement)
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
                TODO("Not yet implemented")
            }

        }
        eins = MockMeshControllerBuilder.createBuilder()
            .ownProfile("eins")
            .advertiser(150, einsSend)
            .build(einsObserver)
        eins.connect()
        zwei = MockMeshControllerBuilder.createBuilder()
            .ownProfile("zwei")
            .advertiser(150, zweiSend)
            .build(zweiObserver)
        zwei.connect()
        Thread.sleep(MeshController.ADVERTISING_UPDATE_INTERVAL * 2)
        assertTrue(resultEins.isNotEmpty())
        assertEquals("zwei", resultEins.first().address)
        assertEquals(-50, resultEins.first().rssi)
        assertEquals(MeshController.MAX_HOPS - 1, resultEins.first().hops)
        assertTrue(resultZwei.isNotEmpty())
        assertEquals("eins", resultZwei.first().address)
        assertEquals(-60, resultZwei.first().rssi)
        assertEquals(MeshController.MAX_HOPS - 1, resultZwei.first().hops)

    }

    @Test
    fun sendMessage() {
        var resultEinsMessage = mutableListOf<Advertisement>()
        var resultEinsAck = mutableListOf<Advertisement>()
        var resultZweiMessage = mutableListOf<Advertisement>()
        var resultZweiAck = mutableListOf<Advertisement>()
        var eins: MeshController? = null
        var zwei: MeshController? = null
        var einsSend: (String) -> Unit = {
            val rssi = -60
            val mac = "eins"
            zwei?.onPackage(mac, rssi, it)
        }
        var zweiSend: (String) -> Unit = {
            val rssi = -50
            val mac = "zwei"
            eins?.onPackage(mac, rssi, it)
        }
        var einsObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
                resultEinsMessage.add(advertisement)
            }

            override fun onMessageAck(advertisement: Advertisement) {
                resultEinsAck.add(advertisement)
            }

            override fun onNeighbour(advertisement: Advertisement) {
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
                TODO("Not yet implemented")
            }

        }
        var zweiObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
                resultZweiMessage.add(advertisement)
            }

            override fun onMessageAck(advertisement: Advertisement) {
                resultZweiAck.add(advertisement)
            }

            override fun onNeighbour(advertisement: Advertisement) {
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
                TODO("Not yet implemented")
            }

        }
        eins = MockMeshControllerBuilder.createBuilder()
            .ownProfile("eins")
            .advertiser(150, einsSend)
            .build(einsObserver)
        eins.connect()
        zwei = MockMeshControllerBuilder.createBuilder()
            .ownProfile("zwei")
            .advertiser(150, zweiSend)
            .build(zweiObserver)
        zwei.connect()
        Thread.sleep(MeshController.ADVERTISING_UPDATE_INTERVAL * 2)
        val einsMessage = Message("zwei", "hallo zwei", System.currentTimeMillis())
        eins.sendMessage(einsMessage)
        val zweiMessage = Message("eins", "hallo eins", System.currentTimeMillis())
        zwei.sendMessage(zweiMessage)
        Thread.sleep(MeshController.ADVERTISING_UPDATE_INTERVAL * 3)
        assertTrue(resultEinsMessage.isNotEmpty())
        assertEquals("zwei", resultEinsMessage.first().sender)
        assertEquals(zweiMessage.address, resultEinsMessage.first().receiver)
        assertEquals(zweiMessage.content, resultEinsMessage.first().message)
        assertEquals(zweiMessage.timeStamp, resultEinsMessage.first().timestamp)
        assertTrue(resultZweiMessage.isNotEmpty())
        assertEquals("eins", resultZweiMessage.first().sender)
        assertEquals(einsMessage.address, resultZweiMessage.first().receiver)
        assertEquals(einsMessage.content, resultZweiMessage.first().message)
        assertEquals(einsMessage.timeStamp, resultZweiMessage.first().timestamp)
        Thread.sleep(MeshController.ADVERTISING_UPDATE_INTERVAL * 3)
        assertTrue(resultEinsAck.isNotEmpty())
        assertEquals("eins", resultEinsAck.first().receiver)
        assertEquals(einsMessage.address, resultEinsAck.first().sender)
        assertEquals(einsMessage.timeStamp, resultEinsAck.first().timestamp)
        assertTrue(resultZweiAck.isNotEmpty())
        assertEquals("zwei", resultZweiAck.first().receiver)
        assertEquals(zweiMessage.address, resultZweiAck.first().sender)
        assertEquals(zweiMessage.timeStamp, resultZweiAck.first().timestamp)
    }

    @Test
    fun sendMessageOneHop() {
        var resultEinsMessage = mutableListOf<Advertisement>()
        var resultEinsAck = mutableListOf<Advertisement>()
        var resultEinsNeighbour = mutableListOf<Advertisement>()
        var resultZweiMessage = mutableListOf<Advertisement>()
        var resultZweiAck = mutableListOf<Advertisement>()
        var resultDreiMessage = mutableListOf<Advertisement>()
        var resultDreiAck = mutableListOf<Advertisement>()
        var eins: MeshController? = null
        var zwei: MeshController? = null
        var drei: MeshController? = null
        var einsSend: (String) -> Unit = {
            val rssi = -60
            val mac = "eins"
            zwei?.onPackage(mac, rssi, it)
        }
        var zweiSend: (String) -> Unit = {
            val rssi = -50
            val mac = "zwei"
            eins?.onPackage(mac, rssi, it)
            drei?.onPackage(mac, rssi, it)
        }
        var dreiSend: (String) -> Unit = {
            val rssi = -40
            val mac = "drei"
            zwei?.onPackage(mac, rssi, it)
        }
        var einsObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
                resultEinsMessage.add(advertisement)
            }

            override fun onMessageAck(advertisement: Advertisement) {
                resultEinsAck.add(advertisement)
            }

            override fun onNeighbour(advertisement: Advertisement) {
                resultEinsNeighbour.add(advertisement)
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
                TODO("Not yet implemented")
            }

        }
        var zweiObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
                resultZweiMessage.add(advertisement)
            }

            override fun onMessageAck(advertisement: Advertisement) {
                resultZweiAck.add(advertisement)
            }

            override fun onNeighbour(advertisement: Advertisement) {
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
                TODO("Not yet implemented")
            }

        }
        var dreiObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
                resultDreiMessage.add(advertisement)
            }

            override fun onMessageAck(advertisement: Advertisement) {
                resultDreiAck.add(advertisement)
            }

            override fun onNeighbour(advertisement: Advertisement) {
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
                TODO("Not yet implemented")
            }

        }
        eins = MockMeshControllerBuilder.createBuilder()
            .ownProfile("eins")
            .advertiser(150, einsSend)
            .build(einsObserver)
        eins.connect()
        zwei = MockMeshControllerBuilder.createBuilder()
            .ownProfile("zwei")
            .advertiser(150, zweiSend)
            .build(zweiObserver)
        zwei.connect()
        drei = MockMeshControllerBuilder.createBuilder()
            .ownProfile("drei")
            .advertiser(150, dreiSend)
            .build(dreiObserver)
        drei.connect()
        Thread.sleep(MeshController.ADVERTISING_UPDATE_INTERVAL * 4)
        assertNotNull(resultEinsNeighbour.firstOrNull { it.address == "drei" })
        val einsMessage = Message("drei", "hallo drei", System.currentTimeMillis())
        eins.sendMessage(einsMessage)
        Thread.sleep(MeshController.ADVERTISING_UPDATE_INTERVAL * 4)
        assertTrue(resultDreiMessage.isNotEmpty())
        assertEquals("eins", resultDreiMessage.first().sender)
        assertEquals(einsMessage.content, resultDreiMessage.first().message)
    }


    @Test
    fun sendMultipleMessages() {
        val amount = 10
        var resultEinsMessage = mutableListOf<Advertisement>()
        var resultEinsAck = mutableListOf<Advertisement>()
        var resultZweiMessage = mutableListOf<Advertisement>()
        var resultZweiAck = mutableListOf<Advertisement>()
        var eins: MeshController? = null
        var zwei: MeshController? = null
        var einsSend: (String) -> Unit = {
            val rssi = -60
            val mac = "eins"
            zwei?.onPackage(mac, rssi, it)
        }
        var zweiSend: (String) -> Unit = {
            val rssi = -50
            val mac = "zwei"
            eins?.onPackage(mac, rssi, it)
        }
        var einsObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
                resultEinsMessage.add(advertisement)
            }

            override fun onMessageAck(advertisement: Advertisement) {
                resultEinsAck.add(advertisement)
            }

            override fun onNeighbour(advertisement: Advertisement) {
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
                TODO("Not yet implemented")
            }

        }
        var zweiObserver = object : MeshObserver {
            override fun onMessage(advertisement: Advertisement) {
                resultZweiMessage.add(advertisement)
            }

            override fun onMessageAck(advertisement: Advertisement) {
                resultZweiAck.add(advertisement)
            }

            override fun onNeighbour(advertisement: Advertisement) {
            }

            override fun onNeighbourTimeout(timeoutList: List<String>) {
                TODO("Not yet implemented")
            }

        }
        eins = MockMeshControllerBuilder.createBuilder()
            .ownProfile("eins")
            .advertiser(1650, einsSend)
            .build(einsObserver)
        eins.connect()
        zwei = MockMeshControllerBuilder.createBuilder()
            .ownProfile("zwei")
            .advertiser(1650, zweiSend)
            .build(zweiObserver)
        zwei.connect()
        Thread.sleep(MeshController.ADVERTISING_UPDATE_INTERVAL * 2)
        val einsMessageList = mutableListOf<Message>()
        for (i in 0 until amount) {
            val msg = Message("zwei", "$i", System.currentTimeMillis())
            einsMessageList.add(msg)
            eins.sendMessage(msg)
        }
        val zweiMessageList = mutableListOf<Message>()
        for (i in 0 until amount) {
            val msg = Message("eins", "$i", System.currentTimeMillis())
            zweiMessageList.add(msg)
            zwei.sendMessage(msg)
        }
        Thread.sleep(MeshController.ADVERTISING_UPDATE_INTERVAL * 6)
        assertTrue(resultEinsMessage.isNotEmpty())
        assertTrue(amount <= resultEinsMessage.size)
        assertTrue(resultEinsAck.isNotEmpty())
        assertTrue(amount <= resultEinsAck.size)
        assertTrue(resultZweiMessage.isNotEmpty())
        assertTrue(amount <= resultZweiMessage.size)
        assertTrue(resultZweiAck.isNotEmpty())
        assertTrue(amount <= resultZweiAck.size)
    }

}