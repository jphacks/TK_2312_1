package com.atssystem

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atssystem.http.AnalyzeToPRepository
import com.atssystem.http.Result
import com.atssystem.http.RiskyClausesResponse
import com.atssystem.model.Clause
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppDetailViewModel(
    private val pm: PackageManager,
    savedStateHandle: SavedStateHandle,
    val packageName: String,
    private val appItemRepository: AppItemRepository,
    private val clauseRepository: ClauseRepository
): ViewModel() {
    val analyzeRepository = AnalyzeToPRepository()


    private val _uiState = MutableStateFlow(
        UiState(
            packageName = packageName,
            isAnalyzed = false,
            list = listOf()
        )
    )

    val uiState = _uiState.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    fun getIconImage(): Drawable {
        val appInfo = pm.getApplicationInfo(uiState.value.packageName, 0)
        return appInfo.loadIcon(pm)
    }

    fun getAppName() =
        pm.getApplicationInfo(uiState.value.packageName, 0).loadLabel(pm).toString()

    fun startAnalyze(){
        _isAnalyzing.value = true
        viewModelScope.launch {
            val result = analyzeRepository.analyzeToP(uiState.value.packageName)
            when(result) {
                is Result.Success<RiskyClausesResponse> -> {
                    _isAnalyzing.value = false
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

    fun saveRiskyClauses() {

    }
}

data class UiState(
    val packageName: String,
    val isAnalyzed: Boolean,
    val list: List<Clause>
)