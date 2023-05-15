package com.maestrovs.radiocar.ui.radio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.data.entities.Station
import com.maestrovs.radiocar.databinding.FragmentRadioBinding
import com.maestrovs.radiocar.utils.Resource
import com.maestrovs.radiocar.utils.combineWith
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

@AndroidEntryPoint
class RadioFragment : Fragment() {

    var currentListType = ListType.Recent

    var firstStart = true;


    private var _binding: FragmentRadioBinding? = null

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java].apply {

        }
    }

    private val radioViewModel by lazy {
        ViewModelProvider(this)[RadioViewModel::class.java].apply {

        }
    }



    private lateinit var adapter: StationAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRadioBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StationAdapter(object : StationAdapter.ItemListener {
            override fun onClickedCharacter(item: Station?) {
                Log.d("Station", "~~~~~~~~~~ Station Url \uD83C\uDFB5  ${item?.url}")
                mainViewModel.switchStationState(item)
                // if(item != null) {
                // adapter.setSelectedStation(item)
                // }
            }

        })
        // binding.recycler.layoutManager = LinearLayoutManager(requireContext())


        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.recycler.layoutManager = layoutManager

        binding.recycler.adapter = adapter

        mainViewModel.selectedStation.observe(viewLifecycleOwner) { stationEvent ->
            adapter.setStationEvent(stationEvent)

        }


        Log.d("RequestResult", ">>onViewCreated")
        radioViewModel.stations.observe(viewLifecycleOwner){
            Log.d("RequestResult", ">>success $it")
            it?.let {
                Log.d("RequestResult", ">>success1")
                if(currentListType == ListType.All) {
                    processResources(it)
                }
            }

        }

        radioViewModel.recent.observe(viewLifecycleOwner){
            Log.d("RequestResult", ">>success recent $it")
            it?.let {
                Log.d("RequestResult", ">>success1 recent")
                if(currentListType == ListType.Recent) {
                    processResources(it)
                }
            }

        }

        radioViewModel.fetchRecent()
        updateButtons(ListType.Recent)

        binding.btAll.setOnClickListener {
            currentListType = ListType.All
            updateButtons(ListType.All)
            radioViewModel.fetchStations()
        }

        binding.btRecent.setOnClickListener {
            currentListType = ListType.Recent
            updateButtons(ListType.Recent)
            radioViewModel.fetchRecent()
        }

        binding.btFavorite.setOnClickListener {
            currentListType = ListType.Favorites
            updateButtons(ListType.Favorites)
          //  radioViewModel.fetchRecent()
        }
            //

    }



    fun updateButtons(listType:ListType){
        val activeColor = ContextCompat.getColor(requireContext(), R.color.pink)
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.white_80)

        var colorRecent = inactiveColor
        var colorFavorites = inactiveColor
        var colorAll = inactiveColor

        when(listType){
            ListType.Recent -> colorRecent = activeColor
            ListType.Favorites -> colorFavorites = activeColor
            ListType.All -> colorAll = activeColor
        }
        binding.btRecent.setColorFilter(colorRecent, android.graphics.PorterDuff.Mode.SRC_IN)
        binding.btFavorite.setColorFilter(colorFavorites, android.graphics.PorterDuff.Mode.SRC_IN)
        binding.btAll.setColorFilter(colorAll, android.graphics.PorterDuff.Mode.SRC_IN)

    }



    private fun processResources(response: Resource<List<Station>>) {

            when (response.status) {
                Resource.Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE

                    response.data.let { list ->
                        if (list != null) {
                            adapter.submitList(list)
                            if(currentListType == ListType.Recent && list.isNullOrEmpty() && firstStart){
                                currentListType = ListType.All
                                firstStart = false
                                radioViewModel.fetchStations()
                                updateButtons(ListType.All)
                            }
                        }

                    }
                }

                Resource.Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }

                Resource.Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class ListType{
    Recent, Favorites, All
}
