package com.noto.domain.repository

import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import com.noto.domain.model.NotoWithLabels
import kotlinx.coroutines.flow.Flow

interface NotoRepository {

    suspend fun createNoto(noto: Noto)

    suspend fun deleteNoto(noto: Noto)

    suspend fun updateNoto(noto: Noto)

    suspend fun getNotos(libraryId: Long): Result<Flow<List<Noto>>>

    suspend fun getArchivedNotos(): Result<Flow<List<Noto>>>

    suspend fun getNoto(notoId: Long): Result<Flow<Noto>>

    suspend fun getAllNotos(): Result<Flow<List<Noto>>>

    suspend fun getNotoWithLabels(notoId: Long) : Result<Flow<NotoWithLabels>>

    suspend fun createNotoWithLabels(noto: Noto, labels: Set<Label>)

    suspend fun updateNotoWithLabels(noto: Noto, labels: Set<Label>)

    suspend fun deleteNotoWithLabels(notoId: Long)

}