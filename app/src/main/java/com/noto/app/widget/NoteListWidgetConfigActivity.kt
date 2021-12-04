package com.noto.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.noto.app.R
import com.noto.app.databinding.NoteListWidgetConfigActivityBinding
import com.noto.app.domain.model.Layout
import com.noto.app.label.labelItem
import com.noto.app.library.SelectLibraryDialogFragment
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListWidgetConfigActivity : AppCompatActivity() {

    private val viewModel by viewModel<NoteListWidgetConfigViewModel> { parametersOf(appWidgetId) }

    private val appWidgetId by lazy {
        intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    private val libraryId by lazy { intent?.getLongExtra(Constants.LibraryId, 0) ?: 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        NoteListWidgetConfigActivityBinding.inflate(layoutInflater).withBinding {
            setContentView(root)
            if (libraryId == 0L)
                showSelectLibraryDialog(false)
            else
                viewModel.getWidgetData(libraryId)
            setupState()
            setupListeners()
        }
    }

    private fun NoteListWidgetConfigActivityBinding.setupState() {
        setResult(Activity.RESULT_CANCELED)
        widget.lv.dividerHeight = 16.dp
        widget.gv.horizontalSpacing = 16.dp
        widget.gv.verticalSpacing = 16.dp

        viewModel.isWidgetCreated
            .onEach { isCreated ->
                if (isCreated) {
                    tb.title = stringResource(R.string.edit_notes_widget)
                    btnCreate.text = stringResource(R.string.update_widget)
                }
            }
            .launchIn(lifecycleScope)

        combine(viewModel.library, viewModel.notes, viewModel.labels) { library, notes, labels ->
            val filteredNotes = notes.filter { it.second.containsAll(labels.filterSelected()) }
            val color = colorResource(library.color.toResource())
            val colorStateList = colorStateResource(library.color.toResource())
            tvFilterLabels.isVisible = labels.isNotEmpty()
            rv.isVisible = labels.isNotEmpty()
            widget.tvLibraryTitle.text = library.title
            widget.tvLibraryTitle.setTextColor(color)
            widget.fab.background?.setTint(color)
            val tab = when (viewModel.widgetLayout.value) {
                Layout.Linear -> tlWidgetLayout.getTabAt(0)
                Layout.Grid -> tlWidgetLayout.getTabAt(1)
            }
            tlWidgetLayout.setSelectedTabIndicatorColor(color)
            tlWidgetLayout.selectTab(tab)
            listOf(swWidgetHeader, swEditWidget, swAppIcon, swNewLibrary)
                .onEach { it.setupColors(thumbCheckedColor = color, trackCheckedColor = color) }
            if (colorStateList != null) {
                tlWidgetLayout.tabRippleColor = colorStateList
                sWidgetRadius.trackActiveTintList = colorStateList
                sWidgetRadius.thumbTintList = colorStateList
                sWidgetRadius.tickInactiveTintList = colorStateList
            }
            if (filteredNotes.isEmpty()) {
                widget.lv.isVisible = false
                widget.gv.isVisible = false
                widget.tvPlaceholder.isVisible = true
            } else {
                widget.lv.isVisible = true
                widget.gv.isVisible = true
                widget.tvPlaceholder.isVisible = false
                val adapter = NoteListWidgetAdapter(
                    this@NoteListWidgetConfigActivity,
                    R.layout.note_list_widget,
                    filteredNotes,
                    library.isShowNoteCreationDate,
                    library.color,
                    library.notePreviewSize,
                )
                widget.lv.adapter = adapter
                widget.gv.adapter = adapter
            }

            rv.withModels {
                labels.forEach { entry ->
                    labelItem {
                        id(entry.key.id)
                        label(entry.key)
                        isSelected(entry.value)
                        color(library.color)
                        backgroundColor(colorResource(R.color.colorSurface))
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
                    widget.tvLibraryTitle.setPadding(0.dp, 16.dp, 0.dp, 16.dp)
                else
                    widget.tvLibraryTitle.setPadding(16.dp)
            }
            .launchIn(lifecycleScope)

        viewModel.isNewLibraryButtonEnabled
            .onEach { isEnabled ->
                widget.fab.isVisible = isEnabled
                swNewLibrary.isChecked = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.widgetRadius
            .onEach { radius ->
                sWidgetRadius.value = radius.toFloat()
                widget.ll.background = drawableResource(radius.toWidgetShapeId())
                widget.llHeader.background = drawableResource(radius.toWidgetHeaderShapeId())
            }
            .launchIn(lifecycleScope)

        viewModel.widgetLayout
            .onEach { layout ->
                when (layout) {
                    Layout.Linear -> {
                        tlWidgetLayout.selectTab(tlWidgetLayout.getTabAt(0))
                        widget.lv.isVisible = true
                        widget.gv.isVisible = false
                    }
                    Layout.Grid -> {
                        tlWidgetLayout.selectTab(tlWidgetLayout.getTabAt(1))
                        widget.lv.isVisible = false
                        widget.gv.isVisible = true
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun NoteListWidgetConfigActivityBinding.setupListeners() {
        tvSelectLibrary.setOnClickListener {
            showSelectLibraryDialog(true)
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

        swNewLibrary.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsNewLibraryButtonEnabled(isChecked)
        }

        sWidgetRadius.addOnChangeListener { _, value, _ ->
            viewModel.setWidgetRadius(value.toInt())
        }

        tlWidgetLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val layout = if (tab?.text == stringResource(R.string.list))
                        Layout.Linear
                    else
                        Layout.Grid
                    viewModel.setWidgetLayout(layout)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            }
        )

        btnCreate.setOnClickListener {
//            sendBroadcast() // Maybe we can send broadcast to NoteListWidgetProvider instead of updating it manually
            viewModel.createOrUpdateWidget()
            val appWidgetManager = AppWidgetManager.getInstance(this@NoteListWidgetConfigActivity)
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, viewModel.widgetLayout.value.toWidgetViewId())
            appWidgetManager.updateAppWidget(
                appWidgetId,
                createNoteListWidgetRemoteViews(
                    appWidgetId,
                    viewModel.widgetLayout.value,
                    viewModel.isWidgetHeaderEnabled.value,
                    viewModel.isEditWidgetButtonEnabled.value,
                    viewModel.isAppIconEnabled.value,
                    viewModel.isNewLibraryButtonEnabled.value,
                    viewModel.widgetRadius.value,
                    viewModel.library.value,
                    viewModel.notes.value.isEmpty(),
                )
            )
            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }
    }

    private fun showSelectLibraryDialog(isDismissible: Boolean) {
        val selectLibraryItemClickListener = SelectLibraryDialogFragment.SelectLibraryItemClickListener { libraryId ->
            viewModel.getWidgetData(libraryId)
        }
        val args = bundleOf(Constants.LibraryId to 0L, Constants.SelectedLibraryItemClickListener to selectLibraryItemClickListener)
        SelectLibraryDialogFragment(isDismissible)
            .apply { arguments = args }
            .show(supportFragmentManager, null)
    }
}