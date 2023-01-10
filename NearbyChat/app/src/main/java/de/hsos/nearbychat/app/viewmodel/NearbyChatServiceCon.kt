package de.hsos.nearbychat.app.viewmodel

import android.content.*
import android.os.IBinder
import android.util.Log
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.service.bluetooth.util.Advertisement
import de.hsos.nearbychat.service.controller.NearbyChatService

class NearbyChatServiceCon(private val observer: NearbyChatObserver) : ServiceConnection {
    private val TAG: String = NearbyChatServiceCon::class.java.simpleName
    private lateinit var nearbyChatService: NearbyChatService
    private lateinit var ownProfile: OwnProfile

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG, "onServiceConnected() called with: name = $name, service = $service")
        val binder: NearbyChatService.LocalBinder = service as NearbyChatService.LocalBinder
        this.nearbyChatService = binder.getService()
        this.nearbyChatService.start(this.ownProfile)
        this.observer.onBound()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d(TAG, "onServiceDisconnected() called with: name = $name")
    }

    fun startService(context: Context, ownProfile: OwnProfile) {
        val filterServiceStarted: IntentFilter =  IntentFilter(NearbyChatService.PROFILE_ACTION)
        context.registerReceiver(this.broadcastReceiver, filterServiceStarted)
        this.ownProfile = ownProfile
        val startServiceIntent: Intent = Intent(context, NearbyChatService::class.java)
        context.startService(startServiceIntent)
        val bindToServiceIntent: Intent = Intent(context, NearbyChatService::class.java)
        context.bindService(bindToServiceIntent, this, Context.BIND_AUTO_CREATE)
    }

    fun closeService(context: Context){
        this.nearbyChatService.stop()
        context.unbindService(this)
        context.unregisterReceiver(this.broadcastReceiver)
    }

    fun sendMessage(message: Message): Boolean{
        return if (this::nearbyChatService.isInitialized){
            false
        }else{
            this.nearbyChatService.sendMessage(message)
            true
        }
    }

    private val broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                NearbyChatService.PROFILE_PARAM ->{
                    val param:String? = intent.getStringExtra(NearbyChatService.PROFILE_PARAM)
                    if(param == null){
                        Log.w(TAG, "onReceive: received ${NearbyChatService.PROFILE_ACTION} intent without content")
                    }else {
                        val advertisement: Advertisement = Advertisement.Builder()
                            .rawMessage(param)
                            .build()
                        val profile: Profile = Profile(advertisement.address!!)
                        profile.name = advertisement.name!!
                        profile.description = advertisement.description!!
                        profile.hopCount = advertisement.hops!!
                        profile.rssi = advertisement.rssi!!
                        profile.color = advertisement.color!!
                        profile.lastInteraction = System.currentTimeMillis()
                        this@NearbyChatServiceCon.observer.onProfile(profile)
                    }
                }
            }

        }
    }

}