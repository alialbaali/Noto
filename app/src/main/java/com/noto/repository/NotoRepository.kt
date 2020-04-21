package com.noto.repository

import com.noto.database.NotoDao
import com.noto.domain.Block
import com.noto.domain.NoteBlock
import com.noto.domain.Noto
import com.noto.domain.TodoBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotoRepository(private val dao: NotoDao) {

    suspend fun getNotos(libraryId: Long): List<Noto> {
        return withContext(Dispatchers.IO) {
            dao.getNotos(libraryId)
        }
    }

    suspend fun getNotoById(notoId: Long): Noto {
        return withContext(Dispatchers.IO) {
            dao.getNotoById(notoId)
        }
    }

    suspend fun updateNoto(noto: Noto) {
        dao.updateNoto(noto)
    }

    suspend fun countLibraryNotos(libraryId: Long): Int {
        return withContext(Dispatchers.IO) {
            dao.countLibraryNotos(libraryId)
        }
    }

    suspend fun countNotos(): Int {
        return withContext(Dispatchers.IO) {
            dao.countNotos()
        }
    }

    suspend fun insertAll(noto: Noto, blocks: List<Block>) {
        withContext(Dispatchers.Default) {
            val noteBlocks = blocks.filterIsInstance<NoteBlock>()
            val todoBlocks = blocks.filterIsInstance<TodoBlock>()
            withContext(Dispatchers.IO) {
                dao.insertAll(noto, noteBlocks, todoBlocks)
            }
        }
    }

    suspend fun updateAll(noto: Noto, blocks: List<Block>) {
        withContext(Dispatchers.Default) {
            val noteBlocks = blocks.filterIsInstance<NoteBlock>()
            val todoBlocks = blocks.filterIsInstance<TodoBlock>()
            withContext(Dispatchers.IO) {
                dao.updateAll(noto, noteBlocks, todoBlocks)
            }
        }
    }
}