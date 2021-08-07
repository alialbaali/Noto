package com.noto.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Theme
import com.noto.app.domain.source.LocalStorage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val ThemeKey = "Theme"

class MainViewModel(private val storage: LocalStorage) : ViewModel() {

    val theme = storage.get(ThemeKey)
        .map { Theme.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Theme.System)

    fun updateTheme(value: Theme) = viewModelScope.launch {
        storage.put(ThemeKey, value.toString())
    }
}
