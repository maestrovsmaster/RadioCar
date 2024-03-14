package com.maestrovs.radiocar.ui.radio

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.common.CurrentCountryManager
import com.maestrovs.radiocar.data.default_podcasts.bbc_uk_json
import com.maestrovs.radiocar.data.default_podcasts.cowboys_juke_json
import com.maestrovs.radiocar.data.default_podcasts.jaz_fm_json
import com.maestrovs.radiocar.data.default_podcasts.parseStation
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.databinding.FragmentRadioBinding
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.extensions.setVisible
import com.maestrovs.radiocar.ui.components.showDeleteStationDialog
import com.maestrovs.radiocar.ui.radio.utils.RadioErrorType
import com.maestrovs.radiocar.ui.radio.utils.errorMapper
import com.maestrovs.radiocar.ui.radio.utils.filterAll
import com.maestrovs.radiocar.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.util.Arrays
import android.content.res.Configuration
import com.maestrovs.radiocar.common.Constants.PAGE_SIZE
import com.maestrovs.radiocar.ui.components.PaginationScrollListener

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

@AndroidEntryPoint
class RadioFragment : Fragment() {

    var currentListType = ListType.Recent

    var firstStart = true


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
    //private lateinit var layoutManager: WrapFlexboxLayoutManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var currentPage = 0
    private var isLastPage = false

    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRadioBinding.inflate(inflater, container, false)



        binding.adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                Log.d("AdMob", "AdMob onAdFailedToLoad = $p0")
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                Log.d("AdMob", "AdMob onAdLoaded")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d("AdMob", "AdMob onAdClicked")
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.d("AdMob", "AdMob onAdClosed")
            }
        }

        // binding.adView.setAdSize(AdSize.BANNER)
        // binding.adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"//"ca-app-pub-3109852779138325/6128284569"


        val testDeviceIds = Arrays.asList("2571438569BBD089458441E499736CF4")
        val configuration =
            RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)


        val adRequest = AdRequest.Builder().build()
        // binding.adView.setAdSize(AdSize.BANNER) // розмір реклами
        // binding.adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111") // ID блоку реклами
        binding.adView.loadAd(adRequest) // завантаження реклами

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = StationAdapter(object : StationAdapter.ItemListener {

            override fun onClickedItem(station: Station?, mustUpdateList: Boolean) {
                station?.let {
                    mainViewModel.setStation(it, mustUpdateList)
                }
            }

            override fun onLongClickedItem(station: Station?) {
                station?.let {
                    if (currentListType == ListType.Recent) {
                        showDeleteStationDialog(requireContext(), station.name) {
                            mainViewModel.deleteRecent(it.stationuuid)
                        }
                        radioViewModel.fetchRecent()
                    }
                }
            }

        })

        val spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            3
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            4
        } else {
            3
        }

        val gridLayoutManager = GridLayoutManager(context, spanCount)

        binding.recycler.layoutManager = gridLayoutManager

        binding.recycler.adapter = adapter
        adapter.setRecyclerView(binding.recycler)

        binding.recycler.addOnScrollListener(object :
            PaginationScrollListener(gridLayoutManager) {
            override fun isLastPage(): Boolean {
                return this@RadioFragment.isLastPage
            }

            override fun loadMoreItems() {
                this@RadioFragment.isLoading = true
              //  mustReplaceTransactionsList = false

             //   mainViewModel.getTransactions(this@RadioFragment.currentPage)

               // Log.d("RadioFragment","LoadMoreItems page = $currentPage")
                if(currentListType == ListType.All){
                    fetchAllStations(false)
                    this@RadioFragment.currentPage += 1
                }

            }

            override fun isLoading(): Boolean {
                return this@RadioFragment.isLoading
            }
        })



        mainViewModel.selectedStation.observe(viewLifecycleOwner) { it ->
            val station = it ?: return@observe
            adapter.setStation(station)

        }

        mainViewModel.playAction.observe(viewLifecycleOwner) { playAction ->
            // if(playAction == PlayAction.Resume || playAction == PlayAction.Pause || playAction == PlayAction.Idle) {
            //    adapter.setPlayAction(playAction)
            // }

            when (playAction) {
                is PlayAction.Resume, PlayAction.Pause, PlayAction.Idle -> {
                    adapter.setPlayAction(playAction)
                }

                is PlayAction.Error -> {
                    adapter.setPlayAction(playAction)
                    //TODO write to corrupt stations
                }

                is PlayAction.Next -> {
                    Log.d("RadioFragment","Click ?Next .selectedStation = ${mainViewModel.selectedStation}");
                    mainViewModel.selectedStation.let {
                        adapter.nextStation(it.value)
                    }
                }

                is PlayAction.Previous -> {
                    Log.d("RadioFragment","Click ?Previous");
                    mainViewModel.selectedStation.let {
                        adapter.previousStation(it.value)
                    }
                }

                else -> {

                }
            }

        }


        radioViewModel.stations.observe(viewLifecycleOwner) {
            Log.d("ApiError", "Server *  ${it} ")
            it?.let {
                Log.d("ApiError", "Server *2  ${it} ")
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


        mainViewModel.mustRefreshStatus.observe(viewLifecycleOwner) {
            fetchAllStations(true)
        }




        radioViewModel.fetchRecent()
        updateButtons(ListType.Recent)

        binding.btAll.setOnClickListener {
            currentListType = ListType.All
            //   layoutManager.justifyContent = JustifyContent.SPACE_AROUND
            updateButtons(ListType.All)
            fetchAllStations(true)
            mainViewModel.setListType(ListType.All)
        }

        binding.btRecent.setOnClickListener {
            currentListType = ListType.Recent
            // layoutManager.justifyContent = JustifyContent.FLEX_START
            updateButtons(ListType.Recent)
            radioViewModel.fetchRecent()
            mainViewModel.setListType(ListType.Recent)
        }

        binding.btFavorite.setOnClickListener {
            currentListType = ListType.Favorites
            // layoutManager.justifyContent = JustifyContent.FLEX_START
            updateButtons(ListType.Favorites)
            radioViewModel.fetchFavorites()
            mainViewModel.setListType(ListType.Favorites)
        }

        binding.btSearch.setOnClickListener {
            mainViewModel.setListType(ListType.All)
            currentListType = ListType.Searched
            //   layoutManager.justifyContent = JustifyContent.FLEX_START
            updateButtons(ListType.Searched)

            radioViewModel.searched.value?.let { lastSearchResult ->
                processResources(lastSearchResult)
            } ?: kotlin.run { showList(listOf()) }

        }



        binding.etSearch.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {


            //
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("Search", "Search2 $query")
                query ?: return true
                if (query.isNotEmpty()) {
                    radioViewModel.searchStations(query)
                }
                val inputManager =
                    binding.etSearch.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun updateButtons(listType: ListType) {

        /* var colorRecent = R.drawable.ripple_gray_round
         var colorFavorites = R.drawable.ripple_gray_round
         var colorAll = R.drawable.ripple_gray_round
         var colorSearched = R.drawable.ripple_gray_round

         when (listType) {
             ListType.Recent -> colorRecent = R.drawable.ripple_pink_round
             ListType.Favorites -> colorFavorites = R.drawable.ripple_pink_round
             ListType.All -> colorAll = R.drawable.ripple_pink_round
             ListType.Searched -> colorSearched = R.drawable.ripple_pink_round
         }*/

        val inactiveColor = R.color.gray
        val activeColor = R.color.blue
        var colorRecent = inactiveColor
        var colorFavorites = inactiveColor
        var colorAll = inactiveColor
        var colorSearched = inactiveColor

        when (listType) {
            ListType.Recent -> colorRecent = activeColor
            ListType.Favorites -> colorFavorites = activeColor
            ListType.All -> colorAll = activeColor
            ListType.Searched -> colorSearched = activeColor
        }

        // binding.btRecent.setCardBackground(colorRecent)
        // binding.btFavorite.setCardBackground(colorFavorites)
        // binding.btAll.setCardBackground(colorAll)
        // binding.btSearch.setCardBackground(colorSearched)

        binding.btRecent.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                colorRecent
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
        binding.btFavorite.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                colorFavorites
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
        binding.btAll.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                colorAll
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
        binding.btSearch.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                colorSearched
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )


        binding.etSearch.setVisible(listType == ListType.Searched)

    }



    private fun processResources(response: Resource<List<Station>>) {
        this@RadioFragment.isLoading = false
        when (response.status) {
            Resource.Status.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.setVisible(false)
                showList(response.data)
                /*if(response.data.isNullOrEmpty()){
                    lastPageCount+=1
                    if(lastPageCount>=5) {
                        isLastPage = true
                    }
                }*/
            }

            Resource.Status.ERROR -> {
                binding.progressBar.visibility = View.GONE
                Log.d("ApiError", "Server error  ${response.message}  = ${response.data}")
                binding.tvError.setVisible(true)

                when (errorMapper(response.message ?: "")) {
                    RadioErrorType.ServiceNotAvailable -> {
                        binding.tvError.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.yourThumbTintColor
                            )
                        )
                        binding.tvError.text = getString(R.string.radio_service_not_available)
                    }

                    RadioErrorType.NoInternet -> {
                        binding.tvError.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        binding.tvError.text = getString(R.string.error_no_address)
                    }

                    RadioErrorType.Other -> {
                        binding.tvError.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                        binding.tvError.text = response.message ?: ""
                    }
                }

                if (adapter.itemCount == 0) {
                    val bbc_uk_json = parseStation(bbc_uk_json)
                    val jaz_fm_json = parseStation(jaz_fm_json)
                    val cowboys_juke_json = parseStation(cowboys_juke_json)
                    val list = listOf(bbc_uk_json, jaz_fm_json, cowboys_juke_json)

                    showList(list)
                    radioViewModel.insertStations(list)
                }
            }

            Resource.Status.LOADING -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvError.setVisible(false)
            }
        }
    }

    private var lastSubmitSize = 0

    private fun showList(list: List<Station>?) {
        if (list != null) {

            val filteredList = filterAll(list)

            Log.d("RadioFragment","submitList size = ${list.size} page = $currentPage  ")
            if(list.size == lastSubmitSize && list.isNotEmpty()){
                lastPageCount += 1
                if(lastPageCount>5) {
                    isLastPage = true
                }
            }else {
                lastSubmitSize = list.size
            }
            adapter.submitList(filteredList)
            if (currentListType == ListType.Recent && firstStart) {
                if (filteredList.isNullOrEmpty()) {
                    currentListType = ListType.All
                    //  layoutManager.justifyContent = JustifyContent.FLEX_START
                    firstStart = false
                    currentPage = 0
                    fetchAllStations(true)
                    updateButtons(ListType.All)
                } else if (filteredList.isNotEmpty()) {
                    mainViewModel.setIfNeedInitStation(filteredList[0])
                }
            }
        } else {
            adapter.submitList(listOf())
        }
    }

    private var lastPageCount = 0
    fun fetchAllStations(fromFirstPage: Boolean){

        if(fromFirstPage) {
            currentPage = 0
            lastPageCount = 0
            isLastPage = false
        }
       val query = StationQuery(CurrentCountryManager.readCountry(requireContext())?.alpha2
            ?: CurrentCountryManager.DEFAULT_COUNTRY,
           currentPage * PAGE_SIZE,
           PAGE_SIZE
            )
        Log.d("RadioFragment","fetchAllStations isLast = $isLastPage  ** = $query")
        radioViewModel.fetchStations(
            query
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}

enum class ListType {
    Recent, Favorites, All, Searched
}
