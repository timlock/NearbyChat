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
import de.hsos.nearbychat.app.viewmodel.ViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileView.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileView : Fragment() {

    private lateinit var colorPreview: Button

    private val viewModel: ViewModel by viewModels {
        ViewModel.ViewModelFactory(
            (activity?.application as Application).repository,
            activity?.application as Application
        )
    }

    var name: String = ""
    var description: String = ""
    var color: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_view, container, false)

        if(savedInstanceState == null) {
            viewModel.ownProfile.observe(viewLifecycleOwner) { profiles ->
                profiles.let {
                    if (it != null) {
                        name = it.name
                        description = it.description
                        color = it.color
                    }
                    loadProfile(view)
                }
            }
        } else {
            name = savedInstanceState.getString("PROFILE_NAME")!!
            description = savedInstanceState.getString("PROFILE_DESCRIPTION")!!
            color = savedInstanceState.getInt("PROFILE_COLOR")
            loadProfile(view)
        }

        return view
    }

    private fun loadProfile( view: View) {
        val nameView = view.findViewById<TextView>(R.id.profile_name)
        nameView.text = name
        nameView.addTextChangedListener {
            name = it.toString()
        }

        val descView = view.findViewById<TextView>(R.id.profile_desc)
        descView.text = description
        descView.addTextChangedListener {
            description = it.toString()
        }

        colorPreview = view.findViewById(R.id.profile_color)
        changeColor(color)

        view.findViewById<Button>(R.id.profile_color_0).setOnClickListener{
            changeColor(0)
        }
        view.findViewById<Button>(R.id.profile_color_1).setOnClickListener{
            changeColor(1)
        }
        view.findViewById<Button>(R.id.profile_color_2).setOnClickListener{
            changeColor(2)
        }
        view.findViewById<Button>(R.id.profile_color_3).setOnClickListener{
            changeColor(3)
        }
        view.findViewById<Button>(R.id.profile_color_4).setOnClickListener{
            changeColor(4)
        }
        view.findViewById<Button>(R.id.profile_color_5).setOnClickListener{
            changeColor(5)
        }
        view.findViewById<Button>(R.id.profile_color_6).setOnClickListener{
            changeColor(6)
        }
        view.findViewById<Button>(R.id.profile_color_7).setOnClickListener{
            changeColor(7)
        }
        view.findViewById<Button>(R.id.profile_color_8).setOnClickListener{
            changeColor(8)
        }
        view.findViewById<Button>(R.id.profile_color_9).setOnClickListener{
            changeColor(9)
        }

        view.findViewById<Button>(R.id.profile_save).setOnClickListener {
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
        outState.putString("PROFILE_NAME", name)
        outState.putString("PROFILE_DESCRIPTION", description)
        outState.putInt("PROFILE_COLOR", color)
        super.onSaveInstanceState(outState)
    }

    private fun changeColor(id: Int) {
        color = id
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