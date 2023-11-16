package com.atssystem.compose

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.room.Room
import com.atssystem.AppItemRepository
import com.atssystem.ClauseRepository
import com.atssystem.ViewModelFactory
import com.atssystem.database.AppItemDatabase
import com.atssystem.database.ClauseDatabase

@Composable
fun getViewModelFactory(defaultArgs: Bundle? = null): ViewModelFactory {
    val pm = (LocalContext.current.applicationContext).packageManager
    return ViewModelFactory(
        pm = pm,
        LocalSavedStateRegistryOwner.current,
        getAppItemDatabase(),
        getClauseDatabase(),
        defaultArgs)
}

@Composable
fun getAppItemDatabase(): AppItemDatabase {
    return AppItemDatabase.getInstance(LocalContext.current.applicationContext)
}

@Composable
fun getClauseDatabase(): ClauseDatabase {
    return ClauseDatabase.getInstance(LocalContext.current.applicationContext)
}