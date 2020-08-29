package com.noto.app

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.noto.R
import com.noto.databinding.ActivityMainBinding
import com.noto.domain.interactor.FetchData
import com.noto.domain.interactor.SyncData
import com.noto.domain.interactor.user.GetUserToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val getUserToken by inject<GetUserToken>()

    private val syncData by inject<SyncData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        lifecycleScope.launch {
            if (getUserToken()) {
                findNavController(R.id.nav_host_fragment).apply {
                    popBackStack()
                    navigate(R.id.libraryListFragment)
                }
            }
        }


        lifecycleScope.launch {
            while (true) {
                delay(20000)
                syncData()
                Timber.i("SYNCED")
            }
        }

        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                window.decorView.systemUiVisibility = 0
            }
        }


        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .setRequiresBatteryNotLow(true)
//            .setRequiresCharging(true)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

//        WorkManager.getInstance(applicationContext)
//            .enqueue(syncWorkRequest)

        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(syncWorkRequest.id).observe(this, Observer { workInfo ->

            println(workInfo.id)
            println(workInfo.tags)
            println(workInfo.state)
            println(workInfo.progress)
            println(workInfo.outputData)
            println(workInfo.runAttemptCount)
        })

    }
}