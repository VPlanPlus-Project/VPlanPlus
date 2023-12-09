package es.jvbabi.vplanplus.ui.screens.logs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateTimeConverter
import es.jvbabi.vplanplus.domain.model.LogRecord
import java.time.format.DateTimeFormatter

@Composable
fun LogsScreen(
    navHostController: NavHostController,
    viewModel: LogsViewModel = hiltViewModel()
) {

    val state = viewModel.state.value
    LogsScreenContent(
        state = state,
        onBackClicked = { navHostController.popBackStack() },
        onDeleteLogsClicked = { viewModel.onDeleteLogsClicked() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreenContent(
    onBackClicked: () -> Unit,
    onDeleteLogsClicked: () -> Unit,
    state: LogsState
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Logs") },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onDeleteLogsClicked() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            items(state.logs) { log ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = LocalDateTimeConverter().timestampToLocalDateTime(log.timestamp/1000).format(
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth(0.2f)
                            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 8.dp)
                    )
                    Text(
                        text = log.tag,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .fillMaxWidth(0.2f)
                            .padding(top = 8.dp, bottom = 8.dp, end = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = log.message,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                            .fillMaxSize(),
                    )
                }
            }
        }
    }

}

@Composable
@Preview
fun LogsScreenPreview() {
    LogsScreenContent(
        onBackClicked = {},
        onDeleteLogsClicked = {},
        state = LogsState(
            listOf(
                LogRecord(
                    timestamp = 0,
                    tag = "tag",
                    message = "message ".repeat(20)
                ),
                LogRecord(
                    timestamp = 0,
                    tag = "tag",
                    message = "message ".repeat(10)
                ),
                LogRecord(
                    timestamp = 0,
                    tag = "tag",
                    message = "message ".repeat(30)
                ),
                LogRecord(
                    timestamp = 0,
                    tag = "tag",
                    message = "message ".repeat(5)
                )
            )
        )
    )
}