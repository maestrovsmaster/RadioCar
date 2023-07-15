package com.maestrovs.radiocar.ui.settings.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hbb20.countrypicker.dialog.launchCountryPickerDialog
import com.hbb20.countrypicker.models.CPCountry
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.common.CurrentCountryManager
import com.maestrovs.radiocar.databinding.FragmentSettingsMainBinding
import com.maestrovs.radiocar.ui.settings.KEY_SETTINGS_RESULT_MESSAGE


class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private var _binding: FragmentSettingsMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btClose.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(KEY_SETTINGS_RESULT_MESSAGE, "Save")
            }
            requireActivity().setResult(Activity.RESULT_OK, resultIntent)
            requireActivity().finish()
        }



        binding.swDisplayActive.setOnCheckedChangeListener(null)
        binding.swDisplayActive.isChecked = SettingsManager.isDisplayActive(requireContext())
        binding.swDisplayActive.setOnCheckedChangeListener { _, isChecked ->
            SettingsManager.setDisplayActive(requireContext(), isChecked)
        }

        binding.swAutoplay.setOnCheckedChangeListener(null)
        binding.swAutoplay.isChecked = SettingsManager.isAutoplay(requireContext())
        binding.swAutoplay.setOnCheckedChangeListener { _, isChecked ->
            SettingsManager.setAutoplay(requireContext(), isChecked)
        }

        val speedUnit = SettingsManager.getSpeedUnit(requireContext())

        binding.rbKmh.isChecked = speedUnit == SpeedUnit.kmh
        binding.rbKmh.setOnClickListener {
            SettingsManager.setSpeedUnit(
                requireContext(),
                SpeedUnit.kmh
            )
        }

        binding.rbMph.isChecked = speedUnit == SpeedUnit.mph
        binding.rbMph.setOnClickListener {
            SettingsManager.setSpeedUnit(
                requireContext(),
                SpeedUnit.mph
            )
        }

        val temperatureUnit = SettingsManager.getTemperatureUnit(requireContext())
        binding.rbCelsium.isChecked = temperatureUnit == TemperatureUnit.C
        binding.rbCelsium.setOnClickListener {
            SettingsManager.setTemperatureUnit(
                requireContext(),
                TemperatureUnit.C
            )
        }

        binding.rbFahrenheit.isChecked = temperatureUnit == TemperatureUnit.F
        binding.rbFahrenheit.setOnClickListener {
            SettingsManager.setTemperatureUnit(
                requireContext(),
                TemperatureUnit.F
            )
        }


        val selectedCountry = CurrentCountryManager.readCountry(requireContext())

        binding.tvSelectedCountry.text = selectedCountry?.let { "${it.flagEmoji} ${it.name}" }
            ?: run { getString(R.string.country_not_selected) }

        binding.lvSelectCountry.setOnClickListener {
            requireContext().launchCountryPickerDialog { country: CPCountry? ->
                val newSelectedCountry = country?:return@launchCountryPickerDialog
                Log.d("Country", "CountryCode = $newSelectedCountry")
                binding.tvSelectedCountry.text =  "${newSelectedCountry.flagEmoji} ${newSelectedCountry.name}"
                CurrentCountryManager.writeCountry(requireContext(),newSelectedCountry)
            }
        }

    }

}