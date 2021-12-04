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
                viewModel.getData(libraryId)
            setupState()
            setupListeners()
        }
    }

    private fun NoteListWidgetConfigActivityBinding.setupState() {
        setResult(Activity.RESULT_CANCELED)
        listWidget.root.clipToOutline = true
        listWidget.lv.dividerHeight = 16.dp
        listWidget.lv.setPaddingRelative(8.dp, 16.dp, 8.dp, 100.dp)
        gridWidget.root.isVisible = false
        gridWidget.root.clipToOutline = true
        gridWidget.gv.horizontalSpacing = 16.dp
        gridWidget.gv.verticalSpacing = 16.dp
        gridWidget.gv.setPaddingRelative(8.dp, 16.dp, 8.dp, 100.dp)

        viewModel.isWidgetCreated
            .onEach { isCreated ->
                if (isCreated)
                    btnCreate.text = stringResource(R.string.update_widget)
            }
            .launchIn(lifecycleScope)

        combine(viewModel.library, viewModel.notes, viewModel.labels) { library, notes, labels ->
            val filteredNotes = notes.filter { it.second.containsAll(labels.filterSelected()) }
            val color = colorResource(library.color.toResource())
            val colorStateList = colorStateResource(library.color.toResource())
            tvFilterLabels.isVisible = labels.isNotEmpty()
            rv.isVisible = labels.isNotEmpty()
            listWidget.tvLibraryTitle.text = library.title
            listWidget.tvLibraryTitle.setTextColor(color)
            listWidget.fab.background?.setTint(color)
            gridWidget.tvLibraryTitle.text = library.title
            gridWidget.tvLibraryTitle.setTextColor(color)
            gridWidget.fab.background?.setTint(color)
            val tab = when (library.layout) {
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
                listWidget.lv.isVisible = false
                gridWidget.gv.isVisible = false
                listWidget.tvPlaceholder.isVisible = true
                gridWidget.tvPlaceholder.isVisible = true
            } else {
                listWidget.lv.isVisible = true
                gridWidget.gv.isVisible = true
                listWidget.tvPlaceholder.isVisible = false
                gridWidget.tvPlaceholder.isVisible = false
                val layoutResourceId = when (viewModel.widgetLayout.value) {
                    Layout.Linear -> R.layout.note_list_widget
                    Layout.Grid -> R.layout.note_grid_widget
                }
                val adapter = NoteListWidgetAdapter(
                    this@NoteListWidgetConfigActivity,
                    layoutResourceId,
                    filteredNotes,
                    library.isShowNoteCreationDate,
                    library.color,
                    library.notePreviewSize,
                )
                listWidget.lv.adapter = adapter
                gridWidget.gv.adapter = adapter
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
                swWidgetHeader.isChecked = isEnabled
                listWidget.llHeader.isVisible = isEnabled
                gridWidget.llHeader.isVisible = isEnabled
                swAppIcon.isVisible = isEnabled
                swEditWidget.isVisible = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.isEditWidgetButtonEnabled
            .onEach { isEnabled ->
                listWidget.llEditWidget.isVisible = isEnabled
                gridWidget.llEditWidget.isVisible = isEnabled
                swEditWidget.isChecked = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.isAppIconEnabled
            .onEach { isEnabled ->
                listWidget.ivAppIcon.isVisible = isEnabled
                gridWidget.ivAppIcon.isVisible = isEnabled
                if (isEnabled) {
                    listWidget.tvLibraryTitle.setPadding(0.dp, 16.dp, 0.dp, 16.dp)
                    gridWidget.tvLibraryTitle.setPadding(0.dp, 16.dp, 0.dp, 16.dp)
                } else {
                    listWidget.tvLibraryTitle.setPadding(16.dp)
                    gridWidget.tvLibraryTitle.setPadding(16.dp)
                }
                swAppIcon.isChecked = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.isNewLibraryButtonEnabled
            .onEach { isEnabled ->
                listWidget.fab.isVisible = isEnabled
                gridWidget.fab.isVisible = isEnabled
                swNewLibrary.isChecked = isEnabled
            }
            .launchIn(lifecycleScope)

        viewModel.widgetRadius
            .onEach { radius ->
                val drawable = drawableResource(radius.toWidgetShapeId())
                val headerDrawable = drawableResource(radius.toWidgetHeaderShapeId())
                sWidgetRadius.value = radius.toFloat()
                listWidget.ll.background = drawable
                gridWidget.ll.background = drawable
                listWidget.llHeader.background = headerDrawable
                gridWidget.llHeader.background = headerDrawable
            }
            .launchIn(lifecycleScope)

        viewModel.widgetLayout
            .onEach { layout ->
                when (layout) {
                    Layout.Linear -> {
                        tlWidgetLayout.selectTab(tlWidgetLayout.getTabAt(0))
                        listWidget.root.isVisible = true
                        gridWidget.root.isVisible = false
                    }
                    Layout.Grid -> {
                        tlWidgetLayout.selectTab(tlWidgetLayout.getTabAt(1))
                        listWidget.root.isVisible = false
                        gridWidget.root.isVisible = true
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
            viewModel.setIsWidgetCreated()
            viewModel.saveLabelIds()
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
            viewModel.getData(libraryId)
        }
        val args = bundleOf(Constants.LibraryId to 0L, Constants.SelectedLibraryItemClickListener to selectLibraryItemClickListener)
        SelectLibraryDialogFragment(isDismissible)
            .apply { arguments = args }
            .show(supportFragmentManager, null)
    }
}