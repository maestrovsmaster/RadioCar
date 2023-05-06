package com.maestrovs.radiocar.ui.radio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.maestrovs.radiocar.MainViewModel
import com.maestrovs.radiocar.databinding.FragmentRadioBinding
import com.maestrovs.radiocar.utils.Resource


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

class RadioFragment : Fragment() {

    private var _binding: FragmentRadioBinding? = null

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java].apply {
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
        adapter = StationAdapter()
        // binding.recycler.layoutManager = LinearLayoutManager(requireContext())


        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.recycler.layoutManager = layoutManager

        binding.recycler.adapter = adapter




        mainViewModel.getData().observe(viewLifecycleOwner, Observer {

            it.status?.let { status ->
                when (status) {
                    Resource.Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d("RequestResult", ">>success")

                        it.data.let { list ->

                            if (list != null) {
                                adapter.submitList(list)
                            }

                        }
                    }

                    Resource.Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d("RequestResult", ">>error  ${it.message}")
                    }

                    Resource.Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }

        })



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}