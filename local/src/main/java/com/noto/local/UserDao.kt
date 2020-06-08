package com.noto.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.alialbaali.noto.data.source.local.UserLocalDataSource
import com.noto.domain.model.User

const val TOKEN = "token"
const val EMAIL = "email"
const val DISPLAY_NAME = "display_name"

class UserDao(private val sharedPreferences: SharedPreferences) : UserLocalDataSource {

    override suspend fun createUser(user: User, token: String) {
        sharedPreferences.edit {
            putString(TOKEN, token)
            putString(DISPLAY_NAME, user.userDisplayName)
            putString(EMAIL, user.userEmail)
        }
    }

}