package de.hsos.nearbychat.app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.hsos.nearbychat.R

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "macAddress"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatsView.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatsView : Fragment() {
    private var macAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            macAddress = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats_view, container, false)
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
        fun newInstance(macAddress: String?) =
            ChatsView().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, macAddress)
                }
            }
    }
}