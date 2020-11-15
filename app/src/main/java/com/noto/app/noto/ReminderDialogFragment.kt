package com.noto.app.noto

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.BaseBottomSheetDialogFragment
import com.noto.app.R
import com.noto.app.databinding.FragmentDialogReminderBinding
import com.noto.app.receiver.AlarmReceiver
import com.noto.app.util.drawableResource
import com.noto.app.util.setAlarm
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val PENDING_INTENT_FLAGS = PendingIntent.FLAG_ONE_SHOT

class ReminderDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogReminderBinding

    private val viewModel by sharedViewModel<NotoViewModel>()

    private val alarmManager by lazy { requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDialogReminderBinding.inflate(inflater, container, false)

        binding.btnDone.setOnClickListener {
            dismiss()
        }

        viewModel.noto.observe(viewLifecycleOwner) { noto ->

            noto.reminderDate?.let { time ->

                binding.til.endIconDrawable = drawableResource(R.drawable.bell_remove_outline)

                if (time.year > ZonedDateTime.now().year) {
                    val format = time.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm a"))
                    binding.et.setText(format)
                } else {
                    val format = time.format(DateTimeFormatter.ofPattern("EEE, d MMM HH:mm a"))
                    binding.et.setText(format)
                }
            }

            if (noto.reminderDate == null) {
                binding.et.setText(getString(R.string.no_reminder))
                binding.til.endIconDrawable = drawableResource(R.drawable.bell_plus_outline)
            }

        }


        binding.til.setEndIconOnClickListener {
            if (viewModel.noto.value?.reminderDate == null) showDateTimeDialog() else {
                viewModel.noto.value?.let { noto ->

                    val intent = Intent(requireContext(), AlarmReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(requireContext(), noto.id.toInt(), intent, PENDING_INTENT_FLAGS)

                    alarmManager.cancel(pendingIntent)

                    viewModel.setNotoReminder(null)
                }
            }
        }

        return binding.root
    }

    private fun showDateTimeDialog() {

        fun createAlarm(zonedDateTime: ZonedDateTime) {

            viewModel.noto.value?.let { noto ->

                val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
                    putExtra(NOTO_ID, noto.id.toInt())
                    putExtra(NOTO_TITLE, noto.title)
                    putExtra(NOTO_BODY, noto.body)
                    putExtra(NOTO_COLOR, viewModel.library.value?.notoColor?.ordinal ?: 0)
                    putExtra(NOTO_ICON, viewModel.library.value?.notoIcon?.ordinal ?: 0)
                }

                val pendingIntent = PendingIntent.getBroadcast(requireContext(), noto.id.toInt(), intent, PENDING_INTENT_FLAGS)

                val timeInMills = zonedDateTime.toInstant().toEpochMilli()

                alarmManager.setAlarm(AlarmManager.RTC_WAKEUP, timeInMills, pendingIntent)

                viewModel.setNotoReminder(zonedDateTime)
            }

        }

        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), R.style.PickerDialog, DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            TimePickerDialog(requireContext(), R.style.PickerDialog, TimePickerDialog.OnTimeSetListener { _, hour, minute ->

                val dateTime = LocalDateTime.of(year, month.plus(1), day, hour, minute)
                val zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault())

                createAlarm(zonedDateTime)

            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()


    }

}