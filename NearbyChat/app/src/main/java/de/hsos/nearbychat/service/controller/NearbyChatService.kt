package de.hsos.nearbychat.service.controller

import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertisingSetParameters
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.Advertiser
import de.hsos.nearbychat.service.bluetooth.MeshController
import de.hsos.nearbychat.service.bluetooth.MeshObserver
import de.hsos.nearbychat.service.bluetooth.advertise.BluetoothAdvertiser
import de.hsos.nearbychat.service.bluetooth.scan.BluetoothScanner
import de.hsos.nearbychat.service.bluetooth.util.Advertisement

class NearbyChatService: Service(), MeshObserver {
    private val TAG: String = NearbyChatService::class.java.simpleName
    private val binder = LocalBinder()
    private lateinit var meshController: MeshController

    inner class LocalBinder : Binder() {
        fun getService(): NearbyChatService = this@NearbyChatService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


   fun start(ownProfile: OwnProfile){
       val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
       var advertiser: Advertiser =
           BluetoothAdvertiser(bluetoothManager.adapter, AdvertisingSetParameters.INTERVAL_MEDIUM)
       var scanner: BluetoothScanner =
           BluetoothScanner(bluetoothManager.adapter.bluetoothLeScanner)
       this.meshController = MeshController(this, advertiser,ownProfile,scanner)

   }

    fun close() {
        stopSelf()
    }

    fun sendMessage(message: Message){
        this.meshController.sendMessage(message)
    }


    override fun onMessage(advertisement: Advertisement) {
        val message: Message = Message(advertisement.sender!!,advertisement.message!!,advertisement.timestamp!!)
    }

    override fun onMessageAck(advertisement: Advertisement) {
        TODO("Not yet implemented")
    }

    override fun onNeighbour(advertisement: Advertisement) {
        TODO("Not yet implemented")
    }

    companion object{
        val MESSAGE_PARAM: String = "MESSAGE_PARAM"
        val PROFILE_PARAM: String = "PROFILE_PARAM"
        val MESSAGE_ACTION: String = "MESSAGE_ACTION"
        val ACKNOWLEDGE_ACTION: String = "ACKNOWLEDGE_ACTION"
    }

}