package com.noto.domain.repository

import com.noto.domain.model.Label
import com.noto.domain.model.Note
import com.noto.domain.model.NotoWithLabels
import kotlinx.coroutines.flow.Flow

interface NotoRepository {

    fun getNotosByLibraryId(libraryId: Long): Flow<List<Note>>

    fun getArchivedNotosByLibraryId(libraryId: Long): Flow<List<Note>>

    fun getNotoById(notoId: Long): Flow<Note>

    suspend fun createNoto(note: Note)

    suspend fun updateNoto(note: Note)

    suspend fun deleteNoto(note: Note)

    suspend fun getNotoWithLabels(notoId: Long): Flow<Result<NotoWithLabels>>

    suspend fun createNotoWithLabels(note: Note, labels: Set<Label>)

    suspend fun updateNotoWithLabels(note: Note, labels: Set<Label>)

    suspend fun deleteNotoWithLabels(notoId: Long)

}