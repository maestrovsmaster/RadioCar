package com.maestrovs.radiocar.ui.bottom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.util.Log
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.entities.Station
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.databinding.FragmentBottomBinding
import com.maestrovs.radiocar.ui.components.PlayPauseView
import com.maestrovs.radiocar.enums.PlayState
import com.maestrovs.radiocar.extensions.setVisible
import com.maestrovs.radiocar.ui.radio.StationEvent
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_radio.view.ivCover

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class BottomFragment : Fragment() {

    private var _binding: FragmentBottomBinding? = null

    private var lastStationEvent: StationEvent? = null

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

        mainViewModel.selectedStation.observe(viewLifecycleOwner) {

            updateUI(it)
        }

        binding.playPause.setOnClickListener {
            mainViewModel.switchCurrentStationState()
        }

        binding.btFavorite.setOnClickListener {
            mainViewModel.switchFavorite()
        }

    }

    private fun updateUI(selectedStation: StationEvent?) {
        var text = " "
        var playPause = PlayPauseView.STATE_PAUSE;

        var imgUrl: String? = null;

        var favoriteImgRes = R.drawable.ic_empty_24

        var contentVisible = false

        var shouldUpdatePlayButton = true

        selectedStation?.let { stationEvent ->

            playPause = when (stationEvent.playState) {
                PlayState.Play -> PlayPauseView.STATE_PLAY
                PlayState.Stop -> PlayPauseView.STATE_PAUSE
            }

            stationEvent.station?.let { station ->



                Log.d("SwitchFavorite", "++++++++++++++++++=station isFavorite = ${station.isFavorite}")

                text = station.name ?: " ??? "

                station.favicon?.let { icon ->
                    if (!icon.isNullOrEmpty()) {
                        imgUrl = station.favicon
                    }
                }

                contentVisible = true
                favoriteImgRes = R.drawable.ic_favorite_stroke_48
                station.isFavorite?.let {
                    if (it == 1) {
                        favoriteImgRes = R.drawable.ic_favorite
                    }
                }

                //When we doesn't should update Play/Stop button to prevent animation? - When was updated only IsFavorite status
                lastStationEvent?.let {
                    val statesIsSame = it.playState == stationEvent.playState
                    it.station?.let{lastStation ->
                        if(lastStation.stationuuid == station.stationuuid &&  lastStation.isFavorite != station.isFavorite && statesIsSame){
                            shouldUpdatePlayButton = false
                        }
                    }

                }

            }
            lastStationEvent = stationEvent

        }

        binding.tvStation.text = text
        if(shouldUpdatePlayButton) {
            binding.playPause.setState(playPause)
        }
        binding.playPause.setVisible(contentVisible)


        binding.btFavorite.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                favoriteImgRes
            )
        )


        binding.ivCover.setVisible(contentVisible)

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}