package com.atssystem

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import com.atssystem.model.AppItem

class AppListViewModel(
    private val pm: PackageManager
): ViewModel() {

    val appItems: List<AppItem> by lazy {
        loadAllApps()
    }

    private fun loadAllApps(): List<AppItem>{
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val resolvedInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                mainIntent,
                PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            pm.queryIntentActivities(mainIntent, 0)
        }
        val list = mutableListOf<AppItem>()
        for(info in resolvedInfos) {
            list.add(
                AppItem(
                    packageName = info.activityInfo.packageName,
                    warnings = 0,
                    appName = info.loadLabel(pm).toString(),
                    icon = info.loadIcon(pm),
                )
            )
        }
        return list
    }

}