package com.maestrovs.radiocar.ui.splash_start_fragment

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.maestrovs.radiocar.shared_managers.CurrentCountryManager
import com.maestrovs.radiocar.databinding.FragmentSplashBinding
import com.maestrovs.radiocar.ui.main.MainViewModel


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


//        val currentCountry = CurrentCountryManager.readCountry(requireContext())

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