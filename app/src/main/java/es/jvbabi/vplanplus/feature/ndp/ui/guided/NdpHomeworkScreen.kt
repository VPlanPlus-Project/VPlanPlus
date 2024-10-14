package es.jvbabi.vplanplus.feature.ndp.ui.guided

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.util.toInt

@Composable
fun NdpHomeworkScreen(
    homework: List<PersonalizedHomework>
) {
    val listState = rememberLazyListState()
    LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }, listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            listState.animateScrollToItem(listState.firstVisibleItemIndex + (listState.firstVisibleItemScrollOffset > listState.layoutInfo.visibleItemsInfo.first { it.index == listState.firstVisibleItemIndex }.size / 2).toInt())
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            items(homework) { homeworkItem ->
                Text(homeworkItem.tasks.joinToString("\n") { it.content })
                repeat(20) {
                    Spacer16Dp()
                }
            }
        }
    }
}