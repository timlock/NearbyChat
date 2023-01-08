package de.hsos.nearbychat.app.view

import ChatAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.application.Application
import de.hsos.nearbychat.app.domain.Profile
import de.hsos.nearbychat.app.viewmodel.ViewModel

private const val ARG_PARAM1 = "address"

class ChatView : Fragment() {

    private lateinit var address: String
    private var profile: Profile? = null

    private val viewModel: ViewModel by viewModels {
        ViewModel.ViewModelFactory((activity?.application as Application).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        address = requireArguments().getString(ARG_PARAM1)!!
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_chat_view, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.chat_messages_recycler)

        viewModel.getSavedProfile(address).observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.chat_user_name).text = it.name
            view.findViewById<TextView>(R.id.chat_user_message).text = it.description

            val symbol = view.findViewById<ImageView>(R.id.chat_user_symbol)
            val signalStrength = view.findViewById<ImageView>(R.id.chat_user_signal_strength)
            symbol.setColorFilter(
                ResourcesCompat.getColor(requireContext().resources,
                    MainActivity.getUserColorRes(it.color), null
                ))
            signalStrength.setColorFilter(
                ResourcesCompat.getColor(requireContext().resources,
                    MainActivity.getUserColorRes(it.color), null
                ))
            signalStrength.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    MainActivity.getSignalStrengthIcon(it.signalStrength0to4())
                )
            )
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)

        val adapter = ChatAdapter(requireContext())

        viewModel.getMessages(address).observe(viewLifecycleOwner) { messages ->
            messages.let { adapter.messages = messages }
        }

        recyclerView.adapter = adapter

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(address: String) =
            ChatView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, address)
                }
            }
    }
}