package com.atssystem.receiver

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.atssystem.MainActivity
import com.atssystem.R
import com.atssystem.database.AppItemDatabase
import com.atssystem.database.AppItemEntity
import com.atssystem.database.ClauseDatabase
import com.atssystem.database.ClauseEntity
import com.atssystem.getCurrentUnixTime
import com.atssystem.http.AnalyzeToPRepository
import com.atssystem.http.Result
import com.atssystem.http.RiskyClausesResponse
import com.atssystem.model.AppItem
import com.atssystem.model.Clause
import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID

class PackageSearchWorker(private val appContext: Context, workerParameters: WorkerParameters)
    :Worker(appContext, workerParameters){

    val CHANNEL_ID = "ats"

    private val pm = appContext.packageManager
    private val appItemDatabase = AppItemDatabase.getInstance(appContext)
    private val clauseItemDatabase = ClauseDatabase.getInstance(appContext)

    override fun doWork(): Result {
        Log.d("PackageSearchWorker", "background task is called")
        createNotificationChannel(appContext)
        val repository = AnalyzeToPRepository()
        val newApps = loadLatelyInstalledPackages()
        for (item in newApps) {
            Log.d("PackageSearchWorker",item.packageName+" is lately installed.")
            runBlocking {
                val result = repository.analyzeToP(item.packageName)
                if(result is com.atssystem.http.Result.Success<RiskyClausesResponse>) {
                    val clauseList = result.data.clauseList.map {
                        ClauseEntity(
                            uuid = UUID.randomUUID().toString(),
                            packageName = item.packageName,
                            clause = it
                        )
                    }
                    if (clauseList.size == 0) return@runBlocking
                    clauseItemDatabase.clauseDao().saveNewClauses(clauseList)
                    val appitem = AppItemEntity(
                        packageName = item.packageName,
                        warnings = clauseList.size,
                        appName = item.appName,
                        isInstalledLately = true,
                        time = item.time)
                    appItemDatabase.appItemDao().saveAppItem(appitem)

                    pushNotification(appContext,item.packageName, appitem.appName)
                }
            }
        }

        return Result.success()
    }

    private fun pushNotification(context: Context, packageName: String, appName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("packageName", packageName)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setContentTitle(appName+"のプライバシーポリシーが分析されました。ここからご確認ください。")

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Log.d(this::class.java.toString(), packageName)
        Log.d(this::class.java.toString(), appName)
        NotificationManagerCompat.from(context)
            .notify(packageName.hashCode(), builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        val name = "アプリのプライバシーポリシー解析のお知らせ"
        val descriptionText = "This is ATS System Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun loadLatelyInstalledPackages(): List<AppItem> {
        val resolvedInfos = pm.getInstalledPackages(0)

        val list = mutableListOf<AppItem>()
        for (info in resolvedInfos) {
            list.add(
                AppItem(
                    packageName = info.packageName,
                    warnings = -1,
                    appName = info.applicationInfo.loadLabel(pm).toString(),
                    icon = null,
                    time = getCurrentUnixTime()
                )
            )
        }

        val savedPackageList: List<String> = appItemDatabase.appItemDao().loadAllApps().map {
            it.packageName
        }

        val newApps =  list.filter { it.packageName !in  savedPackageList }
        appItemDatabase.appItemDao().saveAll(newApps.map {
            AppItemEntity(
                packageName = it.packageName,
                warnings = -1,
                appName = it.appName,
                time = getCurrentUnixTime(),
                isInstalledLately = true
            )
        })

        return newApps
    }
}