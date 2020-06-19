package com.noto.domain.repository

import com.noto.domain.model.Noto
import kotlinx.coroutines.flow.Flow

interface NotoRepository {

    val userToken: String

    suspend fun createNoto(noto: Noto)

    suspend fun deleteNoto(noto: Noto)

    suspend fun updateNoto(noto: Noto)

    suspend fun getNotos(libraryId: Long) : Result<Flow<List<Noto>>>

    suspend fun getNoto(notoId: Long): Result<Flow<Noto>>

    suspend fun countLibraryNotos(libraryId: Long): Int

}