package com.noto.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import com.noto.domain.Block
import com.noto.domain.NoteBlock
import com.noto.domain.TodoBlock

@Dao
interface BlockDao {

    @Query("SELECT * FROM todo_blocks WHERE noto_id = :notoId")
    suspend fun getTodoBlocks(notoId: Long): List<TodoBlock>

    @Query("SELECT * FROM note_blocks WHERE noto_id = :notoId")
    suspend fun getNoteBlocks(notoId: Long): List<NoteBlock>

    @Transaction
    suspend fun getBlocks(notoId: Long): List<Block> {
        return getNoteBlocks(notoId).plus(getTodoBlocks(notoId)).sortedBy { it.blockPosition }
    }

    @Delete
    suspend fun deleteBlocks(noteBlocks: List<NoteBlock>, todoBlocks: List<TodoBlock>)

    @Query("SELECT COUNT(*) FROM note_blocks WHERE noto_id = :notoId")
    suspend fun countNoteBlocks(notoId: Long): Int

    @Query("SELECT COUNT(*) FROM todo_blocks WHERE noto_id = :notoId ")
    suspend fun countTodoBlocks(notoId: Long): Int

    @Transaction
    suspend fun countBlocks(notoId: Long): Int {
        return countNoteBlocks(notoId).plus(countTodoBlocks(notoId))
    }
}