package de.hsos.nearbychat.app.view

import android.content.Context
import android.content.res.Configuration
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
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var toolbar: ActionBar
    lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        updateLanguage(this)
        updateNightMode(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openFragment(AvailableView.newInstance())

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
        fun updateLanguage(context: Context) {
            val languageSetting = context.getSharedPreferences("APP_SETTINGS", MODE_PRIVATE).getString("language", "default")
            val locale = if(languageSetting == null || languageSetting == "default") {
                Locale.getDefault()
            } else {
                Locale(languageSetting)
            }
            Locale.setDefault(locale)
            val config: Configuration = context.resources.configuration
            config.setLocale(locale)
            context.resources.updateConfiguration(
                config,
                context.resources.displayMetrics
            )
        }

        fun updateNightMode(context: Context) {
            if (context.getSharedPreferences("APP_SETTINGS", MODE_PRIVATE).getBoolean("night_mode", false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        fun getUserColorRes(id: Int): Int {
            var color = R.id.profile_color
            when(id) {
                0 -> color = R.color.profile_0
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
    }
}