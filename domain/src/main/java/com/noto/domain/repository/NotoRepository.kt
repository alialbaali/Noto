package com.noto.domain.repository

import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import kotlinx.coroutines.flow.Flow

interface NotoRepository {

    suspend fun createNoto(noto: Noto, labels: List<Label>)

    suspend fun deleteNoto(noto: Noto, labels: List<Label>)

    suspend fun updateNoto(noto: Noto, labels: List<Label>)

    suspend fun getNotos(libraryId: Long) : Result<Flow<List<Noto>>>

    suspend fun getNoto(notoId: Long): Result<Pair<Flow<Noto>, MutableList<Label>>>
}