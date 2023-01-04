package de.hsos.nearbychat.app.view

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.Profile


class MainActivity : AppCompatActivity() {

    lateinit var toolbar: ActionBar
    lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = supportActionBar!!
        toolbar.setSubtitle(R.string.available_desc)

        val nightModePreference = getSharedPreferences("NIGHT_MODE", MODE_PRIVATE)
        if (nightModePreference.getBoolean("night_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        bottomNavView = findViewById(R.id.bottom_navigation_view)
        bottomNavView.setOnItemSelectedListener {
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
            return@setOnItemSelectedListener false
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_layout, fragment)
        transaction.commit()
    }

    fun openChat(profile: Profile) {
        (bottomNavView.getChildAt(0) as BottomNavigationMenuView).getChildAt(1).callOnClick()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_layout, ChatView.newInstance(profile))
        transaction.addToBackStack(null)
        transaction.commit()
    }
    companion object {
        fun getExampleData(): MutableList<Profile> {
            // Testdaten
            val messageList = mutableListOf<Message>()
            messageList.add(Message("Message0", 1))
            var message: Message = Message("Message1", 2000000)
            message.isSelfAuthored = true
            message.isReceived = true
            messageList.add(message)
            messageList.add(Message("Message2", 2000000000))
            messageList.add(Message("Message3\ntest", System.currentTimeMillis()))
            message = Message("Message4", System.currentTimeMillis())
            message.isSelfAuthored = true
            messageList.add(message)

            val list = mutableListOf<Profile>()
            var profile = Profile("Mac-Address-1");
            profile.name = "Peter"
            profile.description = "ich bin der Peter"
            profile.messages = messageList
            profile.color = Color.parseColor("#FF0000")
            list.add(profile)
            profile = Profile("Mac-Address-2");
            profile.name = "Hans"
            profile.description = "ich bin der Hans"
            profile.messages = messageList
            profile.isAvailable = true
            profile.color = Color.parseColor("#00FF00")
            list.add(profile)
            profile = Profile("Mac-Address-3");
            profile.name = "Jürgen"
            profile.description = "ich bin der Jürgen"
            profile.messages = messageList
            profile.color = Color.parseColor("#0000FF")
            list.add(profile)

            return list
        }
    }
}