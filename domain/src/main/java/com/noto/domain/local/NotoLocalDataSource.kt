package com.noto.domain.local

import com.noto.domain.model.Note
import com.noto.domain.model.NotoLabel
import com.noto.domain.model.NotoWithLabels
import kotlinx.coroutines.flow.Flow

interface NotoLocalDataSource {

    fun getNotosByLibraryId(libraryId: Long): Flow<List<Note>>

    fun getArchivedNotosByLibraryId(libraryId: Long): Flow<List<Note>>

    fun getNotoById(notoId: Long): Flow<Note>

    suspend fun createNoto(note: Note)

    suspend fun updateNoto(note: Note)

    suspend fun deleteNoto(note: Note)

    fun getNotoWithLabels(notoId: Long): Flow<NotoWithLabels>

    fun createNotoWithLabels(note: Note, notoLabels: Set<NotoLabel>)

    fun updateNotoWithLabels(note: Note, notoLabels: Set<NotoLabel>)

    fun deleteNotoWithLabels(notoId: Long)

}