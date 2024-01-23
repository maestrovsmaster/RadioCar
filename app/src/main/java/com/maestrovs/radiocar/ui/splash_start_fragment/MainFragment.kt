package com.maestrovs.radiocar.ui.splash_start_fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.databinding.ContentMainBinding
import com.maestrovs.radiocar.databinding.FragmentSplashBinding
import com.maestrovs.radiocar.enums.bluetooth.BT_Status
import com.maestrovs.radiocar.ui.main.MainViewModel
import com.transitionseverywhere.extra.Scale

class MainFragment : Fragment() {

    private var _binding: ContentMainBinding? = null

    private val mainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java].apply {

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


        _binding = ContentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root


        mainViewModel.mustNavToSettings.observe(viewLifecycleOwner){
            if(it == true) {
                findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
            }
        }

        mainViewModel.bluetoothStatus.observe(viewLifecycleOwner) { bt_Status ->
            bt_Status ?: return@observe

            val icon = when (bt_Status) {
                BT_Status.Enabled -> R.drawable.ic_bt_on
                BT_Status.ConnectedDevice -> R.drawable.ic_bluetooth_connected
                BT_Status.Disable -> R.drawable.ic_bt_off
                BT_Status.DisconnectedDevice -> R.drawable.ic_bt_on
            }
            binding.btBluetooth.setImageDrawable(ContextCompat.getDrawable(requireContext(), icon))
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val set: TransitionSet = TransitionSet()
            .addTransition(Fade())
            .addTransition(Scale(0.7f))
            .setInterpolator(LinearOutSlowInInterpolator())

        binding.btNavigation.setOnClickListener {
            launchMapIntent()
        }

        binding.btPhone.setOnClickListener {
            launchPhoneIntent()
        }

        binding.btBluetooth.setOnClickListener {
            launchBluetoothIntent()
        }

        binding.btSettings.setOnClickListener {
            launchSettingsIntent()
            mainViewModel.setMustNavToSettings()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun launchMapIntent() {
        var gmmIntentUri = Uri.parse("geo:0,0")
        //     Uri.parse("geo:0,0?q=?")

        mainViewModel.location.value?.let {
            gmmIntentUri = Uri.parse("geo:${it.latitude},${it.longitude}")
        }
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun launchPhoneIntent() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "", null))
        startActivity(intent)
    }


    private fun launchBluetoothIntent() {
        val intentOpenBluetoothSettings = Intent()
        intentOpenBluetoothSettings.action = Settings.ACTION_BLUETOOTH_SETTINGS
        startActivity(intentOpenBluetoothSettings)
    }


    private fun launchSettingsIntent() {
        // val intent = SettingsActivity.newIntent(requireContext()).apply {
        //    putExtra(KEY_SETTINGS_INPUT_MESSAGE, "")
        // }
        // launcher.launch(intent)
        // _binding?.?.findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
    }

}