package com.maestrovs.radiocar.ui.bottom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.databinding.FragmentBottomBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class BottomFragment : Fragment() {

    private var _binding: FragmentBottomBinding? = null

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java].apply {

        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBottomBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mainViewModel.selectedStation.observe(viewLifecycleOwner){station ->
            station?.let {
                binding.tvStation.text = station.name
            }?: kotlin.run {
                binding.tvStation.text = "------"
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}