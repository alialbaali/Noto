package com.noto.app.domain.source

interface RemoteUserDataSource {

    suspend fun createUser(id: String, name: String, email: String)

}