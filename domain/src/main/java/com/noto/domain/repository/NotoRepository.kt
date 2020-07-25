package com.noto.domain.repository

import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoWithLabels
import kotlinx.coroutines.flow.Flow

interface NotoRepository {

    fun getNotos(): Flow<List<Noto>>

    fun getNoto(notoId: Long): Flow<Noto>

    suspend fun createNoto(noto: Noto)

    suspend fun updateNoto(noto: Noto)

    suspend fun deleteNoto(noto: Noto)

    suspend fun getNotoWithLabels(notoId: Long): Flow<Result<NotoWithLabels>>

    suspend fun createNotoWithLabels(noto: Noto, labels: Set<Label>)

    suspend fun updateNotoWithLabels(noto: Noto, labels: Set<Label>)

    suspend fun deleteNotoWithLabels(notoId: Long)

}