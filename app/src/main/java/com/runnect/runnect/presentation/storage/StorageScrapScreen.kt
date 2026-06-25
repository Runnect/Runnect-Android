package com.runnect.runnect.presentation.storage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.runnect.runnect.R
import com.runnect.runnect.domain.entity.MyScrapCourse
import com.runnect.runnect.presentation.ui.theme.G1
import com.runnect.runnect.presentation.ui.theme.G2
import com.runnect.runnect.presentation.ui.theme.G4
import com.runnect.runnect.presentation.ui.theme.M1
import com.runnect.runnect.presentation.ui.theme.RunnectTheme
import com.runnect.runnect.presentation.ui.theme.White

data class StorageScrapUiState(
    val courses: List<MyScrapCourse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScrapScreen(
    state: StorageScrapUiState,
    onRefresh: () -> Unit,
    onScrapItemClick: (MyScrapCourse) -> Unit,
    onHeartClick: (MyScrapCourse) -> Unit,
    onGoToScrapClick: () -> Unit,
    onErrorShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onErrorShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!state.isLoading && state.courses.isEmpty()) {
                EmptyScrapView(onGoToScrapClick = onGoToScrapClick)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ScrapCountHeader(count = state.courses.size)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.courses, key = { it.id }) { course ->
                            ScrapCourseItem(
                                course = course,
                                onClick = { onScrapItemClick(course) },
                                onHeartClick = { onHeartClick(course) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScrapCountHeader(count: Int) {
    val textStyle = RunnectTheme.textStyle
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(horizontal = 16.dp, vertical = 7.dp)
    ) {
        Text(
            text = stringResource(R.string.storage_total_course_count, count),
            style = textStyle.regular12,
            color = G2
        )
    }
}

@Composable
private fun ScrapCourseItem(
    course: MyScrapCourse,
    onClick: () -> Unit,
    onHeartClick: () -> Unit
) {
    val textStyle = RunnectTheme.textStyle
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        AsyncImage(
            model = course.image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(162f / 114f)
                .clip(RoundedCornerShape(5.dp))
                .background(G4)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = course.title,
                style = textStyle.medium14,
                color = G1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(4.dp))
            Box(
                modifier = Modifier
                    .size(21.dp, 18.dp)
                    .clickable(onClick = onHeartClick)
            ) {
                Image(
                    painter = painterResource(R.drawable.discover_course_scrap_on),
                    contentDescription = null
                )
            }
        }
        Text(
            text = "${course.city} ${course.region}",
            style = textStyle.regular12,
            color = G2
        )
    }
}

@Composable
private fun EmptyScrapView(onGoToScrapClick: () -> Unit) {
    val textStyle = RunnectTheme.textStyle
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 64.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.no_course),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.storage_scrap_empty_guide),
            style = textStyle.medium14,
            color = G2,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(22.dp))
        Button(
            onClick = onGoToScrapClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = M1,
                contentColor = White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = stringResource(R.string.storage_scrap_make_scrap),
                style = textStyle.semiBold15
            )
        }
    }
}
