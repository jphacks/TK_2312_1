package com.atssystem.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atssystem.AppListViewModel
import com.atssystem.model.AppItem
import com.google.accompanist.drawablepainter.rememberDrawablePainter


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    onAppClick: (String) -> Unit,
    viewModel: AppListViewModel,
) {
    AppList(apps = viewModel.appItems, onAppClick)
}

@Composable
fun AppList(
    apps: List<AppItem>,
    onAppClick: (String) -> Unit
) {
    LazyColumn() {
        items(apps) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = rememberDrawablePainter(drawable = it.icon),
                        contentDescription = "this is a icon",
                        modifier = Modifier.size(32.dp))
                    Text(it.appName,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 16.dp))

                }

                Button(onClick = {onAppClick(it.packageName)}) {
                    Text("${it.warnings} warnings")
                }
            }
            Divider(
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
            )
        }
    }
}

fun getWarningColor(warnings: Int) {
}