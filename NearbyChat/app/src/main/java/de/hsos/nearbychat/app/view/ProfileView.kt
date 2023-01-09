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
import de.hsos.nearbychat.app.application.Application
import de.hsos.nearbychat.app.domain.OwnProfile
import de.hsos.nearbychat.app.viewmodel.ViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileView.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileView : Fragment() {

    private lateinit var colorPreview: Button

    private val viewModel: ViewModel by viewModels {
        ViewModel.ViewModelFactory((activity?.application as Application).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_view, container, false)


        viewModel.ownProfile.observe(viewLifecycleOwner) { profiles ->
            profiles.let {
                lateinit var profile: OwnProfile
                if(it != null) {
                    profile = it
                } else {
                    profile = OwnProfile()
                }
                val nameView = view.findViewById<TextView>(R.id.profile_name)
                nameView.text = profile.name
                nameView.addTextChangedListener {
                    profile.name = it.toString()
                }

                val descView = view.findViewById<TextView>(R.id.profile_desc)
                descView.text = profile.description
                descView.addTextChangedListener {
                    profile.description = it.toString()
                }

                colorPreview = view.findViewById(R.id.profile_color)
                changeColor(profile.color, profile)

                view.findViewById<Button>(R.id.profile_color_0).setOnClickListener{
                    changeColor(0, profile)
                }
                view.findViewById<Button>(R.id.profile_color_1).setOnClickListener{
                    changeColor(1, profile)
                }
                view.findViewById<Button>(R.id.profile_color_2).setOnClickListener{
                    changeColor(2, profile)
                }
                view.findViewById<Button>(R.id.profile_color_3).setOnClickListener{
                    changeColor(3, profile)
                }
                view.findViewById<Button>(R.id.profile_color_4).setOnClickListener{
                    changeColor(4, profile)
                }
                view.findViewById<Button>(R.id.profile_color_5).setOnClickListener{
                    changeColor(5, profile)
                }
                view.findViewById<Button>(R.id.profile_color_6).setOnClickListener{
                    changeColor(6, profile)
                }
                view.findViewById<Button>(R.id.profile_color_7).setOnClickListener{
                    changeColor(7, profile)
                }
                view.findViewById<Button>(R.id.profile_color_8).setOnClickListener{
                    changeColor(8, profile)
                }
                view.findViewById<Button>(R.id.profile_color_9).setOnClickListener{
                    changeColor(9, profile)
                }

                view.findViewById<Button>(R.id.profile_save).setOnClickListener{
                    viewModel.updateOwnProfile(profile)
                    // hide keyboard
                    val inputMethodManager: InputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isAcceptingText) {
                        inputMethodManager.hideSoftInputFromWindow( requireActivity().currentFocus!!.windowToken, 0)
                    }
                    Snackbar.make(view, R.string.saved_profile, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        return view
    }

    private fun changeColor(id: Int, profile: OwnProfile) {
        profile.color = id
        colorPreview.setBackgroundColor(ContextCompat.getColor(requireContext(), Application.getUserColorRes(id)))
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