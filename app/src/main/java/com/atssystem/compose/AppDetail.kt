package com.atssystem.compose

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atssystem.AppDetailViewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun AppDetailScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: AppDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawable = viewModel.getIconImage()

    Column {
        AppDetailHeader(icon = drawable, appName = viewModel.getAppName())
        if(uiState.isAnalyzed) {

        } else {
            AnalyzeButton {
                viewModel.startAnalyze()
            }
        }
    }
}

@Composable
fun AppDetailHeader(icon: Drawable, appName: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = rememberDrawablePainter(drawable = icon),
            contentDescription ="This is an icon." ,
            modifier = Modifier.size(64.dp))

        Text(
            text = appName,
            fontSize = 24.sp
        )
    }
}

@Composable
fun AnalyzeButton(onStartAnalyze:() -> Unit){
    Button(onClick = {onStartAnalyze()}) {
       Text(text = "プライバシーポリシーを分析する")
    }
}

@Composable
fun DangerousClauseList(){

}