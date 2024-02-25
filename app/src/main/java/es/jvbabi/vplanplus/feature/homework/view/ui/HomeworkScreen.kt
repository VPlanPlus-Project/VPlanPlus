package es.jvbabi.vplanplus.feature.homework.view.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.view.ui.components.HomeworkCard
import es.jvbabi.vplanplus.feature.homework.view.ui.components.WrongProfile
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun HomeworkScreen(
    navHostController: NavHostController,
    navBar: @Composable () -> Unit,
    viewModel: HomeworkViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    HomeworkScreenContent(
        onBack = { navHostController.popBackStack() },
        onAddHomework = { navHostController.navigate(Screen.AddHomeworkScreen.route) },
        onMarkAllDone = viewModel::markAllDone,
        onMarkSingleDone = viewModel::markSingleDone,
        state = state,
        navBar = navBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkScreenContent(
    onBack: () -> Unit = {},
    onAddHomework: () -> Unit = {},
    onMarkAllDone: (homework: Homework, done: Boolean) -> Unit = { _, _ -> },
    onMarkSingleDone: (homeworkTask: HomeworkTask, done: Boolean) -> Unit = { _, _ -> },
    state: HomeworkState,
    navBar: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.homework_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        BackIcon()
                    }
                }
            )
        },
        bottomBar = navBar,
        floatingActionButton = {
            if (!state.wrongProfile) FloatingActionButton(onClick = onAddHomework) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.add))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.wrongProfile) {
                WrongProfile()
                return@Column
            }

            LazyColumn {
                items(state.homework.sortedBy { it.until }) { homework ->
                    HomeworkCard(
                        homework = homework,
                        allDone = { onMarkAllDone(homework, it) },
                        singleDone = { task, done -> onMarkSingleDone(task, done) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeworkScreenPreview() {
    HomeworkScreenContent(
        state = HomeworkState(
            wrongProfile = false,
        )
    )
}