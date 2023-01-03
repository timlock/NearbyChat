package de.hsos.nearbychat.app.view

import AvailableUserAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.domain.Profile

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AvailableView.newInstance] factory method to
 * create an instance of this fragment.
 */
class AvailableView : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_available_view, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.available_user_recycler)

        val list = mutableListOf<Profile>()
        var profile = Profile("Mac-Address-1");
        profile.name = "Peter"
        profile.description = "ich bin der Peter"
        list.add(profile)
        profile = Profile("Mac-Address-2");
        profile.name = "Hans"
        profile.description = "ich bin der Hans"
        list.add(profile)
        profile = Profile("Mac-Address-3");
        profile.name = "Jürgen"
        profile.description = "ich bin der Jürgen"
        list.add(profile)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = AvailableUserAdapter(list)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment HomeView.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            AvailableView().apply {
            }
    }
}