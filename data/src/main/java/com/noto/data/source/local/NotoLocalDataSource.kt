package com.noto.data.source.local

import com.noto.domain.model.Noto
import kotlinx.coroutines.flow.Flow

interface NotoLocalDataSource {

    fun getNotos(libraryId: Long): Flow<List<Noto>>

    fun getNotoById(notoId: Long): Flow<Noto>

    suspend fun createNoto(noto: Noto)

    suspend fun updateNoto(noto: Noto)

    suspend fun deleteNoto(noto: Noto)

    suspend fun countLibraryNotos(libraryId: Long): Int

    suspend fun countNotos(): Int

}