package de.hsos.nearbychat.app.view

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.PermissionManager
import de.hsos.nearbychat.app.application.Application
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.app.viewmodel.ViewModel
import java.sql.Timestamp
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: ActionBar
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var sharedPreferences: SharedPreferences
    private var currentFragment: String? = null

    private val viewModel: ViewModel by viewModels {
        ViewModel.ViewModelFactory((this.application as Application).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("APP_SETTINGS", MODE_PRIVATE)
        currentFragment = savedInstanceState?.getString("currentFragmentName")
        
        clearDatabaseFromTestData()
        fillDatabaseWithTestData()

        updateLanguage()
        updateNightMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(currentFragment != null) {
            when(currentFragment) {
                AvailableView::class.simpleName -> openFragment(AvailableView.newInstance())
                ChatsView::class.simpleName -> openFragment(ChatsView.newInstance())
                ProfileView::class.simpleName -> openFragment(ProfileView.newInstance())
                SettingsView::class.simpleName -> openFragment(SettingsView.newInstance())
            }
        } else {
            openFragment(AvailableView.newInstance())
        }

        toolbar = supportActionBar!!
        toolbar.setSubtitle(R.string.available_desc)

        bottomNavView = findViewById(R.id.bottom_navigation_view)
        bottomNavView.setOnItemSelectedListener {
            supportFragmentManager.popBackStackImmediate() // prevent something remaining on backstack
            when (it.itemId) {
                R.id.available_tab -> {
                    toolbar.setSubtitle(R.string.available_desc)
                    openFragment(AvailableView.newInstance())
                    return@setOnItemSelectedListener true
                }
                R.id.chats_tab -> {
                    toolbar.setSubtitle(R.string.chats_desc)
                    openFragment(ChatsView.newInstance())
                    return@setOnItemSelectedListener true
                }
                R.id.profile_tab -> {
                    toolbar.setSubtitle(R.string.profile_desc)
                    openFragment(ProfileView.newInstance())
                    return@setOnItemSelectedListener true
                }
                R.id.settings_tab -> {
                    toolbar.setSubtitle(R.string.settings_desc)
                    openFragment(SettingsView.newInstance())
                    return@setOnItemSelectedListener true
                }
            }
            PermissionManager.permissionCheck(this,this)
            return@setOnItemSelectedListener false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("currentFragmentName", currentFragment)
        super.onSaveInstanceState(outState)
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_layout, fragment)
        transaction.commit()
        currentFragment = fragment::class.simpleName!!
    }

    fun openChat(profile: Profile) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra(ChatActivity.INTENT_ADDRESS, profile.address)
        startActivity(intent)
    }

    fun getNightMode(): Boolean  {
        return sharedPreferences.getBoolean("night_mode", false)
    }

    fun toggleNightMode(boolean: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putBoolean("night_mode", boolean)
        editor.apply()
        updateNightMode()
    }

    fun updateNightMode() {
        if (sharedPreferences.getBoolean("night_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun getLanguage(): String {
        return sharedPreferences.getString("language", "default")!!
    }

    fun setLanguage(code: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("language", code)
        editor.apply()
    }

    private fun updateLanguage() {
        val languageSetting = sharedPreferences.getString("language", "default")
        val locale = if(languageSetting == null || languageSetting == "default") {
            Locale.getDefault()
        } else {
            Locale(languageSetting)
        }
        Locale.setDefault(locale)
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(
            config,
            resources.displayMetrics
        )
    }

    private fun fillDatabaseWithTestData() {
        var profile: Profile
        var message: Message
        for(i in 0..9) {
            profile = Profile("address-$i")
            profile.name = "name-$i"
            profile.color = i
            profile.description = "description-$i"
            if(i % 5 == 0) profile.unread = true
            for(j in 0..19) {
                val offset: Long = Timestamp.valueOf("2020-01-01 00:00:00").time
                val end: Long = Timestamp.valueOf("2020-03-03 00:00:00").time
                val diff = end - offset + 1
                val rand = Timestamp(offset + (Math.random() * diff).toLong())
                message = Message(profile.address, "message-$j from ${profile.name}", rand.time)
                if(j == 0) {
                    profile.lastInteraction = message.timeStamp
                }
                if(j % 3 == 0) {
                    message.isSelfAuthored = true
                    if(j % 2 == 0) message.isReceived = true
                }
                viewModel.addMessage(message)
            }

            if(i % 2 == 0) {
                profile.hopCount = i % 5
                profile.rssi = 120
                viewModel.updateAvailableProfile(profile)
            }
            viewModel.updateSavedProfile(profile)
        }
    }

    private fun clearDatabaseFromTestData() {
        for(i in 0..9) {
            viewModel.deleteSavedProfile("address-$i")
            viewModel.deleteMessages("address-$i")
            if(i % 2 == 0) viewModel.deleteAvailableProfile("address-$i")
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
}