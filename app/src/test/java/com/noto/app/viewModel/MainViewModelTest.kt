package com.noto.app.viewModel

import com.noto.app.util.appModule
import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.fakeLocalDataSourceModule
import com.noto.app.main.MainViewModel
import com.noto.app.testRepositoryModule
import com.noto.app.domain.model.Layout
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

class MainViewModelTest : StringSpec(), KoinTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var noteRepository: NoteRepository

    init {
        beforeEach {
            startKoin {
                modules(appModule, testRepositoryModule, fakeLocalDataSourceModule)
            }
            viewModel = get()
            noteRepository = get()
            repeat(3) {
                val note = Note(id = it.toLong(), libraryId = 1, position = 0)
                noteRepository.createNote(note)
            }
        }

        afterEach {
            stopKoin()
        }

        "get libraries should return an empty list" {
            viewModel.state
                .map { it.libraries }
                .first()
                .shouldBeEmpty()
        }

        "get layout manager should return grid by default" {
            viewModel.state
                .map { it.layoutManager }
                .first() shouldBe Layout.Grid
        }

        "update layout manager to linear" {
            viewModel.updateLayout(Layout.Linear)
            viewModel.state
                .map { it.layoutManager }
                .first() shouldBe Layout.Linear
        }

        "count notes should return zero notes when library id is 0" {
            viewModel.countNotes(0) shouldBeExactly 0
        }

        "count notes should return all notes with matching library id" {
            viewModel.countNotes(1) shouldBeExactly 3
        }
    }
}