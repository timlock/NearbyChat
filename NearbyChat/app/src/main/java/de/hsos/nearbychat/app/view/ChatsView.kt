package de.hsos.nearbychat.app.view

import ChatUserAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.domain.Profile

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "macAddress"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatsView.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatsView : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_chats_view, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.chat_user_recycler)

        val list = mutableListOf<Profile>()
        var profile = Profile("Mac-Address-1");
        profile.name = "Peter"
        list.add(profile)
        profile = Profile("Mac-Address-2");
        profile.name = "Hans"
        list.add(profile)
        profile = Profile("Mac-Address-3");
        profile.name = "Jürgen"
        list.add(profile)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = ChatUserAdapter(list)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param macAddress mac address of Chat.
         * @return A new instance of fragment HomeView.
         */
        @JvmStatic
        fun newInstance() =
            ChatsView().apply {
            }
    }
}