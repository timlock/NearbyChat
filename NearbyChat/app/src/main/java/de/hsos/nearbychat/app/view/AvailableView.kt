package de.hsos.nearbychat.app.view

import AvailableUserAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.application.NearbyApplication
import de.hsos.nearbychat.app.viewmodel.ViewModel
import de.hsos.nearbychat.app.viewmodel.ViewModel.ViewModelFactory


class AvailableView : Fragment() {

    private val viewModel: ViewModel by viewModels {
        ViewModelFactory((activity?.application as NearbyApplication).repository,
            activity?.application as NearbyApplication
        )
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_available_view, container, false)
        recyclerView = view.findViewById(R.id.available_user_recycler)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        val adapter = AvailableUserAdapter {
            (activity as MainActivity).openChat(it!!, true)
        }

        viewModel.availableProfiles.observe(viewLifecycleOwner) { profiles ->
            profiles.let {
                adapter.availableProfiles = profiles.sortedByDescending {
                    it.getSignalStrength()
                }
            }
        }

        recyclerView.adapter = adapter

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AvailableView().apply {
            }
    }
}