package com.atssystem.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.atssystem.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atssystem.model.AppItem


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    onAppClick: (String) -> Unit,
    viewModel: HomeViewModel,
) {
    AppList(apps = viewModel.appItems)
}

@Composable
fun AppList(apps: List<AppItem>) {
    LazyColumn() {
        items(apps) {
            Row {
                Text(it.appName)
            }
        }
    }
}