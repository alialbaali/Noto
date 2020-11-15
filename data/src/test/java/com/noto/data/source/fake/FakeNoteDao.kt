package com.noto.data.source.fake

import com.noto.domain.local.NoteLocalDataSource
import com.noto.domain.model.Note
import com.noto.domain.model.NoteLabel
import com.noto.domain.model.NoteWithLabels
import com.noto.domain.replaceWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeNoteDao : NoteLocalDataSource {

    private val notos = mutableListOf<Note>()

    fun getNotes(): Flow<List<Note>> = flowOf(notos)
    override fun getNotesByLibraryId(libraryId: Long): Flow<List<Note>> {
        TODO("Not yet implemented")
    }

    override fun getArchivedNotesByLibraryId(libraryId: Long): Flow<List<Note>> {
        TODO("Not yet implemented")
    }

    override fun getNoteById(noteId: Long): Flow<Note> = flowOf(notos.first { it.id == noteId })

    override suspend fun createNote(note: Note) {
        notos.add(note.copy(id = notos.size.toLong()))
    }

    override suspend fun updateNote(note: Note) = notos.replaceWith(note) {
        it.id == note.id
    }

    override suspend fun deleteNote(note: Note) {
        notos.remove(note)
    }

    override fun getNoteWithLabels(notoId: Long): Flow<NoteWithLabels> {
        TODO("Not yet implemented")
    }

    override fun createNoteWithLabels(note: Note, notoLabels: Set<NoteLabel>) {
        TODO("Not yet implemented")
    }

    override fun updateNoteWithLabels(note: Note, notoLabels: Set<NoteLabel>) {
        TODO("Not yet implemented")
    }

    override fun deleteNoteWithLabels(notoId: Long) {
        TODO("Not yet implemented")
    }
}