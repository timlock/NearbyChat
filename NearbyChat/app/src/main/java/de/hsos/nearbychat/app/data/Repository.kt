package de.hsos.nearbychat.app.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.app.domain.Profile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Repository(database: Database) {
    private val messageDao: MessageDao = database.messageDao()
    private val profileDao: ProfileDao = database.profileDao()
    private val ownProfileDao: OwnProfileDao = database.onwProfileDao()

    private val savedProfilesRaw: LiveData<List<Profile>> = profileDao.get()
    private var availableList = mutableListOf<Profile>()
    private var savedList = mutableListOf<Profile>()

    val availableProfiles: LiveData<List<Profile>> = MutableLiveData()
    val savedProfiles: LiveData<List<Profile>> = MutableLiveData()
    val ownProfile: LiveData<OwnProfile?> = ownProfileDao.get()

    init {
        // register for changes in available profiles
        availableProfiles.observeForever{
            availableList = it.toMutableList()
            availableList.forEach { availableProfile ->
                savedList.forEach { savedProfile ->
                    if(availableProfile.address == savedProfile.address) {
                        if (availableProfile != savedProfile) {
                            // update saved profile because it differs from the saved one
                            savedList[savedList.indexOf(savedProfile)] = availableProfile
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
        savedProfilesRaw.observeForever{
            savedList = it.toMutableList()
            savedList.forEach { savedProfile ->
                availableList.forEach { availableProfile ->
                    if(availableProfile.address == savedProfile.address) {
                        // take signal information from available
                        savedList[savedList.indexOf(savedProfile)].updateSignal(availableProfile)
                    }
                }
            }
            (savedProfiles as MutableLiveData).value = savedList
        }
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
        // check if profile to message is saved
        var foundProfile = false
        savedList.forEach { savedProfile ->
            if(message.address == savedProfile.address) {
                if(!message.isSelfAuthored) {
                    // set unread if message is not self authored
                    savedProfile.isUnread = true
                    updateProfile(savedProfile)
                }
                foundProfile = true
            }
        }
        if(!foundProfile) {
            // profile not found, search if profile is available
            var profile: Profile? = null
            availableList.forEach { availableProfile ->
                if(message.address == availableProfile.address) {
                    profile = availableProfile
                }
            }
            if(profile == null) {
                // profile not available so use empty profile just with address and let it update later
                profile = Profile(message.address)
            }
            if(!message.isSelfAuthored) {
                // set unread if message is not self authored
                profile!!.isUnread = true
                insertProfile(profile!!)
            }
        }
        messageDao.insert(message)
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
}