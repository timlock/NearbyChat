package de.hsos.nearbychat.app.view

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.PermissionManager
import de.hsos.nearbychat.app.application.NearbyApplication
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.app.viewmodel.ViewModel
import java.sql.Timestamp
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: ActionBar
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var sharedPreferences: SharedPreferences

    private val viewModel: ViewModel by viewModels {
        ViewModel.ViewModelFactory((application as NearbyApplication).repository, application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("APP_SETTINGS", MODE_PRIVATE)

        toolbar = supportActionBar!!
        
        //clearDatabaseFromTestData()
        //fillDatabaseWithTestData()

        updateLanguage()
        updateNightMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null) {
            openFragment(AvailableView.newInstance())
        }


        bottomNavView = findViewById(R.id.bottom_navigation_view)
        bottomNavView.setOnItemSelectedListener {
            supportFragmentManager.popBackStackImmediate() // prevent something remaining on backstack
            when (it.itemId) {
                R.id.available_tab -> {
                    openFragment(AvailableView.newInstance())
                    return@setOnItemSelectedListener true
                }
                R.id.chats_tab -> {
                    openFragment(ChatsView.newInstance())
                    return@setOnItemSelectedListener true
                }
                R.id.profile_tab -> {
                    openFragment(ProfileView.newInstance())
                    return@setOnItemSelectedListener true
                }
                R.id.settings_tab -> {
                    openFragment(SettingsView.newInstance())
                    return@setOnItemSelectedListener true
                }
            }
            PermissionManager.permissionCheck(this,this)
            return@setOnItemSelectedListener false
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_layout, fragment)
        transaction.commit()
        when(fragment) {
            is AvailableView -> toolbar.setSubtitle(R.string.available_desc)
            is ChatsView -> toolbar.setSubtitle(R.string.chats_desc)
            is ProfileView -> toolbar.setSubtitle(R.string.profile_desc)
            is SettingsView -> toolbar.setSubtitle(R.string.settings_desc)
        }
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
        for(i in 0..29) {
            profile = Profile("address-$i")
            profile.name = "name-$i"
            profile.color = i % 10
            profile.description = "description-$i"
            if(i % 5 == 0) profile.isUnread = true
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
        for(i in 0..29) {
            viewModel.deleteSavedProfile("address-$i")
            viewModel.deleteMessages("address-$i")
            if(i % 2 == 0) viewModel.deleteAvailableProfile("address-$i")
        }
    }
}