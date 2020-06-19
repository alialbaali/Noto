package com.noto.domain.interactor

import com.noto.domain.repository.SyncRepository

class SyncData(private val syncRepository: SyncRepository) {

    suspend operator fun invoke() = syncRepository.syncData()

}