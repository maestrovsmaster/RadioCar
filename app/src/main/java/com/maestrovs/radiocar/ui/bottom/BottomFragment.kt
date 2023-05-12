package com.maestrovs.radiocar.ui.bottom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.util.Log
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.databinding.FragmentBottomBinding
import com.maestrovs.radiocar.ui.components.PlayPauseView
import com.maestrovs.radiocar.enums.PlayState
import com.maestrovs.radiocar.ui.radio.StationEvent

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

        mainViewModel.selectedStation.observe(viewLifecycleOwner){

            updateUI(it)
        }

       binding.playPause.setOnClickListener {
           mainViewModel.switchCurrentStationState()
       }

    }

    private fun updateUI(selectedStation: StationEvent?){
        var text = " "
        var playPause = PlayPauseView.STATE_PAUSE;

        selectedStation?.let { stationEvent ->

            playPause = when(stationEvent.playState){
                PlayState.Play -> PlayPauseView.STATE_PLAY
                PlayState.Stop -> PlayPauseView.STATE_PAUSE
            }

            if(stationEvent.station != null){
                text = stationEvent.station.name ?:" ??? "
            }

        }

        binding.tvStation.text = text
        binding.playPause.setState(playPause)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}