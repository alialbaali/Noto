package com.alialbaali.noto.data.source.local

import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import kotlinx.coroutines.flow.Flow

interface NotoLocalDataSource {

    suspend fun createNoto(noto: Noto, notoLabels: List<NotoLabel>)

    fun getNotos(libraryId: Long): Flow<List<Noto>>

     fun getNotoById(notoId: Long): Flow<Noto>

    suspend fun countLibraryNotos(libraryId: Long): Int

    suspend fun countNotos(): Int

    //    suspend fun deleteNoto(notoId: Long, notoLabels: List<NotoLabel>)
    suspend fun deleteNoto(noto: Noto)

    suspend fun updateNoto(noto: Noto, notoLabels: List<NotoLabel>)

    suspend fun getNotoLabels(notoId: Long): List<NotoLabel>

    suspend fun getLabels(): List<Label>
}