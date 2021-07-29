package com.noto.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.source.LocalStorage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val ThemeKey = "Theme"

class MainViewModel(private val storage: LocalStorage) : ViewModel() {

    private val mutableTheme = MutableStateFlow(Theme.System)
    val theme get() = mutableTheme.asStateFlow()

    init {
        storage.get(ThemeKey)
            .map { it.map { Theme.valueOf(it) } }
            .getOrElse { flowOf(Theme.System) }
            .onEach { mutableTheme.value = it }
            .launchIn(viewModelScope)
    }

    fun setTheme(value: Theme) = viewModelScope.launch {
        storage.put(ThemeKey, value.toString())
    }

}

enum class Theme {
    System, Light, Dark,
}