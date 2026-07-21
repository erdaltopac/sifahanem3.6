package com.hazerfen.sifahane.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        Thread {
            try {
                runBlocking {
                    AlarmRescheduler.refreshAll(context.applicationContext)
                }
            } finally {
                pendingResult.finish()
            }
        }.start()
    }
}
