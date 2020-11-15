package com.noto.data.repository

import com.noto.data.NotoRepositoryImpl
import com.noto.data.source.fake.FakeNotoDao
import com.noto.domain.model.Note
import com.noto.domain.repository.NotoRepository
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.ZonedDateTime

private val notoRepositoryModule = module {

    single { FakeNotoDao() }

    single<NotoRepository> { NotoRepositoryImpl(get<FakeNotoDao>()) }

}

private const val LIBRARY_ID = 1L

class NotoRepositoryTest : KoinTest, StringSpec() {

    private val notoRepository by inject<NotoRepository>()

    private val notos by lazy { runBlocking { notoRepository.getNotosByLibraryId().single() } }

    private val noto = Note(libraryId = LIBRARY_ID, title = "TITLE", body = "BODY", position = 0)

    private val updatedNoto = noto.copy(title = "UPDATED TITLE", body = "UPDATED BODY", isArchived = true, reminderDate = ZonedDateTime.now())

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        startKoin { modules(notoRepositoryModule) }
    }

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        getKoin().close()
    }

    init {

        "get notos"{

            val result = notoRepository.getNotosByLibraryId().single()

            result shouldHaveSize 0
            result.shouldBeEmpty()

        }

        "create noto"{

            notoRepository.createNoto(noto)

            notos shouldContain noto
            notos shouldHaveSize 1
            notos.shouldNotContainNull()
            notos.shouldHaveElementAt(0, noto)

        }


        "update noto" {

            notoRepository.updateNoto(updatedNoto)

            val result = notos.find { it.id == noto.id }

            notos shouldContain updatedNoto
            notos shouldNotContain noto

            result shouldNotBe null
            result!!.isArchived.shouldBeTrue()
            result.reminderDate shouldNotBe null
            result.title shouldBe updatedNoto.title
            result.body shouldBe updatedNoto.body

        }

        "get noto"{

            val result = notoRepository.getNotoById(noto.id).single()

            result shouldBe updatedNoto
            result shouldNotBe noto
            result.isArchived.shouldBeTrue()
            result.reminderDate shouldNotBe null
            result.title shouldBe updatedNoto.title
            result.body shouldBe updatedNoto.body

        }

        "delete noto"{

            notoRepository.deleteNoto(updatedNoto)

            notos.shouldBeEmpty()
            notos shouldHaveSize 0
            notos shouldNotContain noto
            notos shouldNotContain updatedNoto

        }

    }

}