package com.noto.data.repository

import com.noto.data.source.fake.FakeLibraryDao
import com.noto.domain.model.Library
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

val libraryRepositoryModule = module {

    single { FakeLibraryDao() }

    single { LibraryRepositoryImpl(get<FakeLibraryDao>()) }

}

class LibraryRepositoryTest : KoinTest, StringSpec() {

    private val libraryRepository by inject<LibraryRepositoryImpl>()

    private val libraries by lazy { libraryRepository.getLibraries().run { runBlocking { single() } } }

    private val library = Library(libraryTitle = "LIBRARY", notoColor = NotoColor.BLUE, notoIcon = NotoIcon.CODE, libraryPosition = 0)

    private val updatedLibrary = library.copy(libraryTitle = "UPDATED LIBRARY", notoColor = NotoColor.GREEN, notoIcon = NotoIcon.HOME, libraryPosition = 4)

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        startKoin { modules(libraryRepositoryModule) }
    }

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        getKoin().close()
    }

    init {

        "get libraries"{



        }


        "create library" {

            libraryRepository.createLibrary(library)

            libraries shouldContain library
            libraries shouldHaveSize 1
            libraries.shouldNotContainNoNulls()

        }

        "update library"{

            libraryRepository.updateLibrary(updatedLibrary)

            libraries shouldContain updatedLibrary
            libraries shouldNotContain library
            libraries.first() shouldBe library

        }

        "delete library"{

            libraryRepository.deleteLibrary(library)

            libraries shouldNotContain library
            libraries shouldNotContain updatedLibrary
            libraries shouldHaveSize 0
            libraries.shouldBeEmpty()

        }

    }


}