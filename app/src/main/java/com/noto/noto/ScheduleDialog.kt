package com.noto.noto



//class ScheduleDialog(context: Context) : AlertDialog(context) {
//
////    private val binding = DialogDatePickerBinding.inflate(layoutInflater)
//
//    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//    init {
//        create()
//        show()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(binding.root)
//        var requestCode = 1
//
//        val intent = Intent(context, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT)
//
//        binding.tp.setOnTimeChangedListener { view, hourOfDay, minute ->
//
////            AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
////            requestCode++
//            dismiss()
////            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, c.timeInMillis, pendingIntent)
//        }
//
//    }

//}