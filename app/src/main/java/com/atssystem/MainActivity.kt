package com.atssystem

import android.content.Intent
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.atssystem.compose.AtsSystemApp
import com.atssystem.database.AppItemDatabase
import com.atssystem.database.AppItemEntity
import com.atssystem.model.AppItem
import com.atssystem.receiver.PackageSearchWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var startDestination = "home"
        var pushPackageName: String? = null

        val packageName = intent.getStringExtra("packageName")
        Log.d(this::class.java.toString(), packageName + " is from Intent")
        if(packageName != null) {
            pushPackageName = packageName
            startDestination = "list/$packageName"
        }

        val sharedPref = getSharedPreferences("atssystem", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val isInitialOpen = sharedPref.getBoolean("isFirst", true)
        if (isInitialOpen) {
            startDestination = "whenInstalled"
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    loadInitialApps(editor)
                    val searchRequest = PeriodicWorkRequestBuilder<PackageSearchWorker>(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                        .build()
                    WorkManager.getInstance(applicationContext).enqueue(searchRequest)
                }
            }
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                delay(120*1000)
                val searchRequest = PeriodicWorkRequestBuilder<PackageSearchWorker>(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
                    .build()
                WorkManager.getInstance(applicationContext).enqueue(searchRequest)
            }
        }
        setContent {
             AtsSystemApp(
                 startDestination = startDestination,
                 pushPackageName = pushPackageName
             )
        }
    }

    private fun loadInitialApps(editor: Editor) {
        val pm = this.application.packageManager

        val resolvedInfos = pm.getInstalledPackages(0)

        val list = mutableListOf<AppItem>()
        for(info in resolvedInfos) {
            list.add(
                AppItem(
                    packageName = info.packageName,
                    warnings = -1,
                    appName = info.applicationInfo.loadLabel(pm).toString(),
                    icon = info.applicationInfo.loadIcon(pm),
                    time = 0,
                    url = null
                )
            )
        }

        val entities: List<AppItemEntity> = list.map { it ->
            AppItemEntity(
                packageName = it.packageName,
                warnings = 0,
                appName = it.appName,
                isInstalledLately = false,
                time = it.time,
                url = ""
            )
        }

        val appItemDB = AppItemDatabase.getInstance(applicationContext)
        appItemDB.appItemDao().saveAll(entities)
        editor.putBoolean("isFirst", false)
        editor.apply()
    }
}

