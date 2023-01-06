package de.hsos.nearbychat.app.viewmodel

import androidx.lifecycle.*
import androidx.lifecycle.ViewModel
import androidx.room.Transaction
import de.hsos.nearbychat.app.data.Repository
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.app.domain.Profile
import kotlinx.coroutines.launch

class ViewModel(private val repository: Repository) : ViewModel(){
    val ownProfile: LiveData<OwnProfile> = repository.ownProfile
    val savedProfiles: LiveData<List<Profile>> = repository.savedProfiles
    val availableProfiles: LiveData<List<Profile>> = MutableLiveData()

    fun updateOwnProfile(ownProfile: OwnProfile) = viewModelScope.launch {
        repository.insertOwnProfile(ownProfile)
    }

    fun updateSavedProfile(profile: Profile) = viewModelScope.launch {
        repository.insertProfile(profile)
    }

    fun deleteSavedProfile(macAddress: String) = viewModelScope.launch {
        repository.deleteProfile(macAddress)
    }

    fun updateAvailableProfile(profile: Profile) {
        if(availableProfiles.value != null) {
            val list: MutableList<Profile> = availableProfiles.value!!.toMutableList()
            for (i in 0..availableProfiles.value!!.size) {
                if(list[i].macAddress == profile.macAddress) {
                    list[i] = profile
                    (availableProfiles as MutableLiveData<List<Profile>>).value = list
                }
            }
        }
    }

    fun deleteAvailableProfile(macAddress: String) {
        if(availableProfiles.value != null) {
            val list: MutableList<Profile> = availableProfiles.value!!.toMutableList()
            for (i in 0..availableProfiles.value!!.size) {
                if(list[i].macAddress == macAddress) {
                    list.removeAt(i)
                    (availableProfiles as MutableLiveData<List<Profile>>).value = list
                }
            }
        }
    }


    class WordViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}