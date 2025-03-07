package com.maestrovs.radiocar.ui.splash_start_fragment.magnitola_style

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.databinding.ContentMagnitolaBinding
import com.maestrovs.radiocar.databinding.ContentMainBinding
import com.maestrovs.radiocar.databinding.FragmentSplashBinding
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.transitionseverywhere.extra.Scale

class MagnitolaFragment : Fragment() {

    private var _binding: ContentMagnitolaBinding? = null

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


        _binding = ContentMagnitolaBinding.inflate(inflater, container, false)
        val root: View = binding.root


        mainViewModel.mustNavToSettings.observe(viewLifecycleOwner){
            if(it == true) {
                findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
            }
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val set: TransitionSet = TransitionSet()
            .addTransition(Fade())
            .addTransition(Scale(0.7f))
            .setInterpolator(LinearOutSlowInInterpolator())


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}