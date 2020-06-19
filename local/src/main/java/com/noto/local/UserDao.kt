package com.noto.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.noto.data.source.local.UserLocalDataSource
import com.noto.domain.model.User

const val TOKEN = "token"
const val EMAIL = "email"
const val DISPLAY_NAME = "display_name"

class UserDao(private val sharedPreferences: SharedPreferences) : UserLocalDataSource {

    override fun createUser(user: User, userToken: String) {
        sharedPreferences.edit {
            putString(TOKEN, userToken)
            putString(DISPLAY_NAME, user.userDisplayName)
            putString(EMAIL, user.userEmail)
            apply()
        }
    }

    override fun getUserToken(): String {
        return sharedPreferences.getString(TOKEN, null) ?: String()
    }

    override fun getUser(): User {

        val userDisplayName = sharedPreferences.getString(DISPLAY_NAME, null) ?: String()
        val userEmail = sharedPreferences.getString(EMAIL, null) ?: String()

        return User(userDisplayName, userEmail)

    }

}