package com.noto.noto

import android.annotation.SuppressLint
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
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.observe
import com.noto.BaseBottomSheetDialogFragment
import com.noto.R
import com.noto.databinding.FragmentDialogReminderBinding
import com.noto.receiver.AlarmReceiver
import com.noto.util.setAlarm
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ReminderDialogFragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentDialogReminderBinding

    private val viewModel by sharedViewModel<NotoViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentDialogReminderBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@ReminderDialogFragment
        }

        binding.btnDone.setOnClickListener {
            dismiss()
        }

        viewModel.noto.observe(viewLifecycleOwner) { noto ->

            noto.notoReminder?.let { time ->

                binding.til.endIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.bell_remove_outline, null)

                if (time.year > ZonedDateTime.now().year) {
                    val format = time.format(DateTimeFormatter.ofPattern("EEE, d MMM HH:mm a"))
                    binding.et.setText(format)
                } else {
                    val format = time.format(DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm a"))
                    binding.et.setText(format)
                }
            }

            if (noto.notoReminder == null) {
                binding.et.setText(getString(R.string.no_reminder))
                binding.til.endIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.bell_plus_outline, null)
            }

        }
        binding.til.setEndIconOnClickListener {
            if (viewModel.noto.value?.notoReminder == null) showDateTimeDialog() else viewModel.setNotoReminder(null)
        }

        return binding.root
    }

    private fun showDateTimeDialog() {

        fun createAlarm(zonedDateTime: ZonedDateTime) {

            viewModel.noto.value?.let { noto ->

                val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
                    putExtra(NOTO_ID, noto.notoId.toInt())
                    putExtra(NOTO_TITLE, noto.notoTitle)
                    putExtra(NOTO_BODY, noto.notoBody)
                }

                val pendingIntent = PendingIntent.getBroadcast(requireContext(), noto.notoId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)

                val timeInMills = zonedDateTime.toInstant().toEpochMilli()

                (requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager).setAlarm(AlarmManager.RTC_WAKEUP, timeInMills, pendingIntent)

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