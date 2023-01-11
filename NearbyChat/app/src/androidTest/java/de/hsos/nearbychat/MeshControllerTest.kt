package de.hsos.nearbychat

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.*
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
            lateinit var observer: ScannerObserver
            override fun start(): Boolean {
                return true
            }

            override fun stop(): Boolean {
                return true
            }

            override fun subscribe(observer: ScannerObserver) {
                this.observer = observer
            }

        }
        val ownProfile = OwnProfile("eins")
        val meshController: MeshController = MeshController(meshObserver,advertiser,scanner, ownProfile)
        meshController.connect()
        Thread.sleep(MeshController.ADVERTISING_INTERVAL * 3)
        meshController.disconnect()
        Thread.sleep(MeshController.ADVERTISING_INTERVAL * 3)
        assertTrue(output.isNotEmpty())
        val expected: String = Advertisement.Builder()
            .type(MessageType.NEIGHBOUR_MESSAGE)
            .hops(MeshController.MAX_HOPS)
            .rssi(0)
            .address(ownProfile.address)
            .name(ownProfile.name)
            .description(ownProfile.description)
            .color(ownProfile.color)
            .build()
            .toString()
        assertTrue(output.contains(expected))
    }

//    @Test
//    fun discoverNeighbour() {
//        var actual: MutableList<String> = mutableListOf()
//        var scannerObserver: ScannerObserver? = null
//        val meshObserver: MeshObserver = object : MeshObserver {
//            override fun onMessage(advertisement: Advertisement) {
//                actual.add(advertisement.message!!)
//            }
//
//            override fun onMessageAck(advertisement: Advertisement) {
//            }
//
//            override fun onNeighbour(advertisement: Advertisement) {
//            }
//
//            override fun onNeighbourTimeout(timeoutList: List<String>) {
//            }
//
//        }
//        val advertiser: Advertiser = object : Advertiser {
//            override fun start(): Boolean {
//                return true
//            }
//
//            override fun stop() {
//            }
//
//            override fun getMaxMessageSize(): Int {
//                return 150
//            }
//
//            override fun send(message: String): Boolean {
//                if(scannerObserver != null){
//                    scannerObserver!!.onPackage("maceins",-50,message)
//                }
//                return true
//            }
//
//        }
//        val scanner: Scanner = object : Scanner {
//
//            override fun start(): Boolean {
//                return true
//            }
//
//            override fun stop(): Boolean {
//                return true
//            }
//
//            override fun subscribe(observer: ScannerObserver) {
//                scannerObserver = observer
//            }
//
//        }
//        val ownProfile = OwnProfile("eins")
//        val meshController: MeshController = MeshController(meshObserver,advertiser,scanner, ownProfile)
//        meshController.connect()
//        Thread.sleep(MeshController.ADVERTISING_INTERVAL * 3)
//        meshController.disconnect()
//        Thread.sleep(MeshController.ADVERTISING_INTERVAL * 3)
//
//        val expected: String = Advertisement.Builder()
//            .type(MessageType.NEIGHBOUR_MESSAGE)
//            .hops(MeshController.MAX_HOPS)
//            .rssi(0)
//            .address(ownProfile.address)
//            .name(ownProfile.name)
//            .description(ownProfile.description)
//            .color(ownProfile.color)
//            .build()
//            .toString()
//    }
}