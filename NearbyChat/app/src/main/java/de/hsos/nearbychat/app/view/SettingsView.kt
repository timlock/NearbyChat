package de.hsos.nearbychat.app.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import de.hsos.nearbychat.R

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsView.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_view, container, false);

        // dark mode
        val switch = view.findViewById<Switch>(R.id.settings_night_mode)
        val nightMode = (activity as MainActivity).getNightMode()
        switch.isChecked = nightMode
        switch.setOnClickListener {
            (activity as MainActivity).toggleNightMode(!nightMode)
            (activity as MainActivity).updateNightMode()
        }

        // language
        val spinner = view.findViewById<Spinner>(R.id.settings_language)
        var language = (activity as MainActivity).getLanguage()
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        when(language) {
            "en" -> {
                adapter.add(getString(R.string.language_en))
                adapter.add(getString(R.string.language_de))
                adapter.add(getString(R.string.system_default))
            }
            "de" -> {
                adapter.add(getString(R.string.language_de))
                adapter.add(getString(R.string.language_en))
                adapter.add(getString(R.string.system_default))
            }
            else -> {
                adapter.add(getString(R.string.system_default))
                adapter.add(getString(R.string.language_de))
                adapter.add(getString(R.string.language_en))
            }
        }
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                language = if(adapter.getItem(position) == getString(R.string.language_de)) {
                    "de"
                } else if(adapter.getItem(position) == getString(R.string.language_en)) {
                    "en"
                } else {
                    "default"
                }
                (activity as MainActivity).setLanguage(language)
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

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