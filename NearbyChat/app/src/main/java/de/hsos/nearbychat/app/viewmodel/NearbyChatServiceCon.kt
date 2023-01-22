package de.hsos.nearbychat.app.viewmodel

import android.content.*
import android.os.IBinder
import android.util.Log
import de.hsos.nearbychat.common.domain.Message
import de.hsos.nearbychat.common.domain.Profile
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import de.hsos.nearbychat.service.controller.NearbyChatService

class NearbyChatServiceCon(private val observer: NearbyChatObserver?) : ServiceConnection {
    private val TAG: String = NearbyChatServiceCon::class.java.simpleName
    private lateinit var nearbyChatService: NearbyChatService
    private lateinit var ownAddress: String

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG, "onServiceConnected() called with: name = $name, service = $service")
        val binder: NearbyChatService.LocalBinder = service as NearbyChatService.LocalBinder
        this.nearbyChatService = binder.getService()
        this.nearbyChatService.start(this.ownAddress)
        this.observer?.onBound()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d(TAG, "onServiceDisconnected() called with: name = $name")
    }

    fun connect(context: Context, ownAddress: String) {
        Log.d(TAG, "startService: ")
        this.registerReceiver(context)
        this.ownAddress = ownAddress
        this.startService(context)
    }

    private fun startService(context: Context) {
        val startServiceIntent: Intent = Intent(context, NearbyChatService::class.java)
        context.startService(startServiceIntent)
        val bindToServiceIntent: Intent = Intent(context, NearbyChatService::class.java)
        context.bindService(bindToServiceIntent, this, Context.BIND_AUTO_CREATE)
    }

    private fun registerReceiver(context: Context) {
        var filterServiceStarted: IntentFilter = IntentFilter(NearbyChatService.ON_PROFILE_ACTION)
        context.registerReceiver(this.broadcastReceiver, filterServiceStarted)
        filterServiceStarted = IntentFilter(NearbyChatService.ON_PROFILE_TIMEOUT_ACTION)
        context.registerReceiver(this.broadcastReceiver, filterServiceStarted)
    }

    fun disconnect(context: Context) {
        Log.d(TAG, "disconnect: ")
        context.unbindService(this)
        context.unregisterReceiver(this.broadcastReceiver)
    }

    fun closeService(context: Context) {
//        this.nearbyChatService.stop()
//        this.disconnect(context)
        val shutDownServiceIntent: Intent = Intent(context, NearbyChatService::class.java)
        shutDownServiceIntent.action = NearbyChatService.ACTION_SHUTDOWN
        context.startService(shutDownServiceIntent)
    }

    fun sendMessage(message: Message): Boolean {
        Log.d(TAG, "sendMessage() called with: message = $message")
        return if (this::nearbyChatService.isInitialized) {
            this.nearbyChatService.sendMessage(message)
            true
        } else {
            false
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "onReceive() called with")
            when (intent?.action) {
                NearbyChatService.ON_PROFILE_ACTION -> {
                    val param: String? = intent.getStringExtra(NearbyChatService.PROFILE_PARAM)
                    Log.d(TAG, "onReceive() ON_PROFILE_ACTION param = $param")
                    if (param == null) {
                        Log.w(
                            TAG,
                            "onReceive: received ${NearbyChatService.ON_PROFILE_ACTION} intent without content"
                        )
                    } else {
                        Advertisement.Builder()
                        val advertisement: Advertisement = Advertisement.Builder()
                            .rawMessage(param)
                            .build()
                        val profile: Profile = Profile(advertisement.address!!)
                        profile.name = advertisement.name!!
                        profile.description = advertisement.description!!
                        profile.hopCount = advertisement.hops!!
                        profile.rssi = advertisement.rssi!!
                        profile.color = advertisement.color!!
                        this@NearbyChatServiceCon.observer?.onProfile(profile)
                    }
                }
                NearbyChatService.ON_PROFILE_TIMEOUT_ACTION -> {
                    val param: List<String>? =
                        intent.getStringArrayListExtra(NearbyChatService.PROFILE_LIST_PARAM)
                    Log.d(TAG, "onReceive() ON_PROFILE_TIMEOUT_ACTION = $param")
                    if (param == null) {
                        Log.w(
                            TAG,
                            "onReceive: received ${NearbyChatService.ON_PROFILE_TIMEOUT_ACTION} intent without content"
                        )
                    } else {
                        param.forEach{this@NearbyChatServiceCon.observer?.onProfileTimeout(it) }
                    }
                }
            }
        }

    }
}

