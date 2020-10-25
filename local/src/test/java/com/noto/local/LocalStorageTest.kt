package com.noto.local

import com.noto.domain.local.LocalStorage
import org.koin.dsl.module

val localDataSourceModule = module {
    single<LocalStorage> { LocalStorageImpl(get()) }
}

