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
        widget.lv.dividerHeight = 16.dp
        widget.gv.horizontalSpacing = 16.dp
        widget.gv.verticalSpacing = 16.dp
        val tab = when (viewModel.widgetLayout.value) {
            Layout.Linear -> tlWidgetLayout.getTabAt(0)
            Layout.Grid -> tlWidgetLayout.getTabAt(1)
        }
        tlWidgetLayout.selectTab(tab)

        listOf(swWidgetHeader, swEditWidget, swAppIcon, swNewLibrary, swNotesCount)
            .onEach { it.setupColors() }

        combine(
            viewModel.libraries,
            viewModel.widgetLayout,
            viewModel.isNotesCountEnabled,
        ) { libraries, widgetLayout, isShowNotesCount ->
            swNotesCount.isChecked = isShowNotesCount
            if (libraries.isEmpty()) {
                widget.lv.isVisible = false
                widget.gv.isVisible = false
                widget.tvPlaceholder.isVisible = true
            } else {
                widget.lv.isVisible = true
                widget.gv.isVisible = true
                widget.tvPlaceholder.isVisible = false
                val adapter = LibraryListWidgetAdapter(
                    this@LibraryListWidgetConfigActivity,
                    libraries,
                    R.layout.library_list_widget,
                    isShowNotesCount,
                    viewModel::countNotes
                )
                widget.lv.adapter = adapter
                widget.gv.adapter = adapter
            }
        }.launchIn(lifecycleScope)

        viewModel.isWidgetCreated
            .onEach { isCreated ->
                if (isCreated) {
                    tb.title = stringResource(R.string.edit_libraries_widget)
                    btnCreate.text = stringResource(R.string.update_widget)
                }
            }
            .launchIn(lifecycleScope)

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
                    widget.tvAppName.setPadding(0.dp, 16.dp, 0.dp, 16.dp)
                else
                    widget.tvAppName.setPadding(16.dp)
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
                widget.ll.background = drawableResource(radius.toWidgetHeaderShapeId())
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
            viewModel.createOrUpdateWidget()
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