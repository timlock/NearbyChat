package de.hsos.nearbychat.app.view

import MessageAdapter
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
                if (profile.address == address) {
                    this.profile = profile

                    findViewById<TextView>(R.id.chat_user_name).text = profile.name
                    findViewById<TextView>(R.id.chat_user_message).text = profile.description

                    val symbol = findViewById<ImageView>(R.id.chat_user_symbol)
                    val signalStrength = findViewById<ImageView>(R.id.chat_user_signal_strength)
                    symbol.setColorFilter(
                        ResourcesCompat.getColor(
                            resources,
                            Application.getUserColorRes(profile.color), null
                        )
                    )
                    signalStrength.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this,
                            Application.getSignalStrengthIcon(profile.signalStrength0to4())
                        )
                    )
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        //layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager

        val adapter = MessageAdapter(this)

        viewModel.getMessages(address).observe(this) { messages ->
            messages.let {
                adapter.messages = messages
                if( recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent() - recyclerView.computeVerticalScrollOffset() < 50) {
                    recyclerView.scrollToPosition(adapter.messages.size - 1)
                    checkUnread()
                }
            }
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if( recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent() - recyclerView.computeVerticalScrollOffset() < 50) {
                    checkUnread()
                }
            }

        })

        recyclerView.adapter = adapter
    }

    private fun checkUnread() {
        if (profile != null && profile!!.isUnread) {
            profile!!.isUnread = false
            viewModel.updateSavedProfile(profile!!)
        }
    }

    companion object {
        const val INTENT_ADDRESS = "address"
    }
}