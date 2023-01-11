package de.hsos.nearbychat.app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
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

        when((activity as MainActivity).getAppTheme()) {
            "dark" -> view.findViewById<RadioButton>(R.id.settings_theme_dark).isChecked = true
            "light" -> view.findViewById<RadioButton>(R.id.settings_theme_light).isChecked = true
            else -> view.findViewById<RadioButton>(R.id.settings_theme_default).isChecked = true
        }

        when((activity as MainActivity).getAppLanguage()) {
            "en" -> view.findViewById<RadioButton>(R.id.settings_language_en).isChecked = true
            "de" -> view.findViewById<RadioButton>(R.id.settings_language_de).isChecked = true
            else -> view.findViewById<RadioButton>(R.id.settings_language_default).isChecked = true
        }

        val themeRadio = view.findViewById<RadioGroup>(R.id.settings_theme)
        val languageRadio = view.findViewById<RadioGroup>(R.id.settings_language)

        themeRadio.setOnCheckedChangeListener {_, id ->
            when(id) {
                R.id.settings_theme_dark -> (activity as MainActivity).setAppTheme("dark")
                R.id.settings_theme_light -> (activity as MainActivity).setAppTheme("light")
                else -> (activity as MainActivity).setAppTheme("default")
            }
        }

        languageRadio.setOnCheckedChangeListener {_, id ->
            when(id) {
                R.id.settings_language_en -> (activity as MainActivity).setAppLanguage("en")
                R.id.settings_language_de-> (activity as MainActivity).setAppLanguage("de")
                else -> (activity as MainActivity).setAppLanguage("default")
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