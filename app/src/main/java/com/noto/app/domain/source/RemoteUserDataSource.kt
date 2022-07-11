package com.noto.app.domain.source

import com.noto.app.data.model.remote.RemoteUser

interface RemoteUserDataSource {

    suspend fun getUser(): RemoteUser

    suspend fun createUser(id: String, name: String)

    suspend fun updateName(id: String, name: String)

}