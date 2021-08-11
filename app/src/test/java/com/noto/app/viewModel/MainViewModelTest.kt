package com.noto.app.viewModel

import com.noto.app.di.appModule
import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.fakeLocalDataSourceModule
import com.noto.app.main.MainViewModel
import com.noto.app.testRepositoryModule
import com.noto.app.util.LayoutManager
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
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
            viewModel.libraries
                .first()
                .shouldBeEmpty()
        }

        "get layout manager should return grid by default" {
            viewModel.layoutManager
                .first() shouldBe LayoutManager.Grid
        }

        "update layout manager to linear" {
            viewModel.updateLayoutManager(LayoutManager.Linear)
            viewModel.layoutManager
                .first() shouldBe LayoutManager.Linear
        }

        "count notes should return zero notes when library id is 0" {
            viewModel.countNotes(0) shouldBeExactly 0
        }

        "count notes should return all notes with matching library id" {
            viewModel.countNotes(1) shouldBeExactly 3
        }
    }
}