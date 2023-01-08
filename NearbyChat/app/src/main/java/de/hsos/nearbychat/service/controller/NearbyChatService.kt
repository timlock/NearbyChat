package de.hsos.nearbychat.service.controller

import android.app.Service
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.service.bluetooth.MeshController
import de.hsos.nearbychat.service.bluetooth.MeshObserver
import de.hsos.nearbychat.service.bluetooth.util.AdvertisementMessage

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
       this.meshController = MeshController(this, bluetoothManager.adapter,ownProfile)
   }

    fun close() {
        stopSelf()
    }

    fun sendMessage(message: Message){
        this.meshController.sendMessage(message)
    }


    override fun onMessage(advertisementMessage: AdvertisementMessage) {
        val intent: Intent = Intent()
        intent.action = NearbyChatService.MESSAGE_ACTION
        val message: Message = Message(advertisementMessage.sender,advertisementMessage.message,)
    }

    override fun onMessageAck(advertisementMessage: AdvertisementMessage) {
        TODO("Not yet implemented")
    }

    override fun onNeighbour(advertisementMessage: AdvertisementMessage) {
        TODO("Not yet implemented")
    }

    companion object{
        val MESSAGE_PARAM: String = "MESSAGE_PARAM"
        val PROFILE_PARAM: String = "PROFILE_PARAM"
        val MESSAGE_ACTION: String = "MESSAGE_ACTION"
        val ACKNOWLEDGE_ACTION: String = "ACKNOWLEDGE_ACTION"
    }

}