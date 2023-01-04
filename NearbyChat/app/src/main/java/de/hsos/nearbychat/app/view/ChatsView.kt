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
import de.hsos.nearbychat.app.domain.Message
import de.hsos.nearbychat.app.domain.Profile

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

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = ChatUserAdapter(MainActivity.getExampleData()){
            (activity as MainActivity).openChat(it!!)
        }

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