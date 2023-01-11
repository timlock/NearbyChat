package de.hsos.nearbychat.service.controller

import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertisingSetParameters
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.application.NearbyApplication
import de.hsos.nearbychat.app.data.Repository
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.Advertiser
import de.hsos.nearbychat.service.bluetooth.MeshController
import de.hsos.nearbychat.service.bluetooth.MeshObserver
import de.hsos.nearbychat.service.bluetooth.advertise.BluetoothAdvertiser
import de.hsos.nearbychat.service.bluetooth.scan.BluetoothScanner
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NearbyChatService : Service(), MeshObserver {
    private val TAG: String = NearbyChatService::class.java.simpleName
    private val binder = LocalBinder()
    private lateinit var meshController: MeshController
    private var repository: Repository = (application as NearbyApplication).repository
    private var ownProfile: LiveData<OwnProfile?> = repository.ownProfile
    private val ownProfileObserver = Observer<OwnProfile?> { p ->
        if (p != null) {
            this.meshController.ownProfile = p
        }
    }

    override fun onCreate() {
        this.ownProfile.observeForever(this.ownProfileObserver)
    }

    override fun onDestroy() {
        this.ownProfile.removeObserver(this.ownProfileObserver)
    }

    inner class LocalBinder : Binder() {
        fun getService(): NearbyChatService = this@NearbyChatService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


    fun start(ownAddress: String) {
        if (this::meshController.isInitialized) {
            Log.d(TAG, "start: service is already running")
        } else {
            Log.d(TAG, "start: ")
            val bluetoothManager: BluetoothManager =
                getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val advertiser: Advertiser =
                BluetoothAdvertiser(
                    bluetoothManager.adapter,
                    AdvertisingSetParameters.INTERVAL_MEDIUM
                )
            val scanner: BluetoothScanner =
                BluetoothScanner(bluetoothManager.adapter.bluetoothLeScanner)
            var self: OwnProfile? = this.ownProfile.value
            if(this.ownProfile.value == null){
                self = OwnProfile(ownAddress)
                self.name = applicationContext.resources.getString(R.string.error_name_missing)
                self.description = applicationContext.resources.getString(R.string.error_desc_missing)
            }
            this.meshController = MeshController(this, advertiser, self!!, scanner)
            this.meshController.startScan()
            this.meshController.startAdvertise()
        }
    }


    fun stop() {
        Log.d(TAG, "stop: ")
        this.meshController.stopAdvertising()
        this.meshController.stopScan()
        stopSelf()
    }

    fun sendMessage(message: Message) {
        Log.d(TAG, "sendMessage() called with: message = $message")
        this.meshController.sendMessage(message)
    }


    override fun onMessage(advertisement: Advertisement): Unit = runBlocking {
        launch {
            Log.d(TAG, "onMessage() called: advertisement = $advertisement")
            val message: Message =
                Message(advertisement.sender!!, advertisement.message!!, advertisement.timestamp!!)
            repository.insertMessage(message)
        }


    }

    override fun onMessageAck(advertisement: Advertisement): Unit = runBlocking {
        launch {
            Log.d(TAG, "onMessageAck() called: advertisement = $advertisement")
            repository.ackReceivedMessage(advertisement.sender!!, advertisement.timestamp!!)
        }
    }

    override fun onNeighbour(advertisement: Advertisement) {
        Log.d(TAG, "onNeighbour() called with: advertisement = $advertisement")
        val intent: Intent = Intent()
        intent.action = NearbyChatService.PROFILE_ACTION
        intent.putExtra(PROFILE_PARAM, advertisement.toString())
        this.sendBroadcast(intent)
    }

    companion object {
        const val PROFILE_PARAM: String = "PROFILE_PARAM"
        const val PROFILE_ACTION: String = "PROFILE_ACTION"
    }

}