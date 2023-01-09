package de.hsos.nearbychat.app.view

import MessageAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.application.Application
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.app.viewmodel.ViewModel

class ChatActivity : AppCompatActivity() {

    private val viewModel: ViewModel by viewModels {
        ViewModel.ViewModelFactory((application as Application).repository)
    }

    private var profile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.setSubtitle(R.string.chat_desc)

        val address = intent.extras?.getString(INTENT_ADDRESS) ?: return
        val recyclerView: RecyclerView = findViewById(R.id.chat_messages_recycler)

        viewModel.savedProfiles.observe(this) {
            it.forEach { profile ->
                if(profile.address == address) {
                    this.profile = profile

                    findViewById<TextView>(R.id.chat_user_name).text = profile.name
                    findViewById<TextView>(R.id.chat_user_message).text = profile.description

                    val symbol = findViewById<ImageView>(R.id.chat_user_symbol)
                    val signalStrength = findViewById<ImageView>(R.id.chat_user_signal_strength)
                    symbol.setColorFilter(
                        ResourcesCompat.getColor(resources,
                            MainActivity.getUserColorRes(profile.color), null
                        ))
                    signalStrength.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this,
                            MainActivity.getSignalStrengthIcon(profile.signalStrength0to4())
                        )
                    )
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = MessageAdapter(this)

        viewModel.getMessages(address).observe(this) { messages ->
            messages.let { adapter.messages = messages }
        }

        recyclerView.adapter = adapter
    }

    override fun onPause() {
        if(profile != null && profile!!.unread) {
            profile!!.unread = false
            viewModel.updateSavedProfile(profile!!)
        }
        super.onPause()
    }

    companion object {
        const val INTENT_ADDRESS = "address"
    }
}