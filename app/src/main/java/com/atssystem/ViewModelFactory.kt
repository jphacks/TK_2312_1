package com.atssystem

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.atssystem.database.ClauseDatabase

class ViewModelFactory(
    private val pm: PackageManager,
    owner: SavedStateRegistryOwner,
    private val appItemRepository: AppItemRepository,
    private val clauseRepository: ClauseRepository,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    val appId = defaultArgs?.getString("packageName") ?: ""
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(AppDetailViewModel::class.java) ->
                AppDetailViewModel(pm, handle,appId, appItemRepository, clauseRepository)
            isAssignableFrom(AppListViewModel::class.java) ->
                AppListViewModel(pm)
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}