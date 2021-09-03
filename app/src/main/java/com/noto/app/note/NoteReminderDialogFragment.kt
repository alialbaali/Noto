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
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NoteReminderDialogFragmentBinding
import com.noto.app.domain.model.Library
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

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<NoteReminderDialogFragmentArgs>()

    private val alarmManager by lazy { requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        NoteReminderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            val baseDialogFragment = setupBaseDialogFragment()
            setupState(baseDialogFragment)
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

        val buttonTextColor = resources.colorResource(R.color.colorPrimary)

        val is24HourFormat = DateFormat.is24HourFormat(requireContext())

        DatePickerDialog(requireContext(), theme, { _, year, month, day ->
            TimePickerDialog(requireContext(), theme, { _, hour, minute ->
                LocalDateTime(year, month + 1, day, hour, minute)
                    .toInstant(TimeZone.currentSystemDefault())
                    .also { viewModel.setNoteReminder(it) }
                    .toEpochMilliseconds()
                    .also {
                        val note = viewModel.state.value.note
                        alarmManager.createAlarm(requireContext(), note.libraryId, note.id, it)
                    }
            }, startHour, startMinute, is24HourFormat)
                .apply {
                    show()
                    getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(buttonTextColor)
                    getButton(DatePickerDialog.BUTTON_NEUTRAL).setTextColor(buttonTextColor)
                    getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(buttonTextColor)
                }
        }, startYear, startMonth, startDay)
            .apply {
                datePicker.minDate = Clock.System
                    .now()
                    .toEpochMilliseconds()
                    .minus(1000)
                show()
                getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(buttonTextColor)
                getButton(DatePickerDialog.BUTTON_NEUTRAL).setTextColor(buttonTextColor)
                getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(buttonTextColor)
            }
    }

    private fun NoteReminderDialogFragmentBinding.setupListeners() {
        btnDone.setOnClickListener {
            dismiss()
        }

        til.setEndIconOnClickListener {
            if (viewModel.state.value.note.reminderDate == null) {
                showDateTimeDialog()
            } else {
                alarmManager.cancelAlarm(requireContext(), viewModel.state.value.note.id)
                viewModel.setNoteReminder(null)
            }
        }
    }

    private fun NoteReminderDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        viewModel.state
            .onEach { state ->
                setupLibrary(state.library, baseDialogFragment)
                setupNote(state.note, baseDialogFragment)
            }
            .launchIn(lifecycleScope)
    }

    private fun NoteReminderDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        tvDialogTitle.text = resources.stringResource(R.string.new_note_reminder)
    }

    private fun NoteReminderDialogFragmentBinding.setupLibrary(library: Library, baseDialogFragment: BaseDialogFragmentBinding) {
        val color = resources.colorResource(library.color.toResource())
        baseDialogFragment.vHead.background.setTint(color)
        baseDialogFragment.tvDialogTitle.setTextColor(color)
        til.boxStrokeColor = color
    }

    private fun NoteReminderDialogFragmentBinding.setupNote(note: Note, baseDialogFragment: BaseDialogFragmentBinding) {
        if (note.reminderDate == null) {
            et.setText(getString(R.string.no_note_reminder))
            til.endIconDrawable = resources.drawableResource(R.drawable.ic_round_notification_add_24)
            baseDialogFragment.tvDialogTitle.text = resources.stringResource(R.string.new_note_reminder)
        } else {
            til.endIconDrawable = resources.drawableResource(R.drawable.ic_round_cancel_24)
            baseDialogFragment.tvDialogTitle.text = resources.stringResource(R.string.edit_note_reminder)
            et.setText(note.reminderDate.format(requireContext()))
        }
    }
}