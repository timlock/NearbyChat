package de.hsos.nearbychat.app.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.app.domain.Profile

class Repository(database: Database) {
    private val messageDao: MessageDao = database.messageDao()
    private val profileDao: ProfileDao = database.profileDao()
    private val ownProfileDao: OwnProfileDao = database.onwProfileDao()

    val savedProfiles: LiveData<List<Profile>> = profileDao.get()
    val ownProfile: LiveData<OwnProfile> = ownProfileDao.get()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertProfile(profile: Profile) {
        profileDao.insert(profile)
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
        messageDao.insert(message)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteMessages(macAddress: String) {
        messageDao.delete(macAddress)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertOwnProfile(ownProfile: OwnProfile) {
        ownProfileDao.insert(ownProfile)
    }
}