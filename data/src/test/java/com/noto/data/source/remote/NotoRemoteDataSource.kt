package com.noto.data.source.remote

import com.noto.domain.model.Noto
import com.noto.domain.schema.ResponseSchema

interface NotoRemoteDataSource {

    val baseUrl: String
        get() = "/notos"

    suspend fun getNotos(userToken: String): ResponseSchema<List<Noto>>

    suspend fun createNoto(userToken: String, noto: Noto): ResponseSchema<Noto>

    suspend fun updateNoto(userToken: String, noto: Noto): ResponseSchema<Noto>

    suspend fun deleteNoto(userToken: String, libraryId: Long, notoId: Long): ResponseSchema<Nothing>

}