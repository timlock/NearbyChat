package de.hsos.nearbychat.app.application

import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.MutableLiveData
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.data.Database
import de.hsos.nearbychat.app.data.Repository
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.app.viewmodel.NearbyChatObserver
import de.hsos.nearbychat.app.viewmodel.NearbyChatServiceCon
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NearbyApplication: android.app.Application(), NearbyChatObserver {
    private val database by lazy { Database.getDatabase(this) }
    val repository by lazy { Repository(database) }

    val TAG: String = Application::class.java.simpleName

    val chatServiceCon : NearbyChatServiceCon = NearbyChatServiceCon(this)

    init{
        repository.ownProfile.observeForever {
            var ownProfile: OwnProfile? = it
            if (ownProfile == null) {
                ownProfile = OwnProfile(
                    Settings.Secure.getString(
                        this.getContentResolver(),
                        Settings.Secure.ANDROID_ID
                    )
                )
                GlobalScope.launch {
                    repository.updateOwnProfile(ownProfile)
                }
            }
            this.chatServiceCon.startService(
                applicationContext,
                ownProfile
            )
        }
    }

    companion object {
        fun getUserColorRes(id: Int): Int {
            var color = R.color.profile_0
            when(id) {
                1 -> color = R.color.profile_1
                2 -> color = R.color.profile_2
                3 -> color = R.color.profile_3
                4 -> color = R.color.profile_4
                5 -> color = R.color.profile_5
                6 -> color = R.color.profile_6
                7 -> color = R.color.profile_7
                8 -> color = R.color.profile_8
                9 -> color = R.color.profile_9
            }
            return color
        }

        fun getSignalStrengthIcon(id: Int): Int {
            var drawable = R.drawable.ic_baseline_signal_wifi_0_bar_24
            when(id) {
                1 -> drawable = R.drawable.ic_baseline_network_wifi_1_bar_24
                2 -> drawable = R.drawable.ic_baseline_network_wifi_2_bar_24
                3 -> drawable = R.drawable.ic_baseline_network_wifi_3_bar_24
                4 -> drawable = R.drawable.ic_baseline_signal_wifi_4_bar_24
            }
            return drawable
        }
    }

    override fun onBound() {
        Log.d(TAG, "onBound: ")
    }

    override fun onProfile(profile: Profile) {
        Log.d(TAG, "onProfile() called with: profile = $profile")
        val tmpList = this.repository.availableProfiles.value as MutableList
        tmpList.add(profile)
        (this.repository.availableProfiles as MutableLiveData).value = tmpList
    }
}