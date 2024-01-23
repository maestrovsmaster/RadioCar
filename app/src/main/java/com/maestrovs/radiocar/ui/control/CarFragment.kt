package com.maestrovs.radiocar.ui.control

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.common.CarLogoManager
import com.maestrovs.radiocar.common.Constants.CHECK_WEATHER_MINUTES_DELAY
import com.maestrovs.radiocar.common.CurrentCountryManager
import com.maestrovs.radiocar.databinding.FragmentCarBinding
import com.maestrovs.radiocar.enums.bluetooth.BT_Status
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.extensions.setVisible
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.ui.settings.SettingsManager
import com.maestrovs.radiocar.ui.settings.SpeedUnit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Math.round


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class CarFragment : Fragment() {

    private var _binding: FragmentCarBinding? = null


    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java].apply {

        }
    }

    private val controlViewModel by lazy {
        ViewModelProvider(this)[ControlViewModel::class.java].apply {

        }
    }




    private fun refreshMeasures() {
        binding.speedView.setUnit(
            when (SettingsManager.getSpeedUnit(requireContext())) {
                SpeedUnit.kmh -> getString(R.string.km_h)
                SpeedUnit.mph -> getString(R.string.mph)
            }
        )
        binding.weatherWidget.changeTemperatureUnit(SettingsManager.getTemperatureUnit(requireContext()))
    }


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var canFetchWeather = false

    private var isMusicPlaying = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (controlViewModel.lastLocation == null) {
            fetchCachedLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCarBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)









        mainViewModel.mustRefreshStatus.observe(viewLifecycleOwner) {
            val currentCountry = CurrentCountryManager.readCountry(requireContext())
            Log.d("Weather", "ttmainViewModel.currentCountry ${currentCountry?.name}")
            if (currentCountry != null) {
                controlViewModel.countryName = currentCountry.name
            }
            canFetchWeather = true
            controlViewModel.fetchWeather()
        }

        mainViewModel.location.observe(viewLifecycleOwner) { location ->
           // Log.d(
           //     "Weather",
           //     "ttmainViewModel.location ${location?.latitude}, ${location?.longitude}"
           // )
            if (location != null) {
                controlViewModel.setLocation(location)
                WeatherManager.setCurrentLocationCoords(requireContext(), location.getCoords2d())
            } else {
                fetchCachedLocation()
            }
            if (!canFetchWeather) {
                canFetchWeather = true
                controlViewModel.fetchWeather()
            }

        }

        mainViewModel.speed.observe(viewLifecycleOwner) { speed ->
            binding.speedView.setSpeedKmh(speed, SettingsManager.getSpeedUnit(requireContext()))
            val speed =
                SpeedManager.getSpeedForAnimation(speed) * (if (isMusicPlaying || speed > 3) {
                    1.0f
                } else {
                    0.0f
                })

           // Log.d("Orientation", "speed = $speed " +
                //    "isMusicPlaying=$isMusicPlaying")

            binding.animationView.speed = speed
        }

        mainViewModel.playAction.observe(viewLifecycleOwner) { playAction ->

            isMusicPlaying = playAction == PlayAction.Resume
            //Log.d("Orientation", "playAction = $playAction isMusicPlaying=$isMusicPlaying")
        }


        binding.weatherWidget.setOnClickListener {
            controlViewModel.fetchWeather()
        }



        controlViewModel.weatherResponse.observe(viewLifecycleOwner) {
            binding.weatherWidget.setWeatherResponse(
                it,
                SettingsManager.getTemperatureUnit(requireContext())
            )
        }

        controlViewModel.weatherError.observe(viewLifecycleOwner) {
            val msg = it ?: return@observe
            binding.weatherWidget.setWeatherError(msg)
        }

        CarLogoManager.readLogoResId(requireContext())?.let {
            binding.ivCarLogo?.setIconResource(it)
            binding.ivCarLogo?.setVisible(true)
        }?: kotlin.run {
            binding.ivCarLogo?.setVisible(false)
        }

        refreshMeasures()


        //Check Weather loop
        lifecycleScope.launch {
            while (true) {
                if (canFetchWeather) {
                    controlViewModel.fetchWeather()
                }
                delay(CHECK_WEATHER_MINUTES_DELAY * 60 * 1000L) // Delay for CHECK_WEATHER_MINUTES_DELAY minutes

            }
        }




    }

    private fun fetchCachedLocation() {
        val lastLocation = WeatherManager.getLastCoords(requireContext())
        if (lastLocation != null) {
            controlViewModel.lastLocation = lastLocation
        } else {
            val currentCountry = CurrentCountryManager.readCountry(requireContext())
            if (currentCountry != null) {
                controlViewModel.countryName = currentCountry.name
            }
        }
    }




    fun customRound(number: Double): Double {
        return if (number - number.toInt() >= 0.4) round(number).toDouble() else number.toInt()
            .toDouble() + 2
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}