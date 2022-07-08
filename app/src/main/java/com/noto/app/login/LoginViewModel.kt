package com.noto.app.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.components.TextFieldStatus
import com.noto.app.domain.model.UserStatus
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.domain.repository.UserRepository
import com.noto.app.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val mutableState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val state get() = mutableState.asStateFlow()

    private val mutableName = MutableStateFlow("")
    val name get() = mutableName.asStateFlow()

    private val mutableEmail = MutableStateFlow("")
    val email get() = mutableEmail.asStateFlow()

    private val mutablePassword = MutableStateFlow("")
    val password get() = mutablePassword.asStateFlow()

    private val mutableConfirmPassword = MutableStateFlow("")
    val confirmPassword get() = mutableConfirmPassword.asStateFlow()

    private val mutableNameStatus = MutableStateFlow<TextFieldStatus>(TextFieldStatus.Empty)
    val nameStatus get() = mutableNameStatus.asStateFlow()

    private val mutableEmailStatus = MutableStateFlow<TextFieldStatus>(TextFieldStatus.Empty)
    val emailStatus get() = mutableEmailStatus.asStateFlow()

    private val mutablePasswordStatus = MutableStateFlow<TextFieldStatus>(TextFieldStatus.Empty)
    val passwordStatus get() = mutablePasswordStatus.asStateFlow()

    private val mutableConfirmPasswordStatus = MutableStateFlow<TextFieldStatus>(TextFieldStatus.Empty)
    val confirmPasswordStatus get() = mutableConfirmPasswordStatus.asStateFlow()

    fun registerUser(name: String, email: String, password: String) = viewModelScope.launch {
        mutableState.value = UiState.Loading
        mutableState.value = userRepository.registerUser(name.trim(), email.trim(), password.trim())
            .onSuccess { settingsRepository.updateUserStatus(UserStatus.LoggedIn) }
            .toUiState()
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        mutableState.value = UiState.Loading
        mutableState.value = userRepository.loginUser(email.trim(), password.trim())
            .onSuccess { settingsRepository.updateUserStatus(UserStatus.LoggedIn) }
            .toUiState()
    }

    fun skipLogin() = viewModelScope.launch {
        settingsRepository.updateUserStatus(UserStatus.None)
    }

    fun setName(name: String) {
        mutableName.value = name
    }

    fun setEmail(email: String) {
        mutableEmail.value = email
    }

    fun setPassword(password: String) {
        mutablePassword.value = password
    }

    fun setConfirmPassword(confirmPassword: String) {
        mutableConfirmPassword.value = confirmPassword
    }

    fun setNameStatus(status: TextFieldStatus) {
        mutableNameStatus.value = status
    }

    fun setEmailStatus(status: TextFieldStatus) {
        mutableEmailStatus.value = status
    }

    fun setPasswordStatus(status: TextFieldStatus) {
        mutablePasswordStatus.value = status
    }

    fun setConfirmPasswordStatus(status: TextFieldStatus) {
        mutableConfirmPasswordStatus.value = status
    }
}