package com.atssystem

import android.content.Intent
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.atssystem.compose.AtsSystemApp
import com.atssystem.database.AppItemDatabase
import com.atssystem.database.AppItemEntity
import com.atssystem.model.AppItem
import com.atssystem.ui.theme.ATSSystemTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("atssystem", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val isInitialOpen = sharedPref.getBoolean("isFirst", true)
        if (isInitialOpen) {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    loadAllApps(editor)
                }
            }
        }
        setContent {
             AtsSystemApp()
        }
    }

    private suspend fun loadAllApps(editor: Editor) {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pm = this.application.packageManager

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
        val entities: List<AppItemEntity> = list.map { it ->
            AppItemEntity(
                packageName = it.packageName,
                warnings = 0,
                appName = it.appName
            )
        }

        val appItemDB = AppItemDatabase.getInstance(applicationContext)
        appItemDB.appItemDao().saveAll(entities)
        editor.putBoolean("isFirst", false)
        editor.apply()
    }
}

