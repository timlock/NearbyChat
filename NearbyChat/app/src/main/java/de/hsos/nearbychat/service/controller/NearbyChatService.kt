package de.hsos.nearbychat.service.controller

import android.app.Service
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.service.bluetooth.MeshController

class NearbyChatService: Service() {
    private val TAG: String = NearbyChatService::class.java.simpleName
    private val binder = LocalBinder()
    private lateinit var bleController: MeshController

    inner class LocalBinder : Binder() {
        fun getService(): NearbyChatService = this@NearbyChatService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager;
        this.bleController = MeshController(bluetoothManager.adapter)
    }

    fun sendMessage(message: Message): Boolean {
        return true
    }

    companion object {
        val MESSAGE_PARAM: String = "MESSAGE_PARAM"
        val PROFILE_PARAM: String = "PROFILE_PARAM"
    }


}