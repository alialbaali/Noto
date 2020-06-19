package com.noto.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.noto.data.source.local.EntityStatusDataSource
import com.noto.domain.model.EntityStatus

@Dao
interface EntityStatusDao : EntityStatusDataSource {

    @Insert
    override fun createEntityStatus(entityStatus: EntityStatus)

    @Query("SELECT * FROM entities_status ORDER BY id")
    override fun getEntitiesStatus(): List<EntityStatus>

    @Delete
    override fun deleteEntityStatus(entityStatus: EntityStatus)

}
