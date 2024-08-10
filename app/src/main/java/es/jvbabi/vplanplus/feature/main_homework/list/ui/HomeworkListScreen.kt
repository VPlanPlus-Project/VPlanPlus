package es.jvbabi.vplanplus.feature.main_homework.list.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.skydoves.balloon.compose.Balloon
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.general.HOMEWORK_HIDDEN_WHERE_TO_FIND_BALLOON
import es.jvbabi.vplanplus.domain.usecase.general.HOMEWORK_SWIPE_DEMO_BALLOON
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheet
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheetInitialValues
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.AllDone
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.BadProfileType
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.DateHeader
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.DoneStateFilterSheet
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.HomeworkCardItem
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.HomeworkDisabled
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.NoMatchingItems
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.VisibilityFilterSheet
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.DefaultBalloonDescription
import es.jvbabi.vplanplus.ui.common.DefaultBalloonTitle
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.rememberDefaultBalloon
import es.jvbabi.vplanplus.ui.common.rememberModalBottomSheetStateWithoutFullExpansion
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.runComposable
import java.time.LocalDate

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
        onOpenHomework = { homework -> navHostController.navigate(Screen.HomeworkDetailScreen.route + "/${homework.id}") },
        onOpenInHome = { date -> navHostController.navigate(Screen.HomeScreen.route + "/$date") }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HomeworkListContent(
    state: HomeworkListState,
    navBar: @Composable () -> Unit = {},
    onEvent: (event: HomeworkListEvent) -> Unit = {},
    onOpenHomework: (homework: HomeworkCore) -> Unit = {},
    onOpenInHome: (date: LocalDate) -> Unit = {}
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

    var addHomeworkSheetInitialValues by rememberSaveable<MutableState<AddHomeworkSheetInitialValues?>> { mutableStateOf(null) }
    if (addHomeworkSheetInitialValues != null) {
        AddHomeworkSheet(onClose = { addHomeworkSheetInitialValues = null }, initialValues = addHomeworkSheetInitialValues ?: AddHomeworkSheetInitialValues())
    }

    LaunchedEffect(key1 = state.lastHiddenHomework) {
        if (state.lastHiddenHomework == null) return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = context.getString(R.string.homework_hiddenSnackbarMessage),
            withDismissAction = true,
            actionLabel = context.getString(R.string.homework_hiddenSnackbarAction),
            duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) onEvent(HomeworkListEvent.DeleteOrHide(state.lastHiddenHomework))
        onEvent(HomeworkListEvent.ResetLastHiddenHomework)
    }

    val pullRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.homework_title)) })
        },
        floatingActionButton = {
            if (state.userUsesFalseProfileType) return@Scaffold
            AnimatedVisibility(
                visible = state.profile?.isHomeworkEnabled == true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ExtendedFloatingActionButton(onClick = { addHomeworkSheetInitialValues = AddHomeworkSheetInitialValues() }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer4Dp()
                    Text(text = stringResource(id = R.string.homework_addHomework))
                }
            }
        },
        bottomBar = navBar,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) contentWrapper@{
            Column(Modifier.fillMaxSize()) content@{
                if (state.userUsesFalseProfileType) {
                    BadProfileType()
                    return@content
                }
                if (state.profile?.isHomeworkEnabled == false) {
                    HomeworkDisabled { onEvent(HomeworkListEvent.EnableHomework) }
                    return@content
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) filters@{
                    item {}
                    item {
                        Icon(imageVector = Icons.Default.FilterAlt, contentDescription = null, modifier = Modifier.size(24.dp))
                    }
                    item {
                        val filter = state.getFilter<HomeworkFilter.VisibilityFilter>()
                        Balloon(
                            builder = rememberDefaultBalloon(),
                            balloonContent = {
                                Column {
                                    DefaultBalloonTitle(stringResource(id = R.string.homework_hiddenBalloonTitle))
                                    DefaultBalloonDescription(stringResource(id = R.string.homework_hiddenBalloonDescription))
                                }
                            }
                        ) { balloon ->
                            LaunchedEffect(key1 = state.lastHiddenHomework) {
                                if (state.lastHiddenHomework != null && state.allowHomeworkHiddenBanner) balloon.showAlignBottom()
                            }
                            balloon.setOnBalloonDismissListener { onEvent(HomeworkListEvent.DismissBalloon(HOMEWORK_HIDDEN_WHERE_TO_FIND_BALLOON)) }
                            AssistChip(
                                onClick = { showVisibilityFilterSheet = true },
                                label = { Text(text = filter.buildLabel()) },
                                leadingIcon = filter.buildLeadingIcon(),
                                trailingIcon = filter.buildTrailingIcon()
                            )
                        }
                    }
                    item {
                        val filter = state.getFilter<HomeworkFilter.CompletionFilter>()
                        AssistChip(
                            onClick = { showDoneStateFilterSheet = true },
                            label = { Text(text = filter.buildLabel()) },
                            leadingIcon = filter.buildLeadingIcon(),
                            trailingIcon = filter.buildTrailingIcon()
                        )
                    }
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = state.initDone,
                    enter = slideIn(initialOffset = { IntOffset(0, 100) }) + fadeIn()
                ) {
                    val items = state.personalizedHomeworks.groupBy { it.homework.until }.toList()
                    val homeworkListState = rememberLazyListState()

                    var hasDrawnFirstVisibleHomework = false
                    LazyColumn(state = homeworkListState) {
                        item placeholderWrapper@{
                            runComposable placeholders@{
                                val isAllNotHiddenHomeworkDone = state.personalizedHomeworks.all { it.allDone() || (it is PersonalizedHomework.CloudHomework && it.isHidden) }
                                val showOnlyUnfinishedHomework = state.getFilter<HomeworkFilter.CompletionFilter>().showCompleted == false
                                val showOnlyVisibleHomework = state.getFilter<HomeworkFilter.VisibilityFilter>().showVisible == true
                                val showAllDonePlaceholder = isAllNotHiddenHomeworkDone && showOnlyUnfinishedHomework && showOnlyVisibleHomework
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = showAllDonePlaceholder,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically(),
                                    modifier = Modifier.fillParentMaxSize()
                                ) {
                                    AllDone()
                                }

                                val doesNoHomeworkMatchingToFiltersExists = state.personalizedHomeworks.none { homework -> state.filters.all { it.filter(homework) } }
                                if (doesNoHomeworkMatchingToFiltersExists && !showAllDonePlaceholder) {
                                    Box(Modifier.fillParentMaxSize()) { NoMatchingItems { onEvent(HomeworkListEvent.ResetFilters) } }
                                }
                            }
                        }
                        items.forEach { (until, homeworkForDay) ->
                            stickyHeader(key = until) {
                                AnimatedVisibility(visible = homeworkForDay.any { homework -> state.filters.all { filter -> filter.filter(homework) } }) {
                                    DateHeader(
                                        date = until.toLocalDate(),
                                        onAddHomework = { addHomeworkSheetInitialValues = AddHomeworkSheetInitialValues(until = until.toLocalDate()) },
                                        onOpenInHome = { onOpenInHome(until.toLocalDate()) }
                                    )
                                }
                            }
                            items(homeworkForDay) { hw ->
                                val homeworkProfile by rememberUpdatedState(hw)
                                val isVisible = state.filters.all { it.filter(homeworkProfile) }
                                val canShowDemo = isVisible && !hasDrawnFirstVisibleHomework && state.allowSwipingDemo
                                HomeworkCardItem(
                                    personalizedHomework = homeworkProfile,
                                    isVisible = isVisible,
                                    onClick = { onOpenHomework(homeworkProfile.homework) },
                                    onCheckSwiped = {
                                        Log.d("HomeworkListContent", "Toggling done state of homework ${homeworkProfile.homework.id} ${homeworkProfile.tasks.any { !it.isDone }}")
                                        onEvent(HomeworkListEvent.ToggleHomeworkDone(homeworkProfile))
                                                    },
                                    onVisibilityOrDeleteSwiped = { onEvent(HomeworkListEvent.DeleteOrHide(homeworkProfile)) },
                                    resetKey1 = state.updateCounter,
                                    resetKey2 = state.error,
                                    showDemo = canShowDemo,
                                    onDemoEnd = { onEvent(HomeworkListEvent.DismissBalloon(HOMEWORK_SWIPE_DEMO_BALLOON)) },
                                    allowProgressBar = state.getFilter<HomeworkFilter.CompletionFilter>().showCompleted == null
                                )
                                if (canShowDemo) hasDrawnFirstVisibleHomework = true
                            }
                            item { Spacer8Dp() }
                        }
                    }
                }

            }
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter),
            )
        }

        if (pullRefreshState.isRefreshing) {
            LaunchedEffect(key1 = Unit) {
                pullRefreshState.startRefresh()
                onEvent(HomeworkListEvent.RefreshHomework)
            }
        }
        LaunchedEffect(state.isUpdatingHomework) {
            if (state.isUpdatingHomework) pullRefreshState.startRefresh()
            else pullRefreshState.endRefresh()
        }
    }

    LaunchedEffect(key1 = state.error) {
        when (state.error) {
            HomeworkListError.DeleteOrHideError -> snackbarHostState.showSnackbar(context.getString(R.string.homework_errorDelete))
            HomeworkListError.MarkAsDoneError -> snackbarHostState.showSnackbar(context.getString(R.string.homework_errorMarkingDone))
            else -> Unit
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