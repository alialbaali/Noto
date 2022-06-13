package com.noto.app.note

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.databinding.NoteReadingModeFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Note
import com.noto.app.label.labelItem
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteReadingModeFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId) }

    private val args by navArgs<NoteReadingModeFragmentArgs>()

    private val windowInsetsController by lazy {
        activity?.window?.decorView?.let { view ->
            ViewCompat.getWindowInsetsController(view)
        }
    }

    private val notificationManager by lazy {
        context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteReadingModeFragmentBinding.inflate(inflater, container, false).withBinding {
            setupFadeTransition()
            setupState()
            setupListeners()
        }

    private fun NoteReadingModeFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }

        fab.setOnClickListener {
            val scrollPosition = nsv.scrollY
            val isTitleVisible = tvNoteTitle.isLayoutVisible(root)
            val isBodyVisible = tvNoteBody.isLayoutVisible(root)
            navController?.navigate(
                NoteReadingModeFragmentDirections
                    .actionNoteReadingModeFragmentToNoteFragment(
                        args.folderId,
                        args.noteId,
                        scrollPosition = scrollPosition,
                        isTitleVisible = isTitleVisible,
                        isBodyVisible = isBodyVisible,
                    )
            )
        }
    }

    private fun NoteReadingModeFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = HorizontalListItemAnimator()
        abl.bringToFront()

        viewModel.isDoNotDisturb
            .onEach { isDoNotDisturb ->
                if (isDoNotDisturb && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager.isNotificationPolicyAccessGranted)
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
            }
            .launchIn(lifecycleScope)

        viewModel.isScreenOn
            .onEach { isScreenOn ->
                if (isScreenOn)
                    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                else
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            .launchIn(lifecycleScope)

        viewModel.isFullScreen
            .onEach { isFullScreen ->
                if (isFullScreen) {
                    windowInsetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
                } else {
                    windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
                }
            }
            .launchIn(lifecycleScope)

        viewModel.folder
            .onEach { folder -> setupFolder(folder) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.note,
            viewModel.font,
        ) { note, font -> setupNote(note, font) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.folder,
            viewModel.labels,
        ) { folder, labels ->
            rv.withModels {
                labels.filterValues { it }.forEach { entry ->
                    labelItem {
                        id(entry.key.id)
                        label(entry.key)
                        isSelected(entry.value)
                        color(folder.color)
                        onClickListener { _ -> }
                        onLongClickListener { _ -> false }
                    }
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun NoteReadingModeFragmentBinding.setupNote(note: Note, font: Font) {
        tvNoteTitle.text = note.title
        tvNoteBody.text = note.body
        tvNoteTitle.isVisible = note.title.isNotBlank()
        tvNoteBody.isVisible = note.body.isNotBlank()
        tvNoteTitle.setBoldFont(font)
        tvNoteBody.setSemiboldFont(font)
    }

    private fun NoteReadingModeFragmentBinding.setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            val highlightColor = color.withDefaultAlpha()
            tb.title = folder.getTitle(context)
            tb.setTitleTextColor(color)
            tvNoteTitle.setLinkTextColor(color)
            tvNoteBody.setLinkTextColor(color)
            tvNoteTitle.highlightColor = highlightColor
            tvNoteBody.highlightColor = highlightColor
            tb.navigationIcon?.mutate()?.setTint(color)
            fab.backgroundTintList = color.toColorStateList()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                nsv.verticalScrollbarThumbDrawable?.mutate()?.setTint(color)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager.isNotificationPolicyAccessGranted)
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
    }
}