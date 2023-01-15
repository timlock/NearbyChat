package de.hsos.nearbychat.app.data

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.application.NearbyApplication
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.app.view.ChatActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Repository(database: Database, private val application: NearbyApplication) {
    private val messageDao: MessageDao = database.messageDao()
    private val profileDao: ProfileDao = database.profileDao()
    private val ownProfileDao: OwnProfileDao = database.onwProfileDao()

    private val savedProfilesRaw: LiveData<List<Profile>> = profileDao.get()
    private var availableList = mutableListOf<Profile>()
    private var savedList = mutableListOf<Profile>()

    val availableProfiles: LiveData<List<Profile>> = MutableLiveData()
    val savedProfiles: LiveData<List<Profile>> = MutableLiveData()
    val ownProfile: LiveData<OwnProfile?> = ownProfileDao.get()

    private var databaseHandler: HandlerThread = HandlerThread("databaseHandler")

    private val CHANNEL_ID = "Messages"
    private lateinit var notificationManager: NotificationManager

    init {
        val name = "TEST"//getString(R.string.channel_name)
        val descriptionText = "TEST2"//getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        this.databaseHandler.start()
        // register for changes in available profiles
        availableProfiles.observeForever {
            availableList = it.toMutableList()
            availableList.forEach { availableProfile ->
                savedList.forEach { savedProfile ->
                    if (availableProfile.address == savedProfile.address) {
                        if (availableProfile != savedProfile) {
                            // update saved profile because it differs from the saved one
                            savedList[savedList.indexOf(savedProfile)].updateReceivedData(availableProfile)
                        } else {
                            // update available signal information (async)
                            runBlocking {
                                launch {
                                    updateProfile(availableProfile)
                                }
                            }
                        }
                    }
                }
            }
            (savedProfiles as MutableLiveData).value = savedList
        }

        // register for changes in saved profiles
        savedProfilesRaw.observeForever {
            savedList = it.toMutableList()
            savedList.forEach { savedProfile ->
                availableList.forEach { availableProfile ->
                    if (availableProfile.address == savedProfile.address) {
                        // take signal information from available
                        savedList[savedList.indexOf(savedProfile)].updateSignal(availableProfile)
                    }
                }
            }
            (savedProfiles as MutableLiveData).value = savedList
        }
    }

    private fun createNotification(message: Message, profile: Profile, fromAvailable: Boolean) {
        val intent = Intent(application, ChatActivity::class.java)
        intent.putExtra(ChatActivity.INTENT_ADDRESS, profile.address)
        intent.putExtra(ChatActivity.INTENT_FROM_AVAILABLE, fromAvailable)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(application, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(application, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(profile.name)
            .setContentText(message.content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        notificationManager.notify(0, notification.build())
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun ackReceivedMessage(address: String, timestamp: Long) {
        val messageList = messageDao.get(address, timestamp, true)
        messageList.forEach {
            messageDao.update(it)
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertProfile(profile: Profile) {
        profileDao.insert(profile)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateProfile(profile: Profile) {
        profileDao.update(profile)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteProfile(macAddress: String) {
        profileDao.delete(macAddress)
    }

    fun getMessages(macAddress: String): LiveData<List<Message>> {
        return messageDao.get(macAddress)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertMessage(message: Message) {
        Handler(this.databaseHandler.looper).post {
            runBlocking {
                launch {
                    // check if message exists
                    if (messageDao.get(message.address, message.timeStamp, message.isSelfAuthored)
                            .isEmpty()
                    ) {
                        // check if profile to message is saved
                        var foundProfile: Profile? = null
                        savedList.forEach { savedProfile ->
                            if (message.address == savedProfile.address) {
                                if (!message.isSelfAuthored) {
                                    // set unread if message is not self authored
                                    savedProfile.isUnread = true
                                }
                                savedProfile.lastInteraction = message.timeStamp
                                updateProfile(savedProfile)
                                foundProfile = savedProfile
                            }
                        }
                        if (foundProfile != null) {
                            // profile not found, search if profile is available
                            var profile: Profile? = null
                            availableList.forEach { availableProfile ->
                                if (message.address == availableProfile.address) {
                                    profile = availableProfile
                                }
                            }
                            if (profile == null) {
                                // profile not available so use empty profile just with address and let it update later
                                profile = Profile(message.address)
                            }
                            if (!message.isSelfAuthored) {
                                // set unread if message is not self authored
                                profile!!.isUnread = true

                            }
                            profile!!.lastInteraction = message.timeStamp
                            insertProfile(profile!!)
                            foundProfile = profile
                            fromAvailable = true
                        }
                        messageDao.insert(message)
                        createNotification(message, foundProfile!!, fromAvailable)
                    }
                }
            }
        }
        if (!foundProfile) {
            // profile not found, search if profile is available
            var profile: Profile? = null
            availableList.forEach { availableProfile ->
                if (message.address == availableProfile.address) {
                    profile = availableProfile
                }
            }
            if (profile == null) {
                // profile not available so use empty profile just with address and let it update later
                profile = Profile(message.address)
            }
            if (!message.isSelfAuthored) {
                // set unread if message is not self authored
                profile!!.isUnread = true
                profile!!.lastInteraction = message.timeStamp

            }
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteMessages(macAddress: String) {
        messageDao.delete(macAddress)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateOwnProfile(ownProfile: OwnProfile) {
        ownProfileDao.update(ownProfile)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
     suspend fun getUnsentMessages(): List<Message> {
        return messageDao.getUnsentMessages()
    }
}