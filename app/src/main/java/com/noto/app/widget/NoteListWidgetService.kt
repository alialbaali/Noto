package com.noto.app.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteLabelRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.util.Constants
import com.noto.app.util.mapWithLabels
import com.noto.app.util.sorted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NoteListWidgetService : RemoteViewsService(), KoinComponent {

    private val libraryRepository by inject<LibraryRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val labelRepository by inject<LabelRepository>()
    private val noteLabelRepository by inject<NoteLabelRepository>()

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val libraryId = intent?.getLongExtra(Constants.LibraryId, 0) ?: 0
        return runBlocking {
            val library = libraryRepository.getLibraryById(libraryId)
                .filterNotNull()
                .first()
            val notes = noteRepository.getNotesByLibraryId(libraryId)
                .filterNotNull()
                .map {
                    val labels = labelRepository.getLabelsByLibraryId(libraryId)
                        .filterNotNull()
                        .first()
                    val noteLabels = noteLabelRepository.getNoteLabels()
                        .filterNotNull()
                        .first()
                    it.mapWithLabels(labels, noteLabels).sorted(library.sortingType, library.sortingOrder)
                }
                .first()
            NoteListRemoteViewsFactory(applicationContext, intent, library, notes)
        }
    }
}