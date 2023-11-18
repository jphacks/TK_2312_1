package com.atssystem

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.atssystem.database.AppItemDatabase
import com.atssystem.database.ClauseDatabase
import com.atssystem.model.Clause

class ViewModelFactory(
    private val pm: PackageManager,
    owner: SavedStateRegistryOwner,
    private val appItemDB: AppItemDatabase,
    private val clauseDB: ClauseDatabase,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    private val appId = defaultArgs?.getString("packageName") ?: ""
    private val isAnalyzed = defaultArgs?.getBoolean("isAnalyzed") ?: false
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(AppDetailViewModel::class.java) ->
                AppDetailViewModel(pm, handle,appId, appItemDB, clauseDB)
            isAssignableFrom(AppListViewModel::class.java) ->
                AppListViewModel(pm, appItemDB)
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}