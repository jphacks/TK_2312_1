package com.atssystem

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atssystem.database.AppItemDatabase
import com.atssystem.database.AppItemEntity
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
        //ここでも差分抽出をしている。
        withContext(Dispatchers.IO) {
            val savedEntities = appItemDB.appItemDao().loadAllApps()

            val savedList: List<String> = savedEntities.map {
                it.packageName
            }

            val resolvedInfos = pm.getInstalledPackages(0)

            val presentList = mutableListOf<AppItem>()
            for(info in resolvedInfos) {
                presentList.add(
                    AppItem(
                        packageName = info.packageName,
                        warnings = -1,
                        appName = info.applicationInfo.loadLabel(pm).toString(),
                        icon = null,
                        time = 0,
                        url = null
                    )
                )
            }

            val diffList = presentList.filter {
                it.packageName !in savedList
            }
            //これらが新しく追加されたもの

            for(i in diffList) {
                Log.d("AppListViewModel", i.packageName + " is lately installed.")
            }

            appItemDB.appItemDao().saveAll(
                diffList.map {
                    AppItemEntity(
                        packageName = it.packageName,
                        warnings = -1,
                        appName = it.appName,
                        isInstalledLately = true,
                        time = getCurrentUnixTime(),
                        url = ""
                    ) }
            )

            _appItems.value = appItemDB.appItemDao().loadLatelyInstalledApps().map {
                AppItem(
                    packageName = it.packageName,
                    warnings = it.warnings,
                    appName = it.appName,
                    icon = pm.getApplicationIcon(pm.getApplicationInfo(it.packageName, 0)),
                    time = it.time,
                    url = null
                )
            }.sortedBy{it.time}.asReversed()
            //新しい順に並べる
           }
        }

}