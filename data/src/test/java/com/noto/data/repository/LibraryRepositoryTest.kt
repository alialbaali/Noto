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

    private val library = Library(title = "LIBRARY", color = NotoColor.BLUE, icon = NotoIcon.CODE, position = 0)

    private val updatedLibrary = library.copy(title = "UPDATED LIBRARY", color = NotoColor.GREEN, icon = NotoIcon.HOME, position = 4)

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

            val result = libraries.find { it.id == updatedLibrary.id }

            libraries shouldContain updatedLibrary
            libraries shouldNotContain library
            libraries.first() shouldBe updatedLibrary
            result shouldNotBe null
            result!!.title shouldBe updatedLibrary.title
            result.color shouldBe updatedLibrary.color
            result.icon shouldBe updatedLibrary.icon

        }

        "get library"{

            val result = libraryRepository.getLibraryById(library.id).single()

            result shouldBe  updatedLibrary
            result shouldNotBe library
            result.title shouldBe updatedLibrary.title
            result.color shouldBe updatedLibrary.color
            result.icon shouldBe updatedLibrary.icon

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