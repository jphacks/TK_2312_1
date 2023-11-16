package com.atssystem

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atssystem.database.AppItemDatabase
import com.atssystem.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class AppListViewModel(
    private val pm: PackageManager,
    private val appItemDB: AppItemDatabase
): ViewModel() {

    private val _appItems: MutableStateFlow<List<AppItem>> by lazy {
        viewModelScope.launch {
            loadApps()
        }
        MutableStateFlow(listOf())
    }

    val appItems = _appItems.asStateFlow()

    private suspend fun loadApps(){
        withContext(Dispatchers.IO) {
            val savedEntities = appItemDB.appItemDao().loadAllApps()

            val list = savedEntities.map {
                val info = pm.getApplicationInfo(it.packageName, 0)
                AppItem(
                    packageName = it.packageName,
                    warnings = it.warnings,
                    appName = it.appName,
                    icon = info.loadIcon(pm),
                )
            }
            _appItems.value = list
           }
        }

}