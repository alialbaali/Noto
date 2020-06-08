package com.alialbaali.noto.data.source.local

import com.noto.domain.model.User

interface UserLocalDataSource {

    suspend fun createUser(user: User, token: String)

}