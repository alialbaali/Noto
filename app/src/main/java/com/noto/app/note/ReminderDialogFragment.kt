package com.noto.app.note

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.ReminderDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.time.format.DateTimeFormatter
import java.util.*

const val PENDING_INTENT_FLAGS = PendingIntent.FLAG_ONE_SHOT

class ReminderDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteViewModel> { parametersOf(args.libraryId, args.noteId) }

    private val args by navArgs<ReminderDialogFragmentArgs>()

    private val alarmManager by lazy { requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ReminderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = stringResource(R.string.new_reminder)
        }
        btnDone.setOnClickListener {
            dismiss()
        }

        viewModel.note
            .onEach {
                val timeZone = TimeZone.currentSystemDefault()

                it.reminderDate
                    ?.toLocalDateTime(timeZone)
                    ?.toJavaLocalDateTime()
                    ?.also { time ->
                        til.endIconDrawable = drawableResource(R.drawable.bell_remove_outline)

                        val currentDateTime = Clock.System
                            .now()
                            .toLocalDateTime(timeZone)
                            .toJavaLocalDateTime()

                        if (time.year > currentDateTime.year) {
                            val format = time.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm a"))
                            et.setText(format)
                        } else {
                            val format = time.format(DateTimeFormatter.ofPattern("EEE, d MMM HH:mm a"))
                            et.setText(format)
                        }
                    }

                if (it.reminderDate == null) {
                    et.setText(getString(R.string.no_reminder))
                    til.endIconDrawable = drawableResource(R.drawable.bell_plus_outline)
                }
            }

        til.setEndIconOnClickListener {
            if (viewModel.note.value.reminderDate == null) {
                showDateTimeDialog()
            } else {
                alarmManager.cancelAlarm(requireContext(), viewModel.note.value.id.toInt())
                viewModel.setNoteReminder(null)
            }
        }
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

        val is24HourFormat = DateFormat.is24HourFormat(requireContext())

        DatePickerDialog(requireContext(), theme, { _, year, month, day ->
            TimePickerDialog(requireContext(), theme, { _, hour, minute ->

                LocalDateTime(year, month, day, hour, minute)
                    .toInstant(TimeZone.currentSystemDefault())
                    .also { viewModel.setNoteReminder(it) }
                    .toEpochMilliseconds()
                    .also {
                        val note = viewModel.note.value
                        val notoColor = viewModel.library.value.color
                        alarmManager.createAlarm(requireContext(), note, notoColor, it)
                    }

            }, startHour, startMinute, is24HourFormat).show()
        }, startYear, startMonth, startDay)
            .apply {
                datePicker.minDate = Clock.System
                    .now()
                    .toEpochMilliseconds()
                    .minus(1000)
            }
            .show()
    }
}