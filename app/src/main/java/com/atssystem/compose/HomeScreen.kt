package com.atssystem.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    val appItems by viewModel.appItems.collectAsState()

    AppList(apps = appItems, onAppClick)
}

@Composable
fun AppList(
    apps: List<AppItem>,
    onAppClick: (String) -> Unit
) {

    Column() {
        Text(text = "最近インストールしたアプリ", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
        for (i in apps) {
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
                        painter = rememberDrawablePainter(drawable = i.icon),
                        contentDescription = "this is a icon",
                        modifier = Modifier.size(32.dp))
                    Text(i.appName,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 16.dp))

                }

                Button(onClick = {onAppClick(i.packageName)}) {
                    if(i.warnings == -1) {
                        Text(text = "未解析")
                    } else {
                        Text("${i.warnings} warnings")
                    }
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

