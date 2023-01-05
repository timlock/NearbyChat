package de.hsos.nearbychat.app.view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import de.hsos.nearbychat.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileView.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileView : Fragment() {

    private lateinit var colorPreview: Button
    private var colorId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_view, container, false)

        colorPreview = view.findViewById(R.id.profile_color)

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
        return view
    }

    private fun changeColor(id: Int) {
        colorId = id
        colorPreview.setBackgroundColor(ContextCompat.getColor(requireContext(), MainActivity.getUserColorRes(id)))
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