package com.maestrovs.radiocar.ui.radio

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.JustifyContent
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.databinding.FragmentRadioBinding
import com.maestrovs.radiocar.extensions.setVisible
import com.maestrovs.radiocar.ui.components.WrapFlexboxLayoutManager
import com.maestrovs.radiocar.utils.Resource
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
    private lateinit var layoutManager: WrapFlexboxLayoutManager

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


        layoutManager = WrapFlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.recycler.layoutManager = layoutManager
        binding.recycler.itemAnimator = null

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemViewCacheSize(20);
        binding.recycler.setDrawingCacheEnabled(true);
        binding.recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        binding.recycler.adapter = adapter

        mainViewModel.selectedStation.observe(viewLifecycleOwner) { stationEvent ->
            adapter.setStationEvent(stationEvent)

        }


        radioViewModel.stations.observe(viewLifecycleOwner) {
            it?.let {
                if (currentListType == ListType.All) {
                    processResources(it)
                }
            }

        }

        radioViewModel.recent.observe(viewLifecycleOwner) {
            it?.let {
                if (currentListType == ListType.Recent) {
                    processResources(it)
                }
            }
        }


        radioViewModel.favorites.observe(viewLifecycleOwner) {
            it?.let {
                if (currentListType == ListType.Favorites) {
                    processResources(it)
                }
            }

        }

        radioViewModel.searched.observe(viewLifecycleOwner) {
            Log.d("Searched", "Searched 1")
            it?.let {
                Log.d("Searched", "Searched 2")
                if (currentListType == ListType.Searched) {
                    processResources(it)
                }
            }

        }




        radioViewModel.fetchRecent()
        updateButtons(ListType.Recent)

        binding.btAll.setOnClickListener {
            currentListType = ListType.All
            layoutManager.justifyContent = JustifyContent.SPACE_AROUND
            updateButtons(ListType.All)
            radioViewModel.fetchStations()
        }

        binding.btRecent.setOnClickListener {
            currentListType = ListType.Recent
            layoutManager.justifyContent = JustifyContent.FLEX_START
            updateButtons(ListType.Recent)
            radioViewModel.fetchRecent()
        }

        binding.btFavorite.setOnClickListener {
            currentListType = ListType.Favorites
            layoutManager.justifyContent = JustifyContent.FLEX_START
            updateButtons(ListType.Favorites)
            radioViewModel.fetchFavorites()
        }

        binding.btSearch.setOnClickListener {
            currentListType = ListType.Searched
            layoutManager.justifyContent = JustifyContent.FLEX_START
            updateButtons(ListType.Searched)
            //radioViewModel.searchStations("нв")
        }

        /* binding.etSearch.setOnSearchClickListener {
            Log.d("Search","Search1")
            val text =binding.etSearch.query.toString()
            Log.d("Search","Search2 $text")
            if(text.isNotEmpty()) {
                radioViewModel.searchStations(text)
            }
        }*/

        binding.etSearch.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {


            //
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("Search","Search2 $query")
                query?:return true
                if(query.isNotEmpty()) {
                    radioViewModel.searchStations(query)
                }
                val inputManager = binding.etSearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }



    private fun updateButtons(listType: ListType) {

        var colorRecent = R.drawable.ripple_gray_round
        var colorFavorites = R.drawable.ripple_gray_round
        var colorAll = R.drawable.ripple_gray_round
        var colorSearched = R.drawable.ripple_gray_round

        when (listType) {
            ListType.Recent -> colorRecent = R.drawable.ripple_pink_round
            ListType.Favorites -> colorFavorites = R.drawable.ripple_pink_round
            ListType.All -> colorAll = R.drawable.ripple_pink_round
            ListType.Searched -> colorSearched = R.drawable.ripple_pink_round
        }

        binding.btRecent.setCardBackground(colorRecent)
        binding.btFavorite.setCardBackground(colorFavorites)
        binding.btAll.setCardBackground(colorAll)
        binding.btSearch.setCardBackground(colorSearched)

        binding.etSearch.setVisible(listType == ListType.Searched)

    }


    private fun processResources(response: Resource<List<Station>>) {

        when (response.status) {
            Resource.Status.SUCCESS -> {
                binding.progressBar.visibility = View.GONE

                response.data.let { list ->
                    if (list != null) {
                        adapter.submitList(list)
                        if (currentListType == ListType.Recent && firstStart) {
                            if (list.isNullOrEmpty()) {
                                currentListType = ListType.All
                                layoutManager.justifyContent = JustifyContent.FLEX_START
                                firstStart = false
                                radioViewModel.fetchStations()
                                updateButtons(ListType.All)
                            } else if (list.isNotEmpty()) {
                                mainViewModel.setIfNeedInitStation(list[0])
                            }
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

enum class ListType {
    Recent, Favorites, All, Searched
}
