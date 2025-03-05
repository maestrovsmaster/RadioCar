package com.maestrovs.radiocar.ui.app.pip_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.WeatherViewModel
import com.maestrovs.radiocar.ui.app.stations_list.RadioListViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by maestromaster on 10/02/2025.
 */

@AndroidEntryPoint
class PiPFragment : Fragment() {

    private val viewModel: RadioViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

       // findNavController().navigate(RadioFragmentDirections.actionRadioFragmentToRadioListFragment())

        return ComposeView(requireContext()).apply {
            setContent {
                PiPScreen(viewModel)
            }
        }
    }
}
