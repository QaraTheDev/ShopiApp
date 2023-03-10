package com.example.shopiapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.domain.entities.GoodDetails
import com.example.shopiapp.model.GoodDetailsColor
import com.example.shopiapp.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class GoodDetailsViewModel @Inject constructor() : ViewModel() {

    private val _stateFlow = MutableStateFlow(State.COMPLETE)
    val stateFlow get() = _stateFlow.asStateFlow()

    private val _errorChannel = Channel<Exception>()
    val errorFlow get() = _errorChannel.receiveAsFlow()

    private val _totalChannel = Channel<Double>()
    val totalFlow get() = _totalChannel.receiveAsFlow()

    private val _colorsChannel = Channel<List<GoodDetailsColor>>()
    val colorsFlow = _colorsChannel.receiveAsFlow()

    private var _goodDetails: GoodDetails? = null
    val goodDetails get() = _goodDetails

    private var _colors = mutableListOf<GoodDetailsColor>()
    val colors get() = _colors.toList()

    private var _total = 0.0
    val totalSum get() = _total

    fun setGoodDetails(goodDetails: GoodDetails) {
        _goodDetails = goodDetails
        if (_colors.isEmpty())
            for (color in goodDetails.colors) {
                _colors.add(GoodDetailsColor(color, false))
            }
    }

    fun addGood() {
        _total += goodDetails?.price ?: 0.0
        updateTotal()
    }

    fun removeGood() {
        _total -= goodDetails?.price ?: 0.0
        updateTotal()
    }

    fun selectColor(colorPosition: Int) {
        val colorsChanged = mutableListOf<GoodDetailsColor>()
        for (i in 0 until _colors.size) {
            val isSelected = i == colorPosition
            colorsChanged.add(GoodDetailsColor(_colors[i].colorHex, isSelected))
        }
        viewModelScope.launch {
            _colorsChannel.send(colorsChanged)
        }
    }

    private fun updateTotal() {
        viewModelScope.launch {
            _totalChannel.send(totalSum)
        }
    }
}