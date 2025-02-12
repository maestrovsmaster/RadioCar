package com.maestrovs.radiocar.ui.splash_start_fragment

import android.R.attr.visible
import android.R.id.text2
import android.os.Bundle
import android.os.Handler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.common.CurrentCountryManager
import com.maestrovs.radiocar.common.SharedManager
import com.maestrovs.radiocar.databinding.FragmentSplashBinding
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.ui.settings.SettingsFragmentDirections
import com.transitionseverywhere.extra.Scale


class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java].apply {

        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val currentCountry = CurrentCountryManager.readCountry(requireContext())

        /*if (currentCountry != null) { //UNCOMMENT IT
            findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
        } else {
            findNavController().navigate(
                SplashFragmentDirections.actionSplashFragmentToChooseCountryFragment(false)
            )
        }*/

        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToRadioFragment())

        // findNavController().navigate(R.id.action_splashFragment_to_carLogoFragment)
        //

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}