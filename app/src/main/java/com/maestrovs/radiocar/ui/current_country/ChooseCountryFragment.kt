package com.maestrovs.radiocar.ui.current_country

import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hbb20.countrypicker.datagenerator.CPDataStoreGenerator
import com.maestrovs.radiocar.databinding.FragmentCountryBinding
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.hbb20.countrypicker.models.CPCountry
import com.hbb20.countrypicker.recyclerview.loadCountries
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.common.CurrentCountryManager
import com.maestrovs.radiocar.extensions.setVisible
import com.murgupluoglu.flagkit.FlagKit

// https://github.com/hbb20/AndroidCountryPicker/wiki/Country-List-(RecyclerView)
class ChooseCountryFragment : Fragment() {

    private var _binding: FragmentCountryBinding? = null

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java].apply {

        }
    }

    private val countryViewModel by lazy {
        ViewModelProvider(this)[CurrentCountryViewModel::class.java].apply {

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


        _binding = FragmentCountryBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    var canBack = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        canBack = ChooseCountryFragmentArgs.fromBundle(requireArguments()).canBack

        binding.btBack.setVisible(canBack)
        binding.logo.setVisible(!canBack)
        binding.btNext.setVisible(true)

        binding.btNext.text = when(canBack){
            true -> getString(R.string.done)
            false -> getString(R.string.next)
        }


        binding.btNext.setOnClickListener {
            if(canBack){
                findNavController().popBackStack()
            }else {
                findNavController().navigate(R.id.action_chooseCountryFragment_to_mainFragment)
            }
        }

        binding.btBack.setOnClickListener {
            findNavController().popBackStack()

        }

        loadAllCountriesList()

        countryViewModel.detectedCountry.observe(viewLifecycleOwner) {

            it?.let { txt ->
                findCountry(txt)?.let { country ->
                    setCurrentCountry(country)
                   // binding.btNext.setVisible(true)
                }
            }

            //
        }

        countryViewModel.detectCountryFromIp()


    }


    private fun setCurrentCountry(country: CPCountry) {
        _binding?.tvCountry?.text = country.name
        val flagId = FlagKit.getResId(requireContext(), country.alpha2)
        binding.ivFlag.setImageResource(flagId)
        CurrentCountryManager.writeCountry(requireContext(), country)
    }

    private fun loadAllCountriesList() {
        binding.recyclerView.loadCountries(
            filterQueryEditText = binding.cpCountry,
            preferredCountryCodes = "DEU,IT,GBR,USA,UKR,PL,FRA",
            customExcludedCountries = "RUS"
        )
        { selectedCountry: CPCountry ->
            Log.d("CurrentCountry", "CurrentCountry selectedCountry= $selectedCountry")
            setCurrentCountry(selectedCountry)
          //  binding.btNext.setVisible(!canBack)
        }
    }


    fun findCountry(countryName: String): CPCountry? {
        val dataStore = CPDataStoreGenerator.generate(requireContext())
        return dataStore.countryList.firstOrNull { it.name == countryName }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}