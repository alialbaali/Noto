package com.noto.app.note

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.NoteReminderDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat

private const val DatePickerDialogTag = "DatePickerDialog"
private const val TimePickerDialogTag = "TimePickerDialog"

class NoteReminderDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.folderId, args.noteId) }

    private val args by navArgs<NoteReminderDialogFragmentArgs>()

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    private val is24HourFormat by lazy { DateFormat.is24HourFormat(context) }

    private val timeFormat by lazy { if (is24HourFormat) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H }

    private val parentView by lazy { parentFragment?.view }

    private val folderColor by lazy { viewModel.folder.value.color }

    private val anchorViewId by lazy { R.id.bab }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteReminderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun NoteReminderDialogFragmentBinding.setupListeners() {
        btnSet.setOnClickListener {
            context?.let { context ->
                viewModel.setNoteReminder()
                val instant = viewModel.reminderDateTime.value
                val date = instant.toLocalDate().format(lowercaseTimeSpan = true)
                val time = instant.toLocalTime().format(is24HourFormat)
                val stringId = R.string.reminder_is_set
                val drawableId = R.drawable.ic_round_notifications_active_24
                val note = viewModel.note.value
                alarmManager?.createAlarm(context, note.folderId, note.id, instant.toEpochMilliseconds())
                parentView?.snackbar(
                    context.stringResource(stringId, date, time),
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

        combine(viewModel.folder, viewModel.reminderDateTime) { folder, instant ->
            val date = instant.toLocalDate()
            val time = instant.toLocalTime()
            val epochMilliseconds = instant.toLocalDateTime(TimeZone.currentSystemDefault()).toInstant(TimeZone.UTC).toEpochMilliseconds()
            val datePickerDialogTheme = folder.color.toDatePickerDialogTheme()
            val timePickerDialogTheme = folder.color.toTimePickerDialogTheme()

            tvDateValue.text = date.format()
            tvTimeValue.text = time.format(is24HourFormat)

            llDate.setOnClickListener {
                val datePickerDialog = createDatePickerDialog(epochMilliseconds, datePickerDialogTheme)
                val isDialogShown = parentFragmentManager.findFragmentByTag(DatePickerDialogTag)?.isAdded ?: false
                if (!isDialogShown) datePickerDialog.show(parentFragmentManager, DatePickerDialogTag)
            }

            llTime.setOnClickListener {
                val timePickerDialog = createTimePickerDialog(time.hour, time.minute, timeFormat, timePickerDialogTheme)
                val isDialogShown = parentFragmentManager.findFragmentByTag(TimePickerDialogTag)?.isAdded ?: false
                if (!isDialogShown) timePickerDialog.show(parentFragmentManager, TimePickerDialogTag)
            }

        }.launchIn(lifecycleScope)
    }

    private fun NoteReminderDialogFragmentBinding.setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toColorResourceId())
            val colorStateList = color.toColorStateList()
            tb.vHead.background?.mutate()?.setTint(color)
            tb.tvDialogTitle.setTextColor(color)
            btnSet.background?.mutate()?.setTint(color)
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
                btnSet.text = context.stringResource(R.string.set_reminder)
            } else {
                btnCancel.isVisible = true
                tb.tvDialogTitle.text = context.stringResource(R.string.edit_note_reminder)
                btnSet.text = context.stringResource(R.string.update_reminder)
            }
        }
    }

    private fun createDatePickerDialog(milliseconds: Long, @StyleRes themeResId: Int): MaterialDatePicker<Long> {
        val dateFormat = DateFormat.getDateFormat(context) as SimpleDateFormat
        val localizedDateFormat = dateFormat.toLocalizedPattern().let(::SimpleDateFormat)
        val calendarConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        return MaterialDatePicker.Builder.datePicker()
            .setSelection(milliseconds)
            .setTitleText(context?.stringResource(R.string.reminder_date))
            .setTheme(themeResId)
            .setTextInputFormat(localizedDateFormat)
            .setCalendarConstraints(calendarConstraints)
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    selection?.let { epochMilliseconds ->
                        viewModel.setReminderDate(epochMilliseconds)
                    }
                }
            }
    }

    private fun createTimePickerDialog(hour: Int, minute: Int, @TimeFormat timeFormat: Int, @StyleRes themeResId: Int): MaterialTimePicker {
        return MaterialTimePicker.Builder()
            .setTimeFormat(timeFormat)
            .setTitleText(context?.stringResource(R.string.reminder_time))
            .setTheme(themeResId)
            .setHour(hour)
            .setMinute(minute)
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    viewModel.setReminderTime(this.hour, this.minute)
                }
            }
    }

    private fun NotoColor.toDatePickerDialogTheme() = when (this) {
        NotoColor.Gray -> R.style.DatePickerDialog_Gray
        NotoColor.Blue -> R.style.DatePickerDialog_Blue
        NotoColor.Pink -> R.style.DatePickerDialog_Pink
        NotoColor.Cyan -> R.style.DatePickerDialog_Cyan
        NotoColor.Purple -> R.style.DatePickerDialog_Purple
        NotoColor.Red -> R.style.DatePickerDialog_Red
        NotoColor.Yellow -> R.style.DatePickerDialog_Yellow
        NotoColor.Orange -> R.style.DatePickerDialog_Orange
        NotoColor.Green -> R.style.DatePickerDialog_Green
        NotoColor.Brown -> R.style.DatePickerDialog_Brown
        NotoColor.BlueGray -> R.style.DatePickerDialog_BlueGray
        NotoColor.Teal -> R.style.DatePickerDialog_Teal
        NotoColor.Indigo -> R.style.DatePickerDialog_Indigo
        NotoColor.DeepPurple -> R.style.DatePickerDialog_DeepPurple
        NotoColor.DeepOrange -> R.style.DatePickerDialog_DeepOrange
        NotoColor.DeepGreen -> R.style.DatePickerDialog_DeepGreen
        NotoColor.LightBlue -> R.style.DatePickerDialog_LightBlue
        NotoColor.LightGreen -> R.style.DatePickerDialog_LightGreen
        NotoColor.LightRed -> R.style.DatePickerDialog_LightRed
        NotoColor.LightPink -> R.style.DatePickerDialog_LightPink
        NotoColor.Black -> R.style.DatePickerDialog
    }

    private fun NotoColor.toTimePickerDialogTheme() = when (this) {
        NotoColor.Gray -> R.style.TimePickerDialog_Gray
        NotoColor.Blue -> R.style.TimePickerDialog_Blue
        NotoColor.Pink -> R.style.TimePickerDialog_Pink
        NotoColor.Cyan -> R.style.TimePickerDialog_Cyan
        NotoColor.Purple -> R.style.TimePickerDialog_Purple
        NotoColor.Red -> R.style.TimePickerDialog_Red
        NotoColor.Yellow -> R.style.TimePickerDialog_Yellow
        NotoColor.Orange -> R.style.TimePickerDialog_Orange
        NotoColor.Green -> R.style.TimePickerDialog_Green
        NotoColor.Brown -> R.style.TimePickerDialog_Brown
        NotoColor.BlueGray -> R.style.TimePickerDialog_BlueGray
        NotoColor.Teal -> R.style.TimePickerDialog_Teal
        NotoColor.Indigo -> R.style.TimePickerDialog_Indigo
        NotoColor.DeepPurple -> R.style.TimePickerDialog_DeepPurple
        NotoColor.DeepOrange -> R.style.TimePickerDialog_DeepOrange
        NotoColor.DeepGreen -> R.style.TimePickerDialog_DeepGreen
        NotoColor.LightBlue -> R.style.TimePickerDialog_LightBlue
        NotoColor.LightGreen -> R.style.TimePickerDialog_LightGreen
        NotoColor.LightRed -> R.style.TimePickerDialog_LightRed
        NotoColor.LightPink -> R.style.TimePickerDialog_LightPink
        NotoColor.Black -> R.style.TimePickerDialog
    }
}