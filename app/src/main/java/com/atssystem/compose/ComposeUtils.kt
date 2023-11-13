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
        AppItemRepository(getAppItemDatabase()),
        ClauseRepository(getClauseDatabase()),
        defaultArgs)
}

@Composable
fun getAppItemDatabase(): AppItemDatabase {
    val db = Room.databaseBuilder(
        LocalContext.current.applicationContext,
        AppItemDatabase::class.java, "app_infos"
    ).build()
    return db
}

@Composable
fun getClauseDatabase(): ClauseDatabase {
    val db = Room.databaseBuilder(
        LocalContext.current.applicationContext,
        ClauseDatabase::class.java, "risky_clause"
    ).build()

    return db
}