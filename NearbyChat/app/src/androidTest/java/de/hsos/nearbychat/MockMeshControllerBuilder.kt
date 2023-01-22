package de.hsos.nearbychat

import de.hsos.nearbychat.common.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.Advertiser
import de.hsos.nearbychat.service.bluetooth.MeshController
import de.hsos.nearbychat.service.bluetooth.MeshObserver
import de.hsos.nearbychat.service.bluetooth.Scanner
import de.hsos.nearbychat.service.bluetooth.scan.ScannerObserver

class MockMeshControllerBuilder private constructor() {
    private var ownProfile: OwnProfile? = null
    private var sizeLimit: Int? = null
    private var send: ((String) -> Unit)? = null

    fun ownProfile(name: String): MockMeshControllerBuilder {
        this.ownProfile = OwnProfile(name)
        return this
    }

    fun advertiser(sizeLimit: Int, send: (String) -> Unit): MockMeshControllerBuilder {
        this.sizeLimit = sizeLimit
        this.send = send
        return this
    }


    fun build(meshObserver: MeshObserver): MeshController {
        lateinit var scannerObserver: ScannerObserver
        val advertiser: Advertiser = object : Advertiser {
            override fun start(): Boolean {
                return true
            }

            override fun stop() {
            }

            override fun getMaxMessageSize(): Int {
                return this@MockMeshControllerBuilder.sizeLimit!!
            }

            override fun send(message: String): Boolean {
                this@MockMeshControllerBuilder.send!!(message)
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
        return MeshController(
            meshObserver,
            advertiser,
            scanner,
            this@MockMeshControllerBuilder.ownProfile!!
        )
    }


    companion object {
        fun createBuilder(): MockMeshControllerBuilder = MockMeshControllerBuilder()
    }
}