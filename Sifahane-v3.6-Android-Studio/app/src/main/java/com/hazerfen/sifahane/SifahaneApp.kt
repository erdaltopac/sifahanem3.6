package com.hazerfen.sifahane

import android.app.Application
import com.hazerfen.sifahane.alarm.AlarmRescheduler
import com.hazerfen.sifahane.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SifahaneApp : Application() {
    val database by lazy { AppDatabase.get(this) }

    private val applicationScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            runCatching {
                AlarmRescheduler.refreshAll(applicationContext)
            }
        }
    }
}
