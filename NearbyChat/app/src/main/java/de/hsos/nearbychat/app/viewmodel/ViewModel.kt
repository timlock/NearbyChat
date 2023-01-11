package de.hsos.nearbychat.app.viewmodel

import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import de.hsos.nearbychat.app.application.NearbyApplication
import de.hsos.nearbychat.app.data.OwnProfileDao
import de.hsos.nearbychat.app.data.Repository
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.app.domain.Profile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.log


class ViewModel(private val repository: Repository, application: Application) : AndroidViewModel(application){
    val ownProfile: LiveData<OwnProfile?> = repository.ownProfile
    val savedProfiles: LiveData<List<Profile>> = repository.savedProfiles
    val availableProfiles: LiveData<List<Profile>> = repository.availableProfiles

    fun updateOwnProfile(name: String, description: String, color: Int) = viewModelScope.launch {
        var profile = repository.ownProfile.value
        if (profile == null) {
            profile = OwnProfile(
                Settings.Secure.getString(
                    getApplication<NearbyApplication>().getContentResolver(),
                    Settings.Secure.ANDROID_ID
                )
            )
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
        val list: MutableList<Profile> = if(availableProfiles.value != null) {
            availableProfiles.value!!.toMutableList()
        } else {
            mutableListOf()
        }
        for (i in 0 until list.size) {
            if(list[i].address == profile.address) {
                list[i] = profile
                (availableProfiles as MutableLiveData).value = list
                return
            }
        }
        list.add(profile)
        (availableProfiles as MutableLiveData).value = list

    }

    fun deleteAvailableProfile(macAddress: String) {
        if(availableProfiles.value != null) {
            val list: MutableList<Profile> = availableProfiles.value!!.toMutableList()
            for (i in 0 until availableProfiles.value!!.size) {
                if(list[i].address == macAddress) {
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
        getApplication<NearbyApplication>().chatServiceCon.sendMessage(message)
    }

    fun deleteMessages(macAddress: String) = viewModelScope.launch {
        repository.deleteMessages(macAddress)
    }

    class ViewModelFactory(private val repository: Repository, private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ViewModel(repository, application) as T
        }
    }
}