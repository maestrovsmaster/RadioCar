package com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by maestromaster$ on 25/02/2025$.
 */



@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val FIRST_LAUNCH_KEY = "first_launch"
    }

    private val _firstTimeLaunch = MutableStateFlow(savedStateHandle.get<Boolean>(FIRST_LAUNCH_KEY) ?: true)
    val firstTimeLaunch: StateFlow<Boolean> = _firstTimeLaunch.asStateFlow()

    init {
        viewModelScope.launch {
            delay(50) // Демонстрація, можна прибрати
            _firstTimeLaunch.value = false
            savedStateHandle[FIRST_LAUNCH_KEY] = false // Зберігаємо значення при зміні орієнтації
        }
    }
}

