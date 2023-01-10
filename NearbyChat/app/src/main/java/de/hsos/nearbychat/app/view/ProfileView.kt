package de.hsos.nearbychat.app.view

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import de.hsos.nearbychat.R
import de.hsos.nearbychat.app.application.NearbyApplication
import de.hsos.nearbychat.app.viewmodel.ViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileView.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileView : Fragment() {

    private val viewModel: ViewModel by viewModels {
        ViewModel.ViewModelFactory(
            (activity?.application as NearbyApplication).repository,
            activity?.application as NearbyApplication
        )
    }

    private lateinit var colorPreview: Button
    var color: Int = 0
    set(value) {
        colorPreview.setBackgroundColor(ContextCompat.getColor(requireContext(), NearbyApplication.getUserColorRes(value)))
        field = value
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_view, container, false)
        colorPreview = view.findViewById(R.id.profile_color)

        if(savedInstanceState == null) {
            viewModel.ownProfile.observe(viewLifecycleOwner) { profiles ->
                profiles.let {
                    if (it != null) {
                        view.findViewById<TextView>(R.id.profile_name).text = it.name
                        view.findViewById<TextView>(R.id.profile_desc).text = it.description
                        color = it.color
                    }
                    loadProfile(view)
                }
            }
        } else {
            color = savedInstanceState.getInt("PROFILE_COLOR")
        }

        return view
    }

    private fun loadProfile( view: View) {


        view.findViewById<Button>(R.id.profile_color_0).setOnClickListener{
            color = 0
        }
        view.findViewById<Button>(R.id.profile_color_1).setOnClickListener{
            color = 1
        }
        view.findViewById<Button>(R.id.profile_color_2).setOnClickListener{
            color = 2
        }
        view.findViewById<Button>(R.id.profile_color_3).setOnClickListener{
            color = 3
        }
        view.findViewById<Button>(R.id.profile_color_4).setOnClickListener{
            color = 4
        }
        view.findViewById<Button>(R.id.profile_color_5).setOnClickListener{
            color = 5
        }
        view.findViewById<Button>(R.id.profile_color_6).setOnClickListener{
            color = 6
        }
        view.findViewById<Button>(R.id.profile_color_7).setOnClickListener{
            color = 7
        }
        view.findViewById<Button>(R.id.profile_color_8).setOnClickListener{
            color = 8
        }
        view.findViewById<Button>(R.id.profile_color_9).setOnClickListener{
            color = 9
        }

        view.findViewById<Button>(R.id.profile_save).setOnClickListener {
            val name = view.findViewById<TextView>(R.id.profile_name).text.toString()
            val description = view.findViewById<TextView>(R.id.profile_desc).text.toString()
            if(name.isEmpty() || description.isEmpty()) {
                Snackbar.make(view, R.string.profile_parts_empty, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.updateOwnProfile(name, description, color)
            // hide keyboard
            val inputMethodManager: InputMethodManager =
                requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isAcceptingText) {
                inputMethodManager.hideSoftInputFromWindow(
                    requireActivity().currentFocus!!.windowToken,
                    0
                )
            }
            Snackbar.make(view, R.string.saved_profile, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("PROFILE_COLOR", color)
        super.onSaveInstanceState(outState)
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
            ProfileView().apply {
            }
    }
}