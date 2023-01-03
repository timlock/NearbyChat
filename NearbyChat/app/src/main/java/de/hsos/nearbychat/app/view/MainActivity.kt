package de.hsos.nearbychat.app.view

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hsos.nearbychat.R


class MainActivity : AppCompatActivity() {

    lateinit var toolbar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = supportActionBar!!
        toolbar.setSubtitle(R.string.available_desc)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
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


}