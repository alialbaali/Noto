package com.noto.data.source.fake

import com.noto.domain.local.NotoLocalDataSource
import com.noto.domain.model.Note
import com.noto.domain.model.NotoLabel
import com.noto.domain.model.NotoWithLabels
import com.noto.domain.replaceWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeNotoDao : NotoLocalDataSource {

    private val notos = mutableListOf<Note>()

    fun getNotos(): Flow<List<Note>> = flowOf(notos)
    override fun getNotosByLibraryId(libraryId: Long): Flow<List<Note>> {
        TODO("Not yet implemented")
    }

    override fun getArchivedNotosByLibraryId(libraryId: Long): Flow<List<Note>> {
        TODO("Not yet implemented")
    }

    override fun getNotoById(notoId: Long): Flow<Note> = flowOf(notos.first { it.id == notoId })

    override suspend fun createNoto(note: Note) {
        notos.add(note.copy(id = notos.size.toLong()))
    }

    override suspend fun updateNoto(note: Note) = notos.replaceWith(note) {
        it.id == note.id
    }

    override suspend fun deleteNoto(note: Note) {
        notos.remove(note)
    }

    override fun getNotoWithLabels(notoId: Long): Flow<NotoWithLabels> {
        TODO("Not yet implemented")
    }

    override fun createNotoWithLabels(note: Note, notoLabels: Set<NotoLabel>) {
        TODO("Not yet implemented")
    }

    override fun updateNotoWithLabels(note: Note, notoLabels: Set<NotoLabel>) {
        TODO("Not yet implemented")
    }

    override fun deleteNotoWithLabels(notoId: Long) {
        TODO("Not yet implemented")
    }
}