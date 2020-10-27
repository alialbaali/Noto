package com.noto.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.noto.domain.local.LocalStorage
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val ThemeKey = "Theme"

class MainViewModel(private val storage: LocalStorage) : ViewModel() {

    val viewState = liveData {
        storage.get(ThemeKey)
            .mapCatching { flow -> flow.map { Theme.valueOf(it) } }
            .getOrDefault(flowOf(Theme.SystemTheme))
            .map { MainState(it) }
            .asLiveData()
            .let { emitSource(it) }
    }

    fun setTheme(value: Theme) = viewModelScope.launch {
        storage.put(ThemeKey, value.toString())
    }

}

data class MainState(
    val theme: Theme = Theme.SystemTheme
)

enum class Theme {
    SystemTheme, LightTheme, DarkTheme
}