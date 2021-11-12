package com.noto.app.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.noto.app.domain.model.LibraryListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.sorted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LibraryListWidgetService : RemoteViewsService(), KoinComponent {

    private val storage by inject<LocalStorage>()
    private val libraryRepository by inject<LibraryRepository>()
    private val noteRepository by inject<NoteRepository>()

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return runBlocking {
            val sortingOrder = storage.get(Constants.LibraryListSortingOrderKey)
                .filterNotNull()
                .map { SortingOrder.valueOf(it) }
                .first()
            val sortingType = storage.get(Constants.LibraryListSortingTypeKey)
                .filterNotNull()
                .map { LibraryListSortingType.valueOf(it) }
                .first()
            val libraries = libraryRepository.getLibraries()
                .filterNotNull()
                .first()
                .sorted(sortingType, sortingOrder)
            val isShowNotesCount = storage.get(Constants.ShowNotesCountKey)
                .filterNotNull()
                .map { it.toBoolean() }
                .first()
            LibraryListRemoteViewsFactory(applicationContext, intent, libraries, isShowNotesCount) { libraryId ->
                runBlocking {
                    noteRepository.countNotesByLibraryId(libraryId)
                }
            }
        }
    }
}