package com.noto.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Theme
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(private val storage: LocalStorage) : ViewModel() {

    private val mutableState = MutableStateFlow(State())
    val state get() = mutableState.asStateFlow()

    init {
        combine(
            storage.get(Constants.ThemeKey)
                .map { Theme.valueOf(it) },
            storage.get(Constants.FontKey)
                .map { Font.valueOf(it) },
        ) { theme, font -> mutableState.value = State(theme, font) }
            .launchIn(viewModelScope)
    }

    fun updateTheme(value: Theme) = viewModelScope.launch {
        storage.put(Constants.ThemeKey, value.toString())
    }

    fun updateFont(value: Font) = viewModelScope.launch {
        storage.put(Constants.FontKey, value.toString())
    }

    data class State(
        val theme: Theme = Theme.System,
        val font: Font = Font.Nunito,
    )
}