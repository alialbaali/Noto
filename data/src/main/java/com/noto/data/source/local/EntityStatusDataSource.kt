package com.noto.data.source.local

import com.noto.domain.model.EntityStatus

interface EntityStatusDataSource {

    fun createEntityStatus(entityStatus: EntityStatus)

    fun getEntitiesStatus(): List<EntityStatus>

    fun deleteEntityStatusById(entityStatusId: Long)

}