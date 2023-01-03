package de.hsos.nearbychat.app.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import de.hsos.nearbychat.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsView.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsView : Fragment() {

    var night_mode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_view, container, false);
        val switch = view.findViewById<Switch>(R.id.settings_night_mode);
        val nightModePreference = requireContext().getSharedPreferences("NIGHT_MODE", AppCompatActivity.MODE_PRIVATE)
        if (nightModePreference != null) {
            night_mode = nightModePreference.getBoolean("night_mode", false)
            switch.isChecked = night_mode;
        }
        switch.setOnClickListener {
            night_mode = !night_mode
            if (night_mode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            val nightModePreference = requireContext().getSharedPreferences("NIGHT_MODE", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = nightModePreference.edit()
            editor.putBoolean("night_mode", night_mode)
            editor.apply()
        }

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
            SettingsView().apply {

            }
    }
}