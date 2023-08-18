package com.maestrovs.radiocar.ui.bottom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.databinding.FragmentBottomBinding
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.ui.components.PlayPauseView
import com.maestrovs.radiocar.extensions.setVisible
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class BottomFragment : Fragment() {

    private var _binding: FragmentBottomBinding? = null

    // private var lastStationEvent: StationEvent? = null

    private val delayLoadAnimation = 5L

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

        mainViewModel.selectedStation.observe(viewLifecycleOwner) { station ->
            Log.d("Orientation", "")
            station?.let { updateStation(station) } ?: run {
                showPlaceHolder()
            }
        }

        mainViewModel.playAction.observe(viewLifecycleOwner) { playAction ->
            Log.d("Orientation", "playAction = $playAction")
            updatePlayAction(playAction)
        }


        binding.playPause.setOnClickListener {
            binding.playPause.switchState()
            mainViewModel.switchCurrentStationState()
        }

        binding.tvStation.setOnClickListener {
            binding.playPause.switchState()
            mainViewModel.switchCurrentStationState()
        }

        binding.btFavorite.setOnClickListener {
            mainViewModel.switchFavorite()
        }


    }

    private fun showPlaceHolder() {
        binding.lnWelcome.setVisible(true)
    }


    private fun updateStation(station: Station) {
        binding.lnWelcome.setVisible(false)
        binding.tvStation.text = station.name
        var favoriteImgRes = R.drawable.ic_favorite_stroke_48
        station.isFavorite?.let {
            if (it == 1) {
                favoriteImgRes = R.drawable.ic_favorite
            }
        }
        binding.btFavorite.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                favoriteImgRes
            )
        )
        var imgUrl: String? = null
        station.favicon?.let { icon ->
            if (!icon.isNullOrEmpty()) {
                imgUrl = station.favicon
            }
        }
        if (imgUrl != null) {

            Picasso.get()
                .load(imgUrl)
                .resize(120, 120)
                .centerCrop()
                .into(binding.ivCover)
        } else {
            Picasso.get()
                .load(R.drawable.bg_music)
                .resize(120, 120)
                .centerCrop()
                .into(binding.ivCover)
        }
    }

    private fun updatePlayAction(playAction: PlayAction) {

        when (playAction) {
            is PlayAction.Resume -> {
                binding.playPause.setState(PlayPauseView.STATE_PLAY)
                showPlayAnim(true)
                showLoad(false)
                hideError()
            }

            is PlayAction.Pause -> {
                binding.playPause.setState(PlayPauseView.STATE_PAUSE)
                showPlayAnim(false)
                showLoad(false)
                hideError()
            }

            is PlayAction.Idle -> {
                binding.playPause.setState(PlayPauseView.STATE_PAUSE)
                showPlayAnim(false)
                showLoad(false)

            }

            is PlayAction.Error -> {
                binding.playPause.setState(PlayPauseView.STATE_PAUSE)
                showPlayAnim(false)
                showLoad(false)
                showError(processError(requireContext(),playAction))
            }

            is PlayAction.Buffering -> {
                showPlayAnim(false)
                showLoad(true)
                hideError()
            }

            else -> {}
        }

    }


    private fun showPlayAnim(drawPlayingAnim: Boolean) {
        if (!drawPlayingAnim) {
            binding.animationView.setVisible(drawPlayingAnim)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.animationView.setVisible(true)
            }, delayLoadAnimation)
        }
    }

    private fun showLoad(isPlaying: Boolean) {
        if (!isPlaying) {
            binding.loadProgress.setVisible(false)
        } else {
            binding.loadProgress.setVisible(true)
            //Handler(Looper.getMainLooper()).postDelayed({
            //    binding.loadProgress.setVisible(false)
           // }, delayLoadAnimation)
        }
    }


    private fun showError(text: String) {
        binding.tvError.text = text
        binding.tvError.setVisible(true)
        binding.btFavorite.setVisible(false)
        binding.ivError.setVisible(true)
    }

    private fun hideError() {
        binding.tvError.setVisible(false)
        binding.btFavorite.setVisible(true)
        binding.ivError.setVisible(false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



