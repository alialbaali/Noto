package com.noto.data.source.fake

import com.noto.data.source.local.NotoLocalDataSource
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import com.noto.domain.model.NotoWithLabels
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeNotoDao : NotoLocalDataSource {

    private val notos = mutableListOf<Noto>()

    override fun getNotos(libraryId: Long): Flow<List<Noto>> = flowOf(notos.filter { noto -> noto.libraryId == libraryId && !noto.notoIsArchived })

    override fun getArchivedNotos(): Flow<List<Noto>> = flowOf(notos.filter { noto -> noto.notoIsArchived })

    override fun getAllNotos(): Flow<List<Noto>> = flowOf(notos.filter { noto -> !noto.notoIsArchived })

    override fun getNoto(notoId: Long): Flow<Noto> = flowOf(notos.first { it.notoId == notoId })

    override suspend fun createNoto(noto: Noto) {
        notos.add(noto.copy(notoId = notos.size.toLong()))
    }

    override suspend fun updateNoto(noto: Noto) = notos.replaceWith(noto) {
        it.notoId == noto.notoId
    }

    override suspend fun deleteNoto(noto: Noto) {
        notos.remove(noto)
    }

    override suspend fun countNotos(): Int = notos.count()

    override fun getNotoWithLabels(notoId: Long): Flow<NotoWithLabels> {
        TODO("Not yet implemented")
    }

    override fun createNotoWithLabels(noto: Noto, notoLabels: Set<NotoLabel>) {
        TODO("Not yet implemented")
    }

    override fun updateNotoWithLabels(noto: Noto, notoLabels: Set<NotoLabel>) {
        TODO("Not yet implemented")
    }

    override fun deleteNotoWithLabels(notoId: Long) {
        TODO("Not yet implemented")
    }
}