package com.noto.app.note

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.NoteReminderDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Note
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteReminderDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId) }

    private val args by navArgs<NoteReminderDialogFragmentArgs>()

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    private val isDarkMode by lazy {
        resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private val dateTimeDialogThemeId by lazy {
        if (isDarkMode) android.R.style.Theme_DeviceDefault_Dialog else android.R.style.Theme_DeviceDefault_Light_Dialog
    }

    private val is24HourFormat by lazy { DateFormat.is24HourFormat(context) }

    private val parentView by lazy { parentFragment?.view }

    private val folderColor by lazy { viewModel.folder.value.color }

    private val anchorViewId by lazy { R.id.bab }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteReminderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun NoteReminderDialogFragmentBinding.setupListeners() {
        btnDone.setOnClickListener {
            context?.let { context ->
                val instant = viewModel.setNoteReminder()
                val stringId = R.string.reminder_is_set
                val drawableId = R.drawable.ic_round_notifications_active_24
                val note = viewModel.note.value
                alarmManager?.createAlarm(context, note.folderId, note.id, instant.toEpochMilliseconds())
                parentView?.snackbar(
                    context.stringResource(stringId, instant.format(context)),
                    drawableId,
                    anchorViewId,
                    folderColor
                )
            }
            dismiss()
        }

        btnCancel.setOnClickListener {
            context?.let { context ->
                val stringId = R.string.reminder_is_cancelled
                val drawableId = R.drawable.ic_round_cancel_24
                viewModel.cancelNoteReminder()
                alarmManager?.cancelAlarm(context, viewModel.note.value.id)
                parentView?.snackbar(
                    context.stringResource(stringId),
                    drawableId,
                    anchorViewId,
                    folderColor
                )
            }
            dismiss()
        }
    }

    private fun NoteReminderDialogFragmentBinding.setupState() {
        viewModel.folder
            .onEach { folder -> setupFolder(folder) }
            .launchIn(lifecycleScope)

        viewModel.note
            .onEach { note -> setupNote(note) }
            .launchIn(lifecycleScope)

        combine(viewModel.reminderDate, viewModel.reminderTime) { date, time ->
            tvDateValue.text = date.format()
            tvTimeValue.text = time.format(is24HourFormat)

            llDate.setOnClickListener {
                showDateDialog(date)
            }

            llTime.setOnClickListener {
                showTimeDialog(time)
            }
        }.launchIn(lifecycleScope)
    }

    private fun NoteReminderDialogFragmentBinding.setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            val colorStateList = color.toColorStateList()
            tb.vHead.background?.mutate()?.setTint(color)
            tb.tvDialogTitle.setTextColor(color)
            btnDone.background?.setTint(color)
            btnCancel.rippleColor = color.withDefaultAlpha().toColorStateList()
            llDate.background?.setRippleColor(colorStateList)
            llTime.background?.setRippleColor(colorStateList)
        }
    }

    private fun NoteReminderDialogFragmentBinding.setupNote(note: Note) {
        context?.let { context ->
            if (note.reminderDate == null) {
                btnCancel.isVisible = false
                tb.tvDialogTitle.text = context.stringResource(R.string.new_note_reminder)
                btnDone.text = context.stringResource(R.string.set_reminder)
            } else {
                btnCancel.isVisible = true
                tb.tvDialogTitle.text = context.stringResource(R.string.edit_note_reminder)
                btnDone.text = context.stringResource(R.string.update_reminder)
            }
        }
    }

    private fun showDateDialog(initialDate: LocalDate) {
        val initialYear = initialDate.year
        val initialMonth = initialDate.monthNumber - 1
        val initialDay = initialDate.dayOfMonth
        context?.let { context ->
            val buttonTextColor = context.colorAttributeResource(R.attr.notoPrimaryColor)
            DatePickerDialog(
                /* context = */ context,
                /* themeResId = */ dateTimeDialogThemeId,
                /* listener = */ { _, year, month, day ->
                    val date = LocalDate(year, month + 1, day)
                    viewModel.setReminderDate(date)
                },
                /* year = */ initialYear,
                /* monthOfYear = */ initialMonth,
                /* dayOfMonth = */ initialDay
            ).apply {
                datePicker.minDate = Clock.System.now().toEpochMilliseconds().minus(1000)
                show()
                getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(buttonTextColor)
                getButton(DatePickerDialog.BUTTON_NEUTRAL)?.setTextColor(buttonTextColor)
                getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(buttonTextColor)
            }
        }
    }

    private fun showTimeDialog(initialTime: LocalTime) {
        val initialHour = initialTime.hour
        val initialMinute = initialTime.minute
        context?.let { context ->
            val buttonTextColor = context.colorAttributeResource(R.attr.notoPrimaryColor)
            TimePickerDialog(
                /* context = */ context,
                /* themeResId = */ dateTimeDialogThemeId,
                /* listener = */ { _, hour, minute ->
                    val time = LocalTime(hour, minute)
                    viewModel.setReminderTime(time)
                },
                /* hourOfDay = */ initialHour,
                /* minute = */ initialMinute,
                /* is24HourView = */ is24HourFormat
            )
                .apply {
                    show()
                    getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(buttonTextColor)
                    getButton(DatePickerDialog.BUTTON_NEUTRAL)?.setTextColor(buttonTextColor)
                    getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(buttonTextColor)
                }
        }
    }
}