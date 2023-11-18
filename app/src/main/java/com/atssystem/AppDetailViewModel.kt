package com.atssystem

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atssystem.database.AppItemDatabase
import com.atssystem.database.AppItemEntity
import com.atssystem.database.ClauseDatabase
import com.atssystem.database.ClauseEntity
import com.atssystem.http.AnalyzeToPRepository
import com.atssystem.http.Result
import com.atssystem.http.RiskyClausesResponse
import com.atssystem.model.Clause
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class AppDetailViewModel(
    private val pm: PackageManager,
    savedStateHandle: SavedStateHandle,
    private val packageName: String,
    private val appItemDB: AppItemDatabase,
    private val clauseDB: ClauseDatabase
) : ViewModel() {
    val analyzeRepository = AnalyzeToPRepository()

    private val _uiState by lazy {
        loadAppItemEntity()
        MutableStateFlow(
            UiState(
                packageName = packageName,
                isAnalyzed = false,
                list = listOf()
            )
        )
    }

    val uiState = _uiState.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    var appItemPreEntity: AppItemEntity? = null

    fun getIconImage(): Drawable {
        val appInfo = pm.getApplicationInfo(uiState.value.packageName, 0)
        return appInfo.loadIcon(pm)
    }

    fun getAppName() =
        pm.getApplicationInfo(uiState.value.packageName, 0).loadLabel(pm).toString()

    fun startAnalyze() {
        _isAnalyzing.value = true
        Log.d(this::class.java.toString(), "startAnalyze")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = analyzeRepository.analyzeToP(uiState.value.packageName)
                when (result) {
                    is Result.Success<RiskyClausesResponse> -> {
                        _isAnalyzing.value = false
                        clauseDB.clauseDao().DeletePreClauses(packageName)
                        clauseDB.clauseDao().saveNewClauses(result.data.clauseList.map {
                            ClauseEntity(
                                uuid = UUID.randomUUID().toString(),
                                packageName = packageName,
                                clause = it
                            )
                        })
                        appItemDB.appItemDao().saveAppItem(
                            AppItemEntity(
                                packageName = packageName,
                                warnings = result.data.clauseList.size,
                                appName = appItemPreEntity!!.appName,
                                isInstalledLately = true,
                                time = appItemPreEntity!!.time
                            )
                        )
                        _uiState.value = UiState(
                            packageName = packageName,
                            isAnalyzed = true,
                            list = result.data.clauseList
                        )
                    }

                    else -> {
                        //TODO
                    }
                }
            }

        }
    }

    private fun loadAppItemEntity() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val appitemEntity = appItemDB.appItemDao().loadApp(packageName)
                appItemPreEntity = appitemEntity
                if (appitemEntity.warnings == -1) {
                    //まだ分析がなされていない時
                    _isLoading.value = false
                    _uiState.value = UiState(
                        packageName = packageName,
                        isAnalyzed = false,
                        list = listOf()
                    )
                } else {
                    // もう分析が終わっている時
                    val clauseList = clauseDB.clauseDao().getClauses(packageName = packageName)
                    _isLoading.value = false
                    _uiState.value = UiState(
                        packageName = packageName,
                        isAnalyzed = true,
                        list = clauseList.map {
                            Clause(
                                summary = it.clause.summary,
                                clause = it.clause.clause,
                                description = it.clause.description
                            )
                        }
                    )
                }
            }
        }
    }

}

data class UiState(
    val packageName: String,
    val isAnalyzed: Boolean,
    val list: List<Clause>
)