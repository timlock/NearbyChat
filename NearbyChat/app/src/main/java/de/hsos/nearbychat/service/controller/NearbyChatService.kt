package de.hsos.nearbychat.service.controller

import android.app.Service
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import de.hsos.nearbychat.app.domain.Message
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

    override fun onCreate() {
        var android_id : String = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
        val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        this.meshController = MeshController(this, bluetoothManager.adapter,android_id)
    }

    fun sendMessage(message: Message){
        this.meshController.sendMessage(message)
    }

    fun close() {
        stopSelf()
    }

    companion object {
        val MESSAGE_PARAM: String = "MESSAGE_PARAM"
        val PROFILE_PARAM: String = "PROFILE_PARAM"
    }

    override fun onMessage(advertisementMessage: AdvertisementMessage) {
        TODO("Not yet implemented")
    }

    override fun onMessageAck(advertisementMessage: AdvertisementMessage) {
        TODO("Not yet implemented")
    }


}