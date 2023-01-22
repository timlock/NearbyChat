package de.hsos.nearbychat.service.controller

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.view.SplashScreenActivity
import de.hsos.nearbychat.common.application.NearbyApplication
import de.hsos.nearbychat.common.data.Repository
import de.hsos.nearbychat.common.domain.Message
import de.hsos.nearbychat.common.domain.OwnProfile
import de.hsos.nearbychat.service.bluetooth.Advertiser
import de.hsos.nearbychat.service.bluetooth.MeshController
import de.hsos.nearbychat.service.bluetooth.MeshObserver
import de.hsos.nearbychat.service.bluetooth.Scanner
import de.hsos.nearbychat.service.bluetooth.advertise.MeshAdvertiser
import de.hsos.nearbychat.service.bluetooth.scan.MeshScanner
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NearbyChatService : Service(), MeshObserver {
    private val TAG: String = NearbyChatService::class.java.simpleName
    private val binder = LocalBinder()
    private lateinit var meshController: MeshController
    private lateinit var repository: Repository
    private lateinit var ownProfile: LiveData<OwnProfile?>
    private var databaseHandler: HandlerThread = HandlerThread("databaseHandler")

    init {
        this.databaseHandler.start()
    }

    private val ownProfileObserver = Observer<OwnProfile?> { p ->
        if (p != null && this::meshController.isInitialized) {
            this.meshController.updateOwnProfile(p)
        }
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate: ")
        this.repository = (application as NearbyApplication).repository
        this.ownProfile = this.repository.ownProfile
        this.ownProfile.observeForever(this.ownProfileObserver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action == NearbyChatService.ACTION_SHUTDOWN){
            this.stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        this.ownProfile.removeObserver(this.ownProfileObserver)
    }

    inner class LocalBinder : Binder() {
        fun getService(): NearbyChatService = this@NearbyChatService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun startForegroundService() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)
        val notification =
            createPendingIntent()
        startForeground(NearbyChatService.NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(): Notification {
        val shutdownIntent = Intent(this, SplashScreenActivity::class.java)
        shutdownIntent.action = ACTION_SHUTDOWN
        val pendingIntent =
            PendingIntent.getActivity(this, 0, shutdownIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_NAME)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_shutdown))
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val notificationChannel = NotificationChannel(
            NearbyChatService.NOTIFICATION_CHANNEL_NAME,
            NearbyChatService.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }


    fun start(ownAddress: String) {
        if (this::meshController.isInitialized) {
            Log.d(TAG, "start: service is already running")
        } else {
            Log.d(TAG, "start() called with: ownAddress = $ownAddress")
            val bluetoothAdapter: BluetoothAdapter = getBluetoothAdapter()
            val advertiser: Advertiser = createAdvertiser(bluetoothAdapter)
            val scanner: Scanner = MeshScanner(bluetoothAdapter.bluetoothLeScanner)
            var self: OwnProfile = getOwnProfile(ownAddress)
            this.meshController = MeshController(this, advertiser, scanner, self)
            this.meshController.connect()
            this.resendUnsentMessages()
            this.startForegroundService()
        }
    }

    private fun resendUnsentMessages() {
        Handler(this.databaseHandler.looper).post {
            runBlocking {
                launch {
                    val unsentMessages = this@NearbyChatService.repository.getUnsentMessages()
                    this@NearbyChatService.meshController.addUnsentMessages(unsentMessages)
                }
            }
        }
    }

    private fun getOwnProfile(ownAddress: String): OwnProfile {
        var self: OwnProfile? = this.ownProfile.value
        if (this.ownProfile.value == null) {
            self = OwnProfile(ownAddress)
            self.name = applicationContext.resources.getString(R.string.error_name_missing)
            self.description =
                applicationContext.resources.getString(R.string.error_desc_missing)
        }
        return self!!
    }

    private fun createAdvertiser(bluetoothAdapter: BluetoothAdapter): Advertiser {
        return MeshAdvertiser(
            bluetoothAdapter,
            MeshController.ADVERTISING_INTERVAL.toInt()
        )
    }

    private fun getBluetoothAdapter(): BluetoothAdapter {
        val bluetoothManager: BluetoothManager =
            getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }


    fun stop() {
        Log.d(TAG, "stop() called")
        this.meshController.disconnect()
        stopForeground(true)
        stopSelf()
    }

    fun sendMessage(message: Message) {
        Log.d(TAG, "sendMessage() called with: message = $message")
        this.meshController.sendMessage(message)
    }


    override fun onMessage(advertisement: Advertisement) {
        Log.d(TAG, "onMessage() called: advertisement = $advertisement")
        Handler(this.databaseHandler.looper).post {
            runBlocking {
                launch {
                    val message: Message =
                        Message(
                            advertisement.sender!!,
                            advertisement.message!!,
                            advertisement.timestamp!!
                        )
                    repository.insertMessage(message)
                }
            }
        }
    }

    override fun onMessageAck(advertisement: Advertisement) {
        Log.d(TAG, "onMessageAck() called with: advertisement = $advertisement")
        Handler(this.databaseHandler.looper).post {
            runBlocking {
                launch {
                    repository.ackReceivedMessage(advertisement.sender!!, advertisement.timestamp!!)
                }
            }
        }
    }

    override fun onNeighbour(advertisement: Advertisement) {
        Log.d(TAG, "onNeighbour() called with: advertisement = $advertisement")
        val intent: Intent = Intent()
        intent.action = NearbyChatService.ON_PROFILE_ACTION
        intent.putExtra(PROFILE_PARAM, advertisement.toString())
        sendBroadcast(intent)
    }

    override fun onNeighbourTimeout(timeoutList: List<String>) {
        Log.d(TAG, "onNeighbourTimeout() called with: timeoutList = $timeoutList")
        val intent: Intent = Intent()
        intent.action = NearbyChatService.ON_PROFILE_TIMEOUT_ACTION
        val param: ArrayList<String> = ArrayList()
        param.addAll(timeoutList)
        intent.putStringArrayListExtra(NearbyChatService.PROFILE_LIST_PARAM, param)
        sendBroadcast(intent)
    }

    companion object {
        const val ACTION_SHUTDOWN = "ACTION_SHUTDOWN"
        const val NOTIFICATION_CHANNEL_NAME = "NearbyChat"
        const val NOTIFICATION_ID = 1
        const val PROFILE_PARAM: String = "PROFILE_PARAM"
        const val PROFILE_LIST_PARAM: String = "PROFILE_LIST_PARAM"
        const val ON_PROFILE_ACTION: String = "ON_PROFILE_ACTION"
        const val ON_PROFILE_TIMEOUT_ACTION: String = "ON_PROFILE_TIMEOUT_ACTION"
    }

}