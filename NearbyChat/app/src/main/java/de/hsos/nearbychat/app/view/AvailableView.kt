package de.hsos.nearbychat.app.view

import AvailableUserAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.application.Application
import de.hsos.nearbychat.app.viewmodel.ViewModel
import de.hsos.nearbychat.app.viewmodel.ViewModel.ViewModelFactory


/**
 * A simple [Fragment] subclass.
 * Use the [AvailableView.newInstance] factory method to
 * create an instance of this fragment.
 */
class AvailableView : Fragment() {

    private val viewModel: ViewModel by viewModels {
        ViewModelFactory((activity?.application as Application).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_available_view, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.available_user_recycler)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        val adapter = AvailableUserAdapter() {
            (activity as MainActivity).openChat(it!!)
        }

        viewModel.availableProfiles.observe(viewLifecycleOwner) { profiles ->
            profiles.let { adapter.availableProfiles = profiles }
        }

        recyclerView.adapter = adapter

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
        fun newInstance() =
            AvailableView().apply {
            }
    }
}