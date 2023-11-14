package com.atssystem.compose

import android.graphics.drawable.Drawable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atssystem.AppDetailViewModel
import com.atssystem.model.Clause
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun AppDetailScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: AppDetailViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    val drawable = viewModel.getIconImage()

    Column {
        AppDetailHeader(icon = drawable, appName = viewModel.getAppName())
        if(uiState.isAnalyzed) {
            ClauseList(list = uiState.list)
        } else {
            AnalyzeButton {
                viewModel.startAnalyze()
            }
        }

        if(isAnalyzing) {
            CircularProgressIndicator()
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
    var isClicked = false
    Button(onClick = {
        if(!isClicked) {
            isClicked = true
            onStartAnalyze()
        }
    }) {
       Text(text = "プライバシーポリシーを分析する")
    }
}

@Composable
fun ClauseList(list: List<Clause>){
    LazyColumn {
        items(list) {
            ClauseItem(clause = it)
        }
    }
}

@Composable
fun ClauseItem(clause: Clause) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        ClauseContent(clause = clause)
    }
}

@Composable
fun ClauseContent(clause: Clause) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        ) {
            Text(clause.summary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            if(expanded) {
                Text(clause.description, fontSize = 12.sp)
            }
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if(expanded) Icons.Filled.KeyboardArrowUp
                else Icons.Filled.KeyboardArrowDown,
                contentDescription = "This is an icon")
        }
    }
}

@Preview
@Composable
fun previewClauseContent() {
    val clause = Clause(
        clause = "kk",
        summary = "利用規約の変更に同意しない場合、サービスの使用停止や規約の解除が可能",
        description = "利用者が利用規約の変更に同意しない場合、サービスの使用停止や規約の解除が可能とされており、利用者にとって不利な変更が行われた場合でも、それに同意しなければならないという点で危険性がある。また、利用者が規定に違反した場合にも、事前の通知なしにサービスの一部または全部の使用停止や規約の解除が行われる可能性がある。"
    )
    ClauseItem(clause = clause)
}