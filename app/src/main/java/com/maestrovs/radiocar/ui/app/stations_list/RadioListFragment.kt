package com.maestrovs.radiocar.ui.app.stations_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController

import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by maestromaster on 10/02/2025.
 */

@AndroidEntryPoint
class RadioListFragment : Fragment() {

    private val viewModel: RadioViewModel by viewModels()
    private val radioListViewModel : RadioListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return ComposeView(requireContext()).apply {
            setContent {
                val navController = findNavController()
                //RadioScreen(viewModel, navController = navController)

                RadioListScreen(viewModel = radioListViewModel, onBackClick = {
                    navController.popBackStack()
                })
            }
        }
    }
}
