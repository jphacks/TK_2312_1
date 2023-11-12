package com.atssystem

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppDetailViewModel(
    private val pm: PackageManager,
    savedStateHandle: SavedStateHandle,
    packageName: String
): ViewModel() {

    private val _uiState = MutableStateFlow(
        UiState(
            packageName = packageName,
            isAnalyzed = false
        )
    )

    val uiState = _uiState.asStateFlow()
    fun getIconImage(): Drawable {
        val appInfo = pm.getApplicationInfo(uiState.value.packageName, 0)
        return appInfo.loadIcon(pm)
    }

    fun getAppName() =
        pm.getApplicationInfo(uiState.value.packageName, 0).loadLabel(pm).toString()

    fun setNewApp(packageName: String){
        _uiState.update { UiState(packageName, false) }
    }

    fun startAnalyze(){
        viewModelScope.launch {
        }
    }
}

data class UiState(
    val packageName: String,
    val isAnalyzed: Boolean
)