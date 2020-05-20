package com.noto.domain.repository

import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel

interface NotoRepository {

    suspend fun createNoto(noto: Noto)

    suspend fun deleteNoto(noto: Noto)

    suspend fun updateNoto(noto: Noto)

    suspend fun getNotos(libraryId: Long) : Result<List<Noto>>

    suspend fun getNoto(notoId: Long): Result<Noto>

    suspend fun getNotoLabels(notoId: Long): Result<List<Label>>

    suspend fun insertNotoLabels(notoLabels: List<NotoLabel>)

    suspend fun updateNotoLabels(notoLabels: List<NotoLabel>)
}