package com.atssystem.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.atssystem.R
import java.util.concurrent.TimeUnit

class PhoneBootedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, p1: Intent) {
        val saveRequest = PeriodicWorkRequestBuilder<PackageSearchWorker>(1, TimeUnit.MINUTES)
                .build()
        WorkManager.getInstance(context).enqueue(saveRequest)
    }
}