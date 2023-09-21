package com.maestrovs.radiocar.ui.settings.logos_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.common.CarLogoManager
import com.maestrovs.radiocar.databinding.FragmentLogosBinding
import com.maestrovs.radiocar.extensions.setVisible
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.maestrovs.radiocar.ui.settings.SettingsViewModel


class LogosFragment : Fragment() {


    private lateinit var adapter: ImagesGridAdapter
    private val images = listOf(
        R.drawable.ic_empty,
        R.drawable.logo_renault1,
        R.drawable.logo_renault2,
        R.drawable.logo_dacia1,
        R.drawable.logo_dacia2,
        R.drawable.logo_dacia3,
        R.drawable.logo_daewoo1,
        R.drawable.logo_daewoo2,
        R.drawable.logo_opel1,
    )

    private var _binding: FragmentLogosBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java].apply {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogosBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btBack.setOnClickListener {

            findNavController().popBackStack()
        }


        adapter = ImagesGridAdapter(images){ resId ->
            if(resId != null) {
                binding.ivCarLogo.setImageResource(resId)
                CarLogoManager.saveCarResId(requireContext(), resId)
            }else{
                binding.ivCarLogo.setImageResource(R.drawable.ic_empty)
                CarLogoManager.removeCarResId(requireContext())
            }
            mainViewModel.setMustRefreshStatus()
            findNavController().popBackStack()
        }
        binding.recyclerView.layoutManager = GridLayoutManager(context, 3)
        binding.recyclerView.adapter = adapter

    }



}