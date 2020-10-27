package com.noto.domain.local

import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import com.noto.domain.model.NotoWithLabels
import kotlinx.coroutines.flow.Flow

interface NotoLocalDataSource {

    fun getNotosByLibraryId(libraryId: Long): Flow<List<Noto>>

    fun getArchivedNotosByLibraryId(libraryId: Long): Flow<List<Noto>>

    fun getNotoById(notoId: Long): Flow<Noto>

    suspend fun createNoto(noto: Noto)

    suspend fun updateNoto(noto: Noto)

    suspend fun deleteNoto(noto: Noto)

    fun getNotoWithLabels(notoId: Long): Flow<NotoWithLabels>

    fun createNotoWithLabels(noto: Noto, notoLabels: Set<NotoLabel>)

    fun updateNotoWithLabels(noto: Noto, notoLabels: Set<NotoLabel>)

    fun deleteNotoWithLabels(notoId: Long)

}