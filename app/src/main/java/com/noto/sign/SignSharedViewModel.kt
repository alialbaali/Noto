package com.noto.sign

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.domain.interactor.user.UserUseCases
import com.noto.domain.model.User
import com.noto.util.asLiveData
import kotlinx.coroutines.launch

/**

This regex will enforce these rules:

At least one upper case English letter, (?=.*?[A-Z])
At least one lower case English letter, (?=.*?[a-z])
At least one digit, (?=.*?[0-9])
At least one special character, (?=.*?[#?!@$%^&*-])
Minimum eight in length .{8,} (with the anchors)

 */

private const val PASSWORD_ERROR = "Password must be at least 8 characters and must contain at least 1 upper case letter, 1 lower case letter, 1 number and 1 special character"

private const val EMAIL_ERROR = "Please provide a valid email address"

private val PASSWORD_REGEX = """^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@${'$'}%^&*-]).{8,}${'$'}""".toRegex()

private val EMAIL_REGEX =
    """(?:[a-z0-9!#${'$'}%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#${'$'}%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])""".toRegex()

class SignSharedViewModel(private val userUseCases: UserUseCases) : ViewModel() {

    val user = MutableLiveData<User>()

    private val _error = MutableLiveData<String>()
    val error = _error.asLiveData()

    init {
        user.value = User()
    }

    fun createUser() {
        viewModelScope.launch {

            val user = user.value

            when {

                user?.userDisplayName?.isEmpty() == false ->
                    _error.postValue("Name is required")

                user?.userEmail?.matches(EMAIL_REGEX) == false ->
                    _error.postValue(EMAIL_ERROR)

                user?.userPassword?.matches(PASSWORD_REGEX) == false ->
                    _error.postValue(PASSWORD_ERROR)

                else -> {

                    userUseCases.createUser(user!!).exceptionOrNull()?.let {
                        _error.postValue(it.message)
                    }

                }
            }
        }
    }

    fun loginUser() {
        viewModelScope.launch {

            val user = user.value

            when {

                user?.userEmail?.isEmpty() == true ->
                    _error.postValue("Email is required")

                user?.userPassword?.isEmpty() == true ->
                    _error.postValue("Password is required")

                else -> {

                    userUseCases.loginUser(user!!).exceptionOrNull()?.let {
                        _error.postValue(it.message)
                    }

                }
            }

        }
    }

}