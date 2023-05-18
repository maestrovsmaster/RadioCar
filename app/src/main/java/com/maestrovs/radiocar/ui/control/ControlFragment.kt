package com.maestrovs.radiocar.ui.control

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.entities.weather.WeatherResponse
import com.maestrovs.radiocar.databinding.FragmentControlBinding
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.ui.radio.ListType
import com.maestrovs.radiocar.ui.radio.RadioViewModel
import com.maestrovs.radiocar.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentControlBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controlViewModel.weatherResponse.observe(viewLifecycleOwner){
            binding.weatherWidget!!.setWeatherResponse(it)
        }


       controlViewModel.fetchWeather()


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}