package es.jvbabi.vplanplus.feature.main_homework.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.BadProfileType
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.DoneStateFilterSheet
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.VisibilityFilterSheet
import es.jvbabi.vplanplus.feature.main_homework.list_old.ui.components.HomeworkCardItem
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.rememberModalBottomSheetStateWithoutFullExpansion
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
fun HomeworkListScreen(
    navHostController: NavHostController,
    viewModel: HomeworkListViewModel = hiltViewModel(),
    navBar: @Composable (visible: Boolean) -> Unit
) {
    val state = viewModel.state
    HomeworkListContent(
        state = state,
        navBar = { navBar(true) },
        onEvent = viewModel::onEvent,
        onOpenHomework = { homework -> navHostController.navigate(Screen.HomeworkDetailScreen.route + "/${homework.id}") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkListContent(
    state: HomeworkListState,
    navBar: @Composable () -> Unit = {},
    onEvent: (event: HomeworkListEvent) -> Unit = {},
    onOpenHomework: (homework: Homework) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showDoneStateFilterSheet by rememberSaveable { mutableStateOf(false) }
    val doneStateFilterSheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    if (showDoneStateFilterSheet) DoneStateFilterSheet(
        sheetState = doneStateFilterSheetState,
        onDismiss = { showDoneStateFilterSheet = false },
        onUpdateState = { onEvent(HomeworkListEvent.UpdateFilter(HomeworkFilter.CompletionFilter(it))) },
        state = (state.filters.first { it is HomeworkFilter.CompletionFilter } as HomeworkFilter.CompletionFilter).showCompleted
    )

    var showVisibilityFilterSheet by rememberSaveable { mutableStateOf(false) }
    val visibilityFilterSheetState = rememberModalBottomSheetStateWithoutFullExpansion()
    if (showVisibilityFilterSheet) VisibilityFilterSheet(
        sheetState = visibilityFilterSheetState,
        onDismiss = { showVisibilityFilterSheet = false },
        onUpdateState = { onEvent(HomeworkListEvent.UpdateFilter(HomeworkFilter.VisibilityFilter(it))) },
        state = (state.filters.first { it is HomeworkFilter.VisibilityFilter } as HomeworkFilter.VisibilityFilter).showVisible
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.homework_title)) })
        },
        floatingActionButton = {
            if (state.userUsesFalseProfileType) return@Scaffold
            ExtendedFloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer4Dp()
                Text(text = stringResource(id = R.string.home_addHomeworkLabel))
            }
        },
        bottomBar = navBar,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) content@{
            if (state.userUsesFalseProfileType) {
                BadProfileType()
                return@content
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                item {}
                item {
                    Icon(imageVector = Icons.Default.FilterAlt, contentDescription = null, modifier = Modifier.size(24.dp))
                }
                state.filters.forEach { filter ->
                    item {
                        AssistChip(
                            onClick = {
                                when (filter) {
                                    is HomeworkFilter.VisibilityFilter -> showVisibilityFilterSheet = true
                                    is HomeworkFilter.CompletionFilter -> showDoneStateFilterSheet = true
                                }
                            },
                            label = { Text(text = filter.buildLabel()) },
                            leadingIcon = filter.buildLeadingIcon(),
                            trailingIcon = filter.buildTrailingIcon()
                        )
                    }
                }
            }

            LazyColumn {
                items(state.homework, key = { it.id }) { homework ->
                    HomeworkCardItem(
                        homework = homework,
                        currentVppId = state.profile?.vppId,
                        isVisible = state.filters.all { it.filter(homework) },
                        onClick = { onOpenHomework(homework) },
                        onCheckSwiped = { onEvent(HomeworkListEvent.MarkAsDone(homework)) },
                        onVisibilityOrDeleteSwiped = { onEvent(HomeworkListEvent.DeleteOrHide(homework)) },
                        resetKey1 = state.homework,
                        resetKey2 = state.error,
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = state.error) {
        when (state.error) {
            HomeworkListError.DeleteOrHideError -> snackbarHostState.showSnackbar(context.getString(R.string.homework_errorDelete))
            HomeworkListError.MarkAsDoneError -> snackbarHostState.showSnackbar(context.getString(R.string.homework_errorMarkingDone))
            null -> Unit
        }
    }
}

@Composable
@Preview
private fun HomeworkListContentPreview() {
    HomeworkListContent(
        HomeworkListState()
    )
}

@Composable
@Preview
private fun BadProfileTypePreview() {
    HomeworkListContent(
        HomeworkListState(userUsesFalseProfileType = true)
    )
}