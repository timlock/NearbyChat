package de.hsos.nearbychat.app.view

import ChatAdapter
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.Profile
import java.time.Instant

private const val ARG_PARAM1 = "macAddress"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatView.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatView : Fragment() {

    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        profile = requireArguments().getParcelable(ARG_PARAM1)!!
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_chat_view, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.chat_messages_recycler)

        view.findViewById<TextView>(R.id.chat_user_name).text = profile.name
        view.findViewById<TextView>(R.id.chat_user_message).text = profile.description

        val symbol = view.findViewById<ImageView>(R.id.chat_user_symbol)
        val signalStrength = view.findViewById<ImageView>(R.id.chat_user_signal_strength)
        symbol.setColorFilter(
            ResourcesCompat.getColor(requireContext().resources,
                MainActivity.getUserColorRes(profile.color), null
            ))
        signalStrength.setColorFilter(
            ResourcesCompat.getColor(requireContext().resources,
                MainActivity.getUserColorRes(profile.color), null
            ))
        if(profile.isAvailable) {
            signalStrength.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_baseline_network_wifi_3_bar_24 //TODO: an signalstärke anpassen
                    )
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = ChatAdapter(profile.messages, requireContext())

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment HomeView.
         */
        @JvmStatic
        fun newInstance(profile: Profile) =
            ChatView().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, profile)
                }
            }
    }
}