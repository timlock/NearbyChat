package de.hsos.nearbychat.app.view

import MessageAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.application.Application
import de.hsos.nearbychat.app.viewmodel.ViewModel

class ChatActivity : AppCompatActivity() {

    private val viewModel: ViewModel by viewModels {
        ViewModel.ViewModelFactory((application as Application).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar!!.setSubtitle(R.string.chat_desc)

        val address = intent.extras?.getString(INTENT_ADDRESS) ?: return
        val recyclerView: RecyclerView = findViewById(R.id.chat_messages_recycler)

        viewModel.savedProfiles.observe(this) {
            it.forEach {
                if(it.address == address) {
                    findViewById<TextView>(R.id.chat_user_name).text = it.name
                    findViewById<TextView>(R.id.chat_user_message).text = it.description

                    val symbol = findViewById<ImageView>(R.id.chat_user_symbol)
                    val signalStrength = findViewById<ImageView>(R.id.chat_user_signal_strength)
                    symbol.setColorFilter(
                        ResourcesCompat.getColor(resources,
                            MainActivity.getUserColorRes(it.color), null
                        ))
                    signalStrength.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this,
                            MainActivity.getSignalStrengthIcon(it.signalStrength0to4())
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

    companion object {
        const val INTENT_ADDRESS = "address"
    }
}