package com.noto.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.noto.app.R
import com.noto.app.databinding.LibraryListWidgetConfigActivityBinding
import com.noto.app.domain.model.Layout
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LibraryListWidgetConfigActivity : AppCompatActivity() {

    private val viewModel by viewModel<LibraryListWidgetConfigViewModel> { parametersOf(appWidgetId) }

    private val appWidgetId by lazy {
        intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        LibraryListWidgetConfigActivityBinding.inflate(layoutInflater).withBinding {
            setContentView(root)
            setupState()
            setupListeners()
        }
    }

    private fun LibraryListWidgetConfigActivityBinding.setupState() {
        setResult(Activity.RESULT_CANCELED)
        listWidget.root.clipToOutline = true
        listWidget.lv.dividerHeight = 16.dp
        listWidget.lv.setPaddingRelative(8.dp, 16.dp, 8.dp, 100.dp)
        gridWidget.root.isVisible = false
        gridWidget.root.clipToOutline = true
        gridWidget.gv.horizontalSpacing = 16.dp
        gridWidget.gv.verticalSpacing = 16.dp
        gridWidget.gv.setPaddingRelative(8.dp, 16.dp, 8.dp, 100.dp)
        listOf(swWidgetHeader, swEditWidget, swAppIcon, swNewLibrary, swNotesCount)
            .onEach { it.setupColors() }

        combine(
            viewModel.libraries,
            viewModel.widgetLayout,
            viewModel.isNotesCountEnabled,
        ) { libraries, widgetLayout, isShowNotesCount ->
            swNotesCount.isChecked = isShowNotesCount
            if (libraries.isEmpty()) {
                listWidget.lv.isVisible = false
                gridWidget.gv.isVisible = false
                listWidget.tvPlaceholder.isVisible = true
                gridWidget.tvPlaceholder.isVisible = true
            } else {
                listWidget.lv.isVisible = true
                gridWidget.gv.isVisible = true
                listWidget.tvPlaceholder.isVisible = false
                gridWidget.tvPlaceholder.isVisible = false
                val layoutResourceId = when (widgetLayout) {
                    Layout.Linear -> R.layout.library_list_widget
                    Layout.Grid -> R.layout.library_grid_widget
                }
                val adapter = LibraryListWidgetAdapter(
                    this@LibraryListWidgetConfigActivity,
                    libraries,
                    layoutResourceId,
                    isShowNotesCount,
                    viewModel::countNotes
                )
                listWidget.lv.adapter = adapter
                gridWidget.gv.adapter = adapter
            }
        }.launchIn(lifecycleScope)

        viewModel.isWidgetCreated
            .onEach { isCreated ->
                if (isCreated)
                    btnCreate.text = stringResource(R.string.update_widget)
            }
            .launchIn(lifecycleScope)

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
                    listWidget.tvAppName.setPadding(0.dp, 16.dp, 0.dp, 16.dp)
                    gridWidget.tvAppName.setPadding(0.dp, 16.dp, 0.dp, 16.dp)
                } else {
                    listWidget.tvAppName.setPadding(16.dp)
                    gridWidget.tvAppName.setPadding(16.dp)
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

    private fun LibraryListWidgetConfigActivityBinding.setupListeners() {
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

        swNotesCount.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsNotesCountEnabled(isChecked)
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
            viewModel.setIsWidgetCreated()
            val appWidgetManager = AppWidgetManager.getInstance(this@LibraryListWidgetConfigActivity)
            // Needed to update the visibility of notes count in library items.
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, viewModel.widgetLayout.value.toWidgetViewId())
            appWidgetManager.updateAppWidget(
                appWidgetId,
                createLibraryListWidgetRemoteViews(
                    appWidgetId,
                    viewModel.widgetLayout.value,
                    viewModel.isWidgetHeaderEnabled.value,
                    viewModel.isEditWidgetButtonEnabled.value,
                    viewModel.isAppIconEnabled.value,
                    viewModel.isNewLibraryButtonEnabled.value,
                    viewModel.widgetRadius.value,
                    viewModel.libraries.value.isEmpty(),
                )
            )
            val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }
    }
}