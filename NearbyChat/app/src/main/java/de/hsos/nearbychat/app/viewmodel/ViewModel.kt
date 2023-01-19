package de.hsos.nearbychat.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import de.hsos.nearbychat.common.application.NearbyApplication
import de.hsos.nearbychat.common.data.Repository
import de.hsos.nearbychat.common.domain.Message
import de.hsos.nearbychat.common.domain.OwnProfile
import de.hsos.nearbychat.common.domain.Profile
import kotlinx.coroutines.launch


class ViewModel(private val repository: Repository, application: Application) :
    AndroidViewModel(application), NearbyChatObserver {
    private val TAG: String = ViewModel::class.java.simpleName
    val ownProfile: LiveData<OwnProfile?> = repository.ownProfile
    val savedProfiles: LiveData<List<Profile>> = repository.savedProfiles
    val availableProfiles: LiveData<List<Profile>> = repository.availableProfiles
    val chatServiceCon: NearbyChatServiceCon = NearbyChatServiceCon(this)

    init {
        this.chatServiceCon.connect(
            application,
            (application as NearbyApplication).ownAddress
        )
    }

    override fun onCleared() {
        this.chatServiceCon.disconnect(getApplication())
    }

    fun updateOwnProfile(name: String, description: String, color: Int) = viewModelScope.launch {
        var profile = repository.ownProfile.value
        if (profile == null) {
            profile = OwnProfile(getApplication<NearbyApplication>().ownAddress)
        }
        profile.name = name
        profile.description = description
        profile.color = color
        repository.updateOwnProfile(profile)
    }

    fun updateSavedProfile(profile: Profile) = viewModelScope.launch {
        repository.updateProfile(profile)
    }

    fun deleteSavedProfile(macAddress: String) = viewModelScope.launch {
        repository.deleteProfile(macAddress)
    }

    fun updateAvailableProfile(profile: Profile) {
        val list: MutableList<Profile> = if (availableProfiles.value != null) {
            availableProfiles.value!!.toMutableList()
        } else {
            mutableListOf()
        }
        for (i in 0 until list.size) {
            if (list[i].address == profile.address) {
                if(list[i] != profile) {
                    list[i] = profile
                    (availableProfiles as MutableLiveData).value = list
                }
                return
            }
        }
        list.add(profile)
        (availableProfiles as MutableLiveData).value = list

    }

    fun deleteAvailableProfile(macAddress: String) {
        if (availableProfiles.value != null) {
            val list: MutableList<Profile> = availableProfiles.value!!.toMutableList()
            for (i in 0 until availableProfiles.value!!.size) {
                if (list[i].address == macAddress) {
                    list.removeAt(i)
                    (availableProfiles as MutableLiveData<List<Profile>>).value = list
                    return
                }
            }
        }
    }

    fun getMessages(macAddress: String): LiveData<List<Message>> {
        return repository.getMessages(macAddress)
    }

    fun addMessage(message: Message) = viewModelScope.launch {
        repository.insertMessage(message)
        chatServiceCon.sendMessage(message)
    }

    fun deleteMessages(macAddress: String) = viewModelScope.launch {
        repository.deleteMessages(macAddress)
    }

    class ViewModelFactory(
        private val repository: Repository,
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ViewModel(repository, application) as T
        }
    }

    override fun onBound() {
        Log.d(TAG, "onBound: ")
    }
    @Synchronized
    override fun onProfile(profile: Profile) {
        Log.d(TAG, "onProfile() called with: profile = $profile")
        this.updateAvailableProfile(profile)

    }
    @Synchronized
    override fun onProfileTimeout(address: String) {
        Log.d(TAG, "onProfileTimeout() called with: address = $address")
        this.deleteAvailableProfile(address)
    }
}