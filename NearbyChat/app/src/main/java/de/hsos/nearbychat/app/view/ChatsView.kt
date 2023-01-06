package de.hsos.nearbychat.app.view

import ChatUserAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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
        val chatUserAdapter = ChatUserAdapter(MainActivity.getExampleData()){
            (activity as MainActivity).openChat(it!!)
        }
        recyclerView.adapter = chatUserAdapter

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }
            
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                //TODO: remove from list
                chatUserAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                Snackbar.make(recyclerView, R.string.deleted_chat, Snackbar.LENGTH_LONG)
                .setAction(
                    R.string.undo,
                    View.OnClickListener {
                    //TODO: add to list
                    chatUserAdapter.notifyItemInserted(position)
                    }).show()
                }
            }).attachToRecyclerView(recyclerView)

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