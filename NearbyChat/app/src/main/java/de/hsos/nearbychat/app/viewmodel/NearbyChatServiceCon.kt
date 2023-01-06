package de.hsos.nearbychat.app.viewmodel

import android.content.*
import android.os.IBinder
import android.util.Log
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.service.controller.NearbyChatService

class NearbyChatServiceCon(private val observer: NearbyChatObserver) : ServiceConnection {
    private val TAG: String = NearbyChatServiceCon::class.java.simpleName
    private lateinit var nearbyChatService: NearbyChatService

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d(TAG, "onServiceConnected() called with: name = $name, service = $service")
        val binder: NearbyChatService.LocalBinder = service as NearbyChatService.LocalBinder
        this.nearbyChatService = binder.getService()
        this.observer.onBound()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d(TAG, "onServiceDisconnected() called with: name = $name")
    }

    fun startService(context: Context) {
        val startServiceIntent: Intent = Intent(context, NearbyChatService::class.java)
        context.startService(startServiceIntent);
        val bindToServiceIntent: Intent = Intent(context, NearbyChatService::class.java);
        context.bindService(bindToServiceIntent, this, Context.BIND_AUTO_CREATE);
    }

    fun closeService(context: Context){
        this.nearbyChatService.close()
        context.unbindService(this)

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
                NearbyChatService.MESSAGE_PARAM -> intent.getParcelableExtra<Message>(
                    NearbyChatService.MESSAGE_PARAM
                )
                    ?.let { observer.onMessage(it) }
                NearbyChatService.PROFILE_PARAM -> intent.getParcelableExtra<Profile>(
                    NearbyChatService.PROFILE_PARAM
                )
                    ?.let { observer.onProfile(it) }
            }

        }
    }

}