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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.components.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.NoteReminderDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Note
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class NoteReminderDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId) }

    private val args by navArgs<NoteReminderDialogFragmentArgs>()

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteReminderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun showDateTimeDialog() {

        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        val isDarkMode = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        val theme = if (isDarkMode) android.R.style.Theme_DeviceDefault_Dialog
        else android.R.style.Theme_DeviceDefault_Light_Dialog

        val is24HourFormat = DateFormat.is24HourFormat(context)

        context?.let { context ->
            val buttonTextColor = context.colorAttributeResource(R.attr.notoPrimaryColor)
            DatePickerDialog(context, theme, { _, year, month, day ->
                TimePickerDialog(context, theme, { _, hour, minute ->
                    LocalDateTime(year, month + 1, day, hour, minute)
                        .toInstant(TimeZone.currentSystemDefault())
                        .also { viewModel.setNoteReminder(it) }
                        .toEpochMilliseconds()
                        .also {
                            val note = viewModel.note.value
                            alarmManager?.createAlarm(context, note.folderId, note.id, it)
                        }
                }, startHour, startMinute, is24HourFormat)
                    .apply {
                        show()
                        getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(buttonTextColor)
                        getButton(DatePickerDialog.BUTTON_NEUTRAL)?.setTextColor(buttonTextColor)
                        getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(buttonTextColor)
                    }
            }, startYear, startMonth, startDay)
                .apply {
                    datePicker.minDate = Clock.System
                        .now()
                        .toEpochMilliseconds()
                        .minus(1000)
                    show()
                    getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(buttonTextColor)
                    getButton(DatePickerDialog.BUTTON_NEUTRAL)?.setTextColor(buttonTextColor)
                    getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(buttonTextColor)
                }
        }
    }

    private fun NoteReminderDialogFragmentBinding.setupListeners() {
        btnDone.setOnClickListener {
            dismiss()
        }

        til.setEndIconOnClickListener {
            if (viewModel.note.value.reminderDate == null) {
                showDateTimeDialog()
            } else {
                context?.let { context ->
                    alarmManager?.cancelAlarm(context, viewModel.note.value.id)
                }
                viewModel.setNoteReminder(null)
            }
        }
    }

    private fun NoteReminderDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.new_note_reminder)

        viewModel.folder
            .onEach { folder -> setupFolder(folder) }
            .launchIn(lifecycleScope)

        viewModel.note
            .onEach { note -> setupNote(note) }
            .launchIn(lifecycleScope)
    }

    private fun NoteReminderDialogFragmentBinding.setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            tb.vHead.background?.mutate()?.setTint(color)
            tb.tvDialogTitle.setTextColor(color)
            til.boxStrokeColor = color
        }
    }

    private fun NoteReminderDialogFragmentBinding.setupNote(note: Note) {
        context?.let { context ->
            if (note.reminderDate == null) {
                et.setText(getString(R.string.no_note_reminder))
                til.endIconDrawable = context.drawableResource(R.drawable.ic_round_notification_add_24)
                tb.tvDialogTitle.text = context.stringResource(R.string.new_note_reminder)
            } else {
                til.endIconDrawable = context.drawableResource(R.drawable.ic_round_cancel_24)
                tb.tvDialogTitle.text = context.stringResource(R.string.edit_note_reminder)
                et.setText(note.reminderDate.format(context))
            }
        }
    }
}