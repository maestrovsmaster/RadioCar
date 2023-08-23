package com.maestrovs.radiocar.ui.control

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.common.Constants.CHECK_WEATHER_MINUTES_DELAY
import com.maestrovs.radiocar.common.CurrentCountryManager
import com.maestrovs.radiocar.databinding.FragmentControlBinding
import com.maestrovs.radiocar.enums.bluetooth.BT_Status
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.ui.settings.KEY_SETTINGS_INPUT_MESSAGE
import com.maestrovs.radiocar.ui.settings.KEY_SETTINGS_RESULT_MESSAGE
import com.maestrovs.radiocar.ui.settings.SettingsActivity
import com.maestrovs.radiocar.ui.settings.ui.main.SettingsManager
import com.maestrovs.radiocar.ui.settings.ui.main.SpeedUnit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_control.speedView
import kotlinx.android.synthetic.main.fragment_control.weatherWidget
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Math.round


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class ControlFragment : Fragment() {

    private var _binding: FragmentControlBinding? = null


    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java].apply {

        }
    }

    private val controlViewModel by lazy {
        ViewModelProvider(this)[ControlViewModel::class.java].apply {

        }
    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("SettingsActivity", "On result ...")
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val resultMessage = data?.getStringExtra(KEY_SETTINGS_RESULT_MESSAGE)
                Log.d("SettingsActivity", "On result = $resultMessage")
                mainViewModel.setMustRefreshStatus()
                refreshMeasures()
            }
        }

    private fun refreshMeasures() {
        speedView.setUnit(
            when (SettingsManager.getSpeedUnit(requireContext())) {
                SpeedUnit.kmh -> getString(R.string.km_h)
                SpeedUnit.mph -> getString(R.string.mph)
            }
        )
        weatherWidget.changeTemperatureUnit(SettingsManager.getTemperatureUnit(requireContext()))
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

        _binding = FragmentControlBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btNavigation.setOnClickListener {
            launchMapIntent()
        }

        binding.btPhone.setOnClickListener {
            launchPhoneIntent()
        }

        binding.btBluetooth.setOnClickListener {
            launchBluetoothIntent()
        }

        binding.btSettings.setOnClickListener {
            launchSettingsIntent()
        }



        mainViewModel.bluetoothStatus.observe(viewLifecycleOwner) { bt_Status ->
            bt_Status ?: return@observe

            val icon = when (bt_Status) {
                BT_Status.Enabled -> R.drawable.ic_bt_on
                BT_Status.ConnectedDevice -> R.drawable.ic_bluetooth_connected
                BT_Status.Disable -> R.drawable.ic_bt_off
                BT_Status.DisconnectedDevice -> R.drawable.ic_bt_on
            }
            binding.btBluetooth.setIconResource(icon)
        }

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

    private fun launchMapIntent() {
        var gmmIntentUri = Uri.parse("geo:0,0")
        //     Uri.parse("geo:0,0?q=?")

        mainViewModel.location.value?.let {
            gmmIntentUri = Uri.parse("geo:${it.latitude},${it.longitude}")
        }
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun launchPhoneIntent() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "", null))
        startActivity(intent)
    }


    private fun launchBluetoothIntent() {
        val intentOpenBluetoothSettings = Intent()
        intentOpenBluetoothSettings.action = Settings.ACTION_BLUETOOTH_SETTINGS
        startActivity(intentOpenBluetoothSettings)
    }


    private fun launchSettingsIntent() {
        val intent = SettingsActivity.newIntent(requireContext()).apply {
            putExtra(KEY_SETTINGS_INPUT_MESSAGE, "")
        }
        launcher.launch(intent)
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