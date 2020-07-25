package com.noto.app

import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainViewModel(private val storage: FlowSharedPreferences) : ViewModel() {

    val theme = liveData<Int> {
        val flow = storage.getInt(THEME_KEY, SYSTEM_THEME).asFlow()
        emitSource(flow.asLiveData())
    }

    fun setTheme(value: Int) = storage.sharedPreferences.edit { putInt(THEME_KEY, value) }

}