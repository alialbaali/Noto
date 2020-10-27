package com.noto.data.repository

import com.noto.data.LibraryRepositoryImpl
import com.noto.data.source.fake.FakeLibraryDao
import com.noto.domain.model.Library
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import com.noto.domain.repository.LibraryRepository
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

val libraryRepositoryModule = module {

    single { FakeLibraryDao() }

    single<LibraryRepository> { LibraryRepositoryImpl(get<FakeLibraryDao>()) }

}

class LibraryRepositoryTest : KoinTest, StringSpec() {

    private val libraryRepository by inject<LibraryRepository>()

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

            val list = libraryRepository.getLibraries().single()

            list shouldHaveSize 0
            list.shouldBeEmpty()

        }


        "create library" {

            libraryRepository.createLibrary(library)

            libraries shouldContain library
            libraries shouldHaveSize 1
            libraries.shouldContainNoNulls()

        }

        "update library"{

            libraryRepository.updateLibrary(updatedLibrary)

            val result = libraries.find { it.libraryId == updatedLibrary.libraryId }

            libraries shouldContain updatedLibrary
            libraries shouldNotContain library
            libraries.first() shouldBe updatedLibrary
            result shouldNotBe null
            result!!.libraryTitle shouldBe updatedLibrary.libraryTitle
            result.notoColor shouldBe updatedLibrary.notoColor
            result.notoIcon shouldBe updatedLibrary.notoIcon

        }

        "get library"{

            val result = libraryRepository.getLibraryById(library.libraryId).single()

            result shouldBe  updatedLibrary
            result shouldNotBe library
            result.libraryTitle shouldBe updatedLibrary.libraryTitle
            result.notoColor shouldBe updatedLibrary.notoColor
            result.notoIcon shouldBe updatedLibrary.notoIcon

        }

        "delete library"{

            libraryRepository.deleteLibrary(updatedLibrary)

            libraries shouldNotContain library
            libraries shouldNotContain updatedLibrary
            libraries shouldHaveSize 0
            libraries.shouldBeEmpty()

        }

    }


}