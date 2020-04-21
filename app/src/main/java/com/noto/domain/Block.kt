package com.noto.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "blocks")
abstract class Block(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "block_id")
    var blockId: Long = 0L,

    @ForeignKey(
        entity = Noto::class,
        parentColumns = ["noto_id"],
        childColumns = ["noto_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "noto_id")
    var notoId: Long,

    @ColumnInfo(name = "block_position")
    val blockPosition: Int,

    @ColumnInfo(name = "block_color")
    var notoColor: NotoColor = NotoColor.GRAY,

    @ColumnInfo(name = "block_creation_date")
    var blockCreationDate: Date = Date()
)

abstract class TextBlock(
    notoId: Long,
    blockPosition: Int,
    @ColumnInfo(name = "block_body")
    var blockBody: String
) : Block(notoId = notoId, blockPosition = blockPosition)

@Entity(tableName = "todo_blocks")
class TodoBlock(
    blockPosition: Int,
    notoId: Long,
    blockBody: String,
    @ColumnInfo(name = "block_is_checked")
    val blockIsChecked: Boolean = false
) : TextBlock(notoId = notoId, blockPosition = blockPosition, blockBody = blockBody)

@Entity(tableName = "note_blocks")
class NoteBlock(
    blockPosition: Int,
    notoId: Long,
    blockBody: String = "",
    @ColumnInfo(name = "block_type")
    val blockType: BlockType = BlockType.NOTE
) : TextBlock(notoId = notoId, blockPosition = blockPosition, blockBody = blockBody)
