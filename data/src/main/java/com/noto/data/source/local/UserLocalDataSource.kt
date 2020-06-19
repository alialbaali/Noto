package com.noto.data.source.local

import com.noto.domain.model.User

interface UserLocalDataSource {

    fun createUser(user: User, userToken: String)

    fun getUserToken(): String

    fun getUser(): User

}