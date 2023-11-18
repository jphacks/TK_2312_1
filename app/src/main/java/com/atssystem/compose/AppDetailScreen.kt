package com.atssystem.compose

import android.graphics.drawable.Drawable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.atssystem.AppDetailViewModel
import com.atssystem.R
import com.atssystem.R.drawable
import com.atssystem.model.Clause
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun AppDetailScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: AppDetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val drawable = viewModel.getIconImage()

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        AppDetailHeader(icon = drawable, appName = viewModel.getAppName(), onBack = onBack)
        Column {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                if (uiState.isAnalyzed && !isAnalyzing) {
                    NotAnalyzed {
                        viewModel.startAnalyze()
                    }
                    Analyzed(list = uiState.list)
                } else if(!isAnalyzing) {
                    NotAnalyzed {
                        viewModel.startAnalyze()
                    }
                }
            }
        }

        if (isAnalyzing) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

    }
}

@Composable
fun AppDetailHeader(icon: Drawable?, appName: String, onBack: () -> Unit) {
    Column {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "This is a back button",
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = rememberDrawablePainter(drawable = icon),
                contentDescription = "This is an icon.",
                modifier = Modifier
                    .size(80.dp)
                    .padding(start = 8.dp)
            )

            Box(
                contentAlignment = Alignment.BottomStart, modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
                    .height(72.dp)
            ) {
                Text(
                    text = appName,
                    fontSize = 36.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Start,
                )
            }
        }


    }
}

@Composable
fun NotAnalyzed(onStartAnalyze: () -> Unit) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "このアプリのプライバシーポリシーはまだ解析されていません。",
                color = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnalyzeButton {
            onStartAnalyze()
        }
    }
}

@Composable
fun Analyzed(list: List<Clause>) {
    ClauseList(list = list)
}

@Composable
fun AnalyzeButton(onStartAnalyze: () -> Unit) {
    var isClicked by remember{mutableStateOf(false)}
    Button(
        onClick = {onStartAnalyze()}
    ) {
        Row {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "This is an icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "プライバシーポリシーを解析する")
        }

    }
}

@Composable
fun ClauseList(list: List<Clause>) {
    Column {
        for (i in list) {
            ClauseItem(clause = i)
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
            if (expanded) {
                Text(clause.description, fontSize = 12.sp)
            }
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                else Icons.Filled.KeyboardArrowDown,
                contentDescription = "This is an icon"
            )
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

@Preview
@Composable
fun previewIsLoading() {
    Column {
        AppDetailHeader(icon = null, appName = "タイミー", onBack = {})
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Preview
@Composable
fun previewNotAnalyzed() {
    Column {
        AppDetailHeader(icon = null, appName = "タイミー", onBack = {})
        NotAnalyzed {

        }
    }
}