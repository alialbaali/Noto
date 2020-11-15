package com.noto.data.repository

import com.noto.data.NoteRepositoryImpl
import com.noto.data.source.fake.FakeNoteDao
import com.noto.domain.model.Note
import com.noto.domain.repository.NoteRepository
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

    single { FakeNoteDao() }

    single<NoteRepository> { NoteRepositoryImpl(get<FakeNoteDao>()) }

}

private const val LIBRARY_ID = 1L

class NoteRepositoryTest : KoinTest, StringSpec() {

    private val notoRepository by inject<NoteRepository>()

    private val notos by lazy { runBlocking { notoRepository.getNotesByLibraryId().single() } }

    private val noto = Note(libraryId = LIBRARY_ID, title = "TITLE", body = "BODY", position = 0)

    private val updatedNote = noto.copy(title = "UPDATED TITLE", body = "UPDATED BODY", isArchived = true, reminderDate = ZonedDateTime.now())

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

            val result = notoRepository.getNotesByLibraryId().single()

            result shouldHaveSize 0
            result.shouldBeEmpty()

        }

        "create note"{

            notoRepository.createNote(noto)

            notos shouldContain noto
            notos shouldHaveSize 1
            notos.shouldNotContainNull()
            notos.shouldHaveElementAt(0, noto)

        }


        "update note" {

            notoRepository.updateNote(updatedNote)

            val result = notos.find { it.id == noto.id }

            notos shouldContain updatedNote
            notos shouldNotContain noto

            result shouldNotBe null
            result!!.isArchived.shouldBeTrue()
            result.reminderDate shouldNotBe null
            result.title shouldBe updatedNote.title
            result.body shouldBe updatedNote.body

        }

        "get note"{

            val result = notoRepository.getNoteById(noto.id).single()

            result shouldBe updatedNote
            result shouldNotBe noto
            result.isArchived.shouldBeTrue()
            result.reminderDate shouldNotBe null
            result.title shouldBe updatedNote.title
            result.body shouldBe updatedNote.body

        }

        "delete note"{

            notoRepository.deleteNote(updatedNote)

            notos.shouldBeEmpty()
            notos shouldHaveSize 0
            notos shouldNotContain noto
            notos shouldNotContain updatedNote

        }

    }

}