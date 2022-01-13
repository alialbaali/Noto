package com.noto.app.note

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ListUpdateCallback
import com.noto.app.AppActivity
import com.noto.app.R
import com.noto.app.databinding.NoteFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.label.labelItem
import com.noto.app.label.newLabelItem
import com.noto.app.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val DebounceTimeoutMillis = 250L

class NoteFragment : Fragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId, args.body, args.labelsIds) }

    private val args by navArgs<NoteFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    @OptIn(FlowPreview::class)
    private fun NoteFragmentBinding.setupState() {
        nsv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show))
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        abl.bringToFront()
        bab.setRoundedCorners()

        viewModel.library
            .onEach { library -> setupLibrary(library) }
            .distinctUntilChangedBy { it.isSetNewNoteCursorOnTitle }
            .onEach { library ->
                if (args.noteId == 0L) {
                    activity?.showKeyboard(root)
                    if (library.isSetNewNoteCursorOnTitle)
                        etNoteTitle.requestFocus()
                    else
                        etNoteBody.requestFocus()
                }
            }
            .launchIn(lifecycleScope)

        viewModel.note
            .onEach { note ->
                setupShortcut(note)
                context?.let { context ->
                    tvWordCount.text = context.pluralsResource(R.plurals.words_count, note.wordsCount, note.wordsCount).lowercase()
                    fab.setImageDrawable(
                        if (note.reminderDate == null)
                            context.drawableResource(R.drawable.ic_round_notification_add_24)
                        else
                            context.drawableResource(R.drawable.ic_round_edit_notifications_24)
                    )
                }
                if (note.isValid)
                    enableBottomAppBarActions()
                else
                    disableBottomAppBarActions()
            }
            .combine(viewModel.font) { note, font ->
                if (etNoteTitle.text.isNullOrBlank() && etNoteBody.text.isNullOrBlank())
                    setupNote(note, font)
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.library,
            viewModel.labels,
        ) { library, labels ->
            rv.withModels {
                addModelBuildListener {
                    it.dispatchTo(NoteListUpdateCallback)
                }
                labels.forEach { entry ->
                    labelItem {
                        id(entry.key.id)
                        label(entry.key)
                        isSelected(entry.value)
                        color(library.color)
                        onClickListener { _ ->
                            if (entry.value)
                                viewModel.unselectLabel(entry.key.id)
                            else
                                viewModel.selectLabel(entry.key.id)
                        }
                        onLongClickListener { _ ->
                            navController
                                ?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToLabelDialogFragment(args.libraryId, entry.key.id))
                            true
                        }
                    }
                }
                newLabelItem {
                    id("new")
                    color(library.color)
                    onClickListener { _ ->
                        navController?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToNewLabelDialogFragment(args.libraryId))
                    }
                }
            }
        }.launchIn(lifecycleScope)

        combine(
            etNoteTitle.textAsFlow(emitNewTextOnly = true)
                .filterNotNull(),
            etNoteBody.textAsFlow(emitNewTextOnly = true)
                .filterNotNull(),
        ) { title, body -> title to body }
            .debounce(DebounceTimeoutMillis)
            .onEach { (title, body) ->
                viewModel.createOrUpdateNote(title.toString(), body.toString())
                context?.updateAllWidgetsData()
                context?.updateNoteListWidgets(viewModel.library.value.id)
            }
            .launchIn(lifecycleScope)
    }

    private fun NoteFragmentBinding.enableBottomAppBarActions() {
        fab.alpha = 1F
        fab.isEnabled = true
        bab.menu.forEach {
            it.isEnabled = true
            it.icon?.alpha = 255
        }
    }

    private fun NoteFragmentBinding.disableBottomAppBarActions() {
        fab.alpha = 0.50F
        fab.isEnabled = false
        bab.menu.forEach {
            it.isEnabled = false
            it.icon?.alpha = 128
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun NoteFragmentBinding.setupListeners() {
        fab.setOnClickListener {
            navController
                ?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToNoteReminderDialogFragment(args.libraryId, viewModel.note.value.id))
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToMainFragment())
        }

        bab.setOnSwipeGestureListener {
            navController?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToMainFragment())
        }

        val backCallback = {
            if (args.body != null)
                navController?.popBackStack(R.id.mainFragment, false)

            navController?.navigateUp()
            viewModel.createOrUpdateNote(
                etNoteTitle.text.toString(),
                etNoteBody.text.toString(),
            )
            context?.updateAllWidgetsData()
            context?.updateNoteListWidgets(viewModel.library.value.id)
            activity?.hideKeyboard(root)
        }

        activity?.onBackPressedDispatcher
            ?.addCallback(viewLifecycleOwner) { backCallback() }
            ?.isEnabled = true

        tb.setNavigationOnClickListener {
            backCallback()
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share_note -> {
                    launchShareNoteIntent(viewModel.note.value)
                    true
                }
                R.id.reading_mode -> {
                    navController
                        ?.navigateSafely(NoteFragmentDirections.actionNoteFragmentToNoteReadingModeFragment(args.libraryId, viewModel.note.value.id))
                    true
                }
                R.id.more -> {
                    navController?.navigateSafely(
                        NoteFragmentDirections.actionNoteFragmentToNoteDialogFragment(
                            args.libraryId,
                            viewModel.note.value.id,
                            R.id.libraryFragment
                        )
                    )
                    true
                }
                else -> false
            }
        }

        val nsvClickListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                etNoteBody.requestFocus()
                etNoteBody.showKeyboardUsingImm()
                return super.onSingleTapUp(e)
            }
        }
        val gestureDetector = GestureDetector(requireContext(), nsvClickListener)
        nsv.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            v.performClick()
        }
    }

    private fun NoteFragmentBinding.setupLibrary(library: Library) {
        context?.let { context ->
            val backgroundColor = context.attributeColoResource(R.attr.notoBackgroundColor)
            val color = context.colorResource(library.color.toResource())
            val colorStateList = color.toColorStateList()
            tb.title = if (library.isInbox)
                context.stringResource(R.string.inbox)
            else
                library.title
            tb.setTitleTextColor(color)
            tvCreatedAt.setTextColor(color)
            tvWordCount.setTextColor(color)
            tb.navigationIcon?.mutate()?.setTint(color)
            fab.backgroundTintList = colorStateList
            bab.backgroundTint = colorStateList
            bab.menu.forEach { it.icon?.mutate()?.setTint(backgroundColor) }
            bab.navigationIcon?.mutate()?.setTint(backgroundColor)
            etNoteTitle.setLinkTextColor(color)
            etNoteBody.setLinkTextColor(color)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                fab.outlineAmbientShadowColor = color
                fab.outlineSpotShadowColor = color
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                etNoteTitle.textCursorDrawable?.mutate()?.setTint(color)
                etNoteBody.textCursorDrawable?.mutate()?.setTint(color)
            }
        }
    }

    private fun NoteFragmentBinding.setupNote(note: Note, font: Font) {
        etNoteTitle.setText(note.title)
        etNoteBody.setText(note.body)
        etNoteTitle.setSelection(note.title.length)
        etNoteBody.setSelection(note.body.length)
        etNoteTitle.setBoldFont(font)
        etNoteBody.setSemiboldFont(font)
        context?.let { context ->
            tvCreatedAt.text = context.stringResource(R.string.created, note.creationDate.format(context))
        }
    }

    private fun NoteFragmentBinding.setupShortcut(note: Note) {
        if (note.id != 0L && note.isValid) {
            val intent = Intent(Constants.Intent.ActionOpenNote, null, context, AppActivity::class.java).apply {
                putExtra(Constants.LibraryId, note.libraryId)
                putExtra(Constants.NoteId, note.id)
            }

            val label = note.title.ifBlank { note.body }

            context?.let { context ->
                val shortcut = ShortcutInfoCompat.Builder(context, note.id.toString())
                    .setIntent(intent)
                    .setShortLabel(label)
                    .setLongLabel(label)
                    .setIcon(IconCompat.createWithResource(context, R.mipmap.ic_note))
                    .build()

                try {
                    ShortcutManagerCompat.getDynamicShortcuts(context).also { shortcuts ->
                        val maxShortcutsCount = ShortcutManagerCompat.getMaxShortcutCountPerActivity(context)
                        if (shortcuts.count() == maxShortcutsCount) {
                            shortcuts.removeLastOrNull()
                            shortcuts.add(0, shortcut)
                            ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts)
                        } else {
                            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
                        }
                    }
                } catch (exception: Throwable) {
                    ShortcutManagerCompat.removeAllDynamicShortcuts(context)
                }
            }
        }
    }

}

private val NoteFragmentBinding.NoteListUpdateCallback
    get() = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) = if (rv.childCount == 1) rv.scrollToPosition(0) else Unit
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }