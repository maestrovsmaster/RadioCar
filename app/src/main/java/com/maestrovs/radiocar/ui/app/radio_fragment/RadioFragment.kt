package com.maestrovs.radiocar.ui.app.radio_fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.RadioScreen

import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.WeatherViewModel
import com.maestrovs.radiocar.ui.app.stations_list.RadioListScreen
import com.maestrovs.radiocar.ui.app.stations_list.RadioListViewModel
import com.maestrovs.radiocar.ui.main.MainActivity
import com.maestrovs.radiocar.ui.splash_start_fragment.SplashFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by maestromaster on 10/02/2025.
 */

@AndroidEntryPoint
class RadioFragment : Fragment() {

    private val viewModel: RadioViewModel by activityViewModels()
    private val radioListViewModel : RadioListViewModel by viewModels()
    private val weatherViewModel: WeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

       // findNavController().navigate(RadioFragmentDirections.actionRadioFragmentToRadioListFragment())

        return ComposeView(requireContext()).apply {
            setContent {
                val navController = findNavController()
                RadioScreen(viewModel, weatherViewModel ,navController = navController,
                    onSelectAllClick = {
                         navController.navigate(RadioFragmentDirections.actionRadioFragmentToRadioListFragment())
                    },
                    onSelectNavigationClick = {
                        openGoogleMapsNavigation()
                    },
                    onSelectPhotoClick = {
                        openPhoneIntent()
                    }
                )

                //RadioListScreen(viewModel = radioListViewModel)
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun openGoogleMapsNavigation() {
        val mainActivity = (requireActivity() as MainActivity)
        mainActivity.enterPiPMode()
        //val gmmIntentUri = Uri.parse("google.navigation:q=50.4501,30.5234") // Київ як приклад
        val gmmIntentUri = Uri.parse("google.navigation:q=&zoom=17")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

       /* val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)*/
    }

    @OptIn(UnstableApi::class)
    fun openPhoneIntent() {
        viewModel.stop()

        val mainActivity = (requireActivity() as MainActivity)
        mainActivity.enterPiPMode()
        val intent = Intent(Intent.ACTION_DIAL) // Відкриває екран набору номера
        startActivity(intent)
    }

}
