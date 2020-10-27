package com.noto.data.source.fake

import com.noto.domain.local.NotoLocalDataSource
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import com.noto.domain.model.NotoWithLabels
import com.noto.domain.replaceWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeNotoDao : NotoLocalDataSource {

    private val notos = mutableListOf<Noto>()

    override fun getNotos(): Flow<List<Noto>> = flowOf(notos)

    override fun getNotoById(notoId: Long): Flow<Noto> = flowOf(notos.first { it.notoId == notoId })

    override suspend fun createNoto(noto: Noto) {
        notos.add(noto.copy(notoId = notos.size.toLong()))
    }

    override suspend fun updateNoto(noto: Noto) = notos.replaceWith(noto) {
        it.notoId == noto.notoId
    }

    override suspend fun deleteNoto(noto: Noto) {
        notos.remove(noto)
    }

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