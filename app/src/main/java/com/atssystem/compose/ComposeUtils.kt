package com.atssystem.compose

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import com.atssystem.ViewModelFactory

@Composable
fun getViewModelFactory(defaultArgs: Bundle? = null): ViewModelFactory {
    val pm = (LocalContext.current.applicationContext).packageManager
    return ViewModelFactory(pm, LocalSavedStateRegistryOwner.current, defaultArgs)
}