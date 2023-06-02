package com.noto.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.noto.app.R
import com.noto.app.components.BaseActivity
import com.noto.app.databinding.NoteListWidgetConfigActivityBinding
import com.noto.app.domain.model.FilteringType
import com.noto.app.domain.model.NotoColor
import com.noto.app.label.labelItem
import com.noto.app.main.SelectFolderDialogFragment
import com.noto.app.util.Constants
import com.noto.app.util.colorResource
import com.noto.app.util.dp
import com.noto.app.util.drawableResource
import com.noto.app.util.filterSelected
import com.noto.app.util.filterByLabels
import com.noto.app.util.getTitle
import com.noto.app.util.setupColors
import com.noto.app.util.stringResource
import com.noto.app.util.toResource
import com.noto.app.util.toWidgetHeaderShapeId
import com.noto.app.util.toWidgetShapeId
import com.noto.app.util.updateNoteWidget
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListWidgetConfigActivity : BaseActivity() {

    private val viewModel by viewModel<NoteListWidgetConfigViewModel> { parametersOf(appWidgetId) }

    private val appWidgetId by lazy {
        intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    private val folderId by lazy { intent?.getLongExtra(Constants.FolderId, 0) ?: 0 }

    private var selectFolderDialogFragment: SelectFolderDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NoteListWidgetConfigActivityBinding.inflate(layoutInflater).withBinding {
            setContentView(root)
            if (folderId == 0L)
                showSelectFolderDialog(false)
            else
                viewModel.getWidgetData(folderId)
            setupState()
            setupListeners()
        }
    }

    override fun onPause() {
        super.onPause()
        selectFolderDialogFragment?.dismiss()
    }

    private fun NoteListWidgetConfigActivityBinding.setupState() {
        setResult(Activity.RESULT_CANCELED)
        widget.lv.dividerHeight = 16.dp
        widget.lv.setPaddingRelative(8.dp, 16.dp, 8.dp, 100.dp)
        widget.root.clipToOutline = true
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        listOf(swWidgetHeader, swEditWidget, swAppIcon, swNewFolder)
            .onEach { it.setupColors() }

        viewModel.isWidgetCreated
            .onEach { isCreated ->
                if (isCreated) {
                    tb.title = stringResource(R.string.edit_notes_widget)
                    btnCreate.text = stringResource(R.string.update_widget)
                }
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.folder,
            viewModel.notes,
            viewModel.labels,
            viewModel.widgetFilteringType,
        ) { folder, notes, labels, filteringType ->
            val filteredNotes = notes.filterByLabels(labels.filterSelected(), filteringType)
            val color = colorResource(folder.color.toResource())
            val placeholderId = when {
                notes.isEmpty() -> R.string.folder_is_empty
                else -> R.string.no_notes_found_labels
            }
            tb.setTitleTextColor(color)
            rv.isVisible = labels.isNotEmpty()
            llFiltering.isVisible = labels.isNotEmpty()
            btnCreate.setBackgroundColor(color)
            tvFolderValue.text = folder.getTitle(this@NoteListWidgetConfigActivity)
            widget.tvFolderTitle.text = folder.getTitle(this@NoteListWidgetConfigActivity)
            widget.tvFolderTitle.setTextColor(color)
            widget.fab.background?.setTint(color)
            widget.ivFab.setColorFilter(color)
            widget.tvPlaceholder.text = stringResource(placeholderId)

            if (filteredNotes.isEmpty()) {
                widget.lv.isVisible = false
                widget.tvPlaceholder.isVisible = true
            } else {
                widget.lv.isVisible = true
                widget.tvPlaceholder.isVisible = false
                widget.lv.adapter = NoteListWidgetAdapter(
                    this@NoteListWidgetConfigActivity,
                    R.layout.note_list_widget,
                    filteredNotes,
                    folder.isShowNoteCreationDate,
                    folder.color,
                    folder.notePreviewSize,
                )
            }

            rv.withModels {
                labels.forEach { entry ->
                    labelItem {
                        id(entry.key.id)
                        label(entry.key)
                        isSelected(entry.value)
                        color(NotoColor.Black)
                        onClickListener { _ ->
                            if (entry.value)
                                viewModel.deselectLabel(entry.key.id)
                            else
                                viewModel.selectLabel(entry.key.id)
                        }
                        onLongClickListener { _ -> false }
                    }
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.isWidgetHeaderEnabled
            .onEach { isEnabled ->
                widget.llHeader.isVisible = isEnabled
                swWidgetHeader.isChecked = isEnabled
                swAppIcon.isVisible = isEnabled
                swEditWidget.isVisible = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.isEditWidgetButtonEnabled
            .onEach { isEnabled ->
                widget.llEditWidget.isVisible = isEnabled
                swEditWidget.isChecked = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.isAppIconEnabled
            .onEach { isEnabled ->
                widget.ivAppIcon.isVisible = isEnabled
                swAppIcon.isChecked = isEnabled
                if (isEnabled)
                    widget.tvFolderTitle.setPadding(0.dp, 16.dp, 0.dp, 16.dp)
                else
                    widget.tvFolderTitle.setPadding(16.dp)
            }
            .launchIn(lifecycleScope)

        viewModel.isNewFolderButtonEnabled
            .onEach { isEnabled ->
                widget.fab.isVisible = isEnabled
                swNewFolder.isChecked = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.widgetRadius
            .onEach { radius ->
                sWidgetRadius.value = radius.toFloat()
                widget.ll.background = drawableResource(radius.toWidgetShapeId())
                widget.llHeader.background = drawableResource(radius.toWidgetHeaderShapeId())
            }
            .launchIn(lifecycleScope)

        viewModel.widgetFilteringType
            .onEach { filteringType ->
                tvFilteringValue.text = when (filteringType) {
                    FilteringType.Inclusive -> R.string.inclusive
                    FilteringType.Exclusive -> R.string.exclusive
                    FilteringType.Strict -> R.string.strict
                }.let(this@NoteListWidgetConfigActivity::stringResource)
            }
            .launchIn(lifecycleScope)

        viewModel.icon
            .onEach { icon -> widget.ivAppIcon.setImageResource(icon.toResource()) }
            .launchIn(lifecycleScope)
    }

    private fun NoteListWidgetConfigActivityBinding.setupListeners() {
        tb.setOnClickListener {
            nsv.smoothScrollTo(0, 0)
        }

        llFolder.setOnClickListener {
            showSelectFolderDialog(true)
        }

        swWidgetHeader.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsWidgetHeaderEnabled(isChecked)
        }

        swEditWidget.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsEditWidgetButtonEnabled(isChecked)
        }

        swAppIcon.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsAppIconEnabled(isChecked)
        }

        swNewFolder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsNewFolderButtonEnabled(isChecked)
        }

        sWidgetRadius.addOnChangeListener { _, value, _ ->
            viewModel.setWidgetRadius(value.toInt())
        }

        llFiltering.setOnClickListener {
            NoteListFilteringWidgetDialogFragment(viewModel.widgetFilteringType.value) { filteringType ->
                viewModel.setFilteringType(filteringType)
            }.show(supportFragmentManager, null)
        }

        btnCreate.setOnClickListener {
            viewModel.createOrUpdateWidget()
            updateNoteWidget(appWidgetId)
            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }
    }

    private fun showSelectFolderDialog(isDismissible: Boolean) {
        val args = bundleOf(
            Constants.FilteredFolderIds to longArrayOf(),
            Constants.IsDismissible to isDismissible,
            Constants.SelectedFolderId to viewModel.folder.value.id,
            Constants.Title to stringResource(R.string.select_folder),
        )
        selectFolderDialogFragment = SelectFolderDialogFragment { folderId, _ -> viewModel.getWidgetData(folderId) }
            .apply {
                arguments = args
                show(supportFragmentManager, null)
            }
    }
}