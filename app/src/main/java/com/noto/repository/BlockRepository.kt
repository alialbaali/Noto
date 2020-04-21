package com.noto.repository

import com.noto.database.BlockDao
import com.noto.domain.Block
import com.noto.domain.NoteBlock
import com.noto.domain.TodoBlock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BlockRepository(private val dao: BlockDao) {

    suspend fun getBlocks(notoId: Long): List<Block> {
        return withContext(Dispatchers.IO) {
            dao.getBlocks(notoId)
        }
    }

    suspend fun deleteBlocks(blocks: List<Block>) {
        withContext(Dispatchers.Default) {
            val noteBlocks = blocks.filterIsInstance<NoteBlock>()
            val todoBlocks = blocks.filterIsInstance<TodoBlock>()
            withContext(Dispatchers.IO) {
                dao.deleteBlocks(noteBlocks, todoBlocks)
            }
        }
    }

    suspend fun countBlocks(notoId: Long): Int {
        return withContext(Dispatchers.IO) {
            dao.countBlocks(notoId)
        }
    }
}