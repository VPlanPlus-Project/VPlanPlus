package es.jvbabi.vplanplus.ui.screens.home

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.ui.common.PaddingRow
import es.jvbabi.vplanplus.ui.common.SmallText
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.home.components.DateIndicator
import es.jvbabi.vplanplus.ui.screens.home.components.LessonCard
import es.jvbabi.vplanplus.ui.screens.home.components.SearchBar
import es.jvbabi.vplanplus.ui.screens.home.components.ViewSwitcher
import es.jvbabi.vplanplus.ui.screens.home.components.placeholders.Holiday
import es.jvbabi.vplanplus.ui.screens.home.components.placeholders.NoData
import es.jvbabi.vplanplus.ui.screens.home.components.placeholders.WeekendPlaceholder
import es.jvbabi.vplanplus.ui.screens.home.components.placeholders.WeekendType
import es.jvbabi.vplanplus.ui.screens.home.search.SearchContent
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.FilterType
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.HomeState
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.HomeViewModel
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.ViewType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val coroutineScope = rememberCoroutineScope()
    var menuOpened by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val lessonPagerState = rememberPagerState(initialPage = Int.MAX_VALUE/2+Period.between(LocalDate.now(), state.initDate).days, pageCount = { Int.MAX_VALUE })

    LaunchedEffect(key1 = lessonPagerState, block = {
        snapshotFlow { lessonPagerState.currentPage }.collect {
            val date = state.date.plusDays(it - Int.MAX_VALUE / 2L)
            viewModel.onPageChanged(date)
        }
    })

    HomeScreenContent(
        state = state,
        onMenuOpened = {
            menuOpened = true
        }, onViewModeChanged = {
            viewModel.setViewType(it)
            coroutineScope.launch {
                delay(450)
                lessonPagerState.animateScrollToPage(Int.MAX_VALUE / 2)
            }
        },
        lessonPagerState = lessonPagerState,
        onSearchOpened = {
            if (it) viewModel.onSearchOpened()
            else viewModel.onSearchClosed()
        },
        onSearchQueryChanged = {
            viewModel.onSearchQueryUpdate(it)
        },
        onFilterToggle = {
            viewModel.searchToggleFilter(it)
        },
        onFindAvailableRoomClicked = {
            navHostController.navigate(Screen.SearchAvailableRoomScreen.route)
        }
    )


    BackHandler(enabled = menuOpened, onBack = {
        if (menuOpened) {
            menuOpened = false
        }
    })

    AnimatedVisibility(
        visible = menuOpened,
        enter = fadeIn(animationSpec = TweenSpec(200)),
        exit = fadeOut(animationSpec = TweenSpec(200))
    ) {
        Menu(
            profiles = state.profiles.map { it.toMenuProfile() },
            selectedProfile = state.activeProfile!!.toMenuProfile(),
            onProfileClicked = {
                menuOpened = false
                viewModel.onProfileSelected(it)
            },
            onCloseClicked = {
                menuOpened = false
            },
            onRefreshClicked = {
                viewModel.getVPlanData(context)
                menuOpened = false
            },
            onDeletePlansClicked = {
                coroutineScope.launch {
                    viewModel.deletePlans()
                    menuOpened = false
                }
            },
            onRepositoryClicked = {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Julius-Babies/VPlanPlus/")
                )
                startActivity(context, browserIntent, null)
            },
            onSettingsClicked = {
                navHostController.navigate(Screen.SettingsScreen.route)
            },
            onManageProfilesClicked = {
                navHostController.navigate(Screen.SettingsProfileScreen.route)
            },
            onLogsClicked = {
                navHostController.navigate(Screen.LogsScreen.route)
            },
            onProfileLongClicked = {
                navHostController.navigate(Screen.SettingsProfileScreen.route + it)
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    state: HomeState,
    onSearchOpened: (Boolean) -> Unit = {},
    onSearchQueryChanged: (String) -> Unit = {},
    onFilterToggle: (FilterType) -> Unit = {},
    onMenuOpened: () -> Unit = {},
    onViewModeChanged: (type: ViewType) -> Unit = {},
    lessonPagerState: PagerState = rememberPagerState(
        initialPage = LocalDate.now().dayOfWeek.value,
        pageCount = { 5 }),
    onFindAvailableRoomClicked: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            currentProfileName = state.getActiveProfileDisplayName(),
            onMenuOpened = onMenuOpened,
            onSearchActiveChange = { onSearchOpened(it) },
            searchOpen = state.searchOpen,
            searchValue = state.searchQuery,
            onSearchTyping = { onSearchQueryChanged(it) },
            isSyncing = state.syncing,
        ) {
            if (state.searchOpen) {
                SearchContent(
                    state = state,
                    onFindAvailableRoomClicked = { onFindAvailableRoomClicked() },
                    onFilterToggle = { onFilterToggle(it) },
                    time = state.time
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PaddingRow {
                    if (state.lastSync == null) SmallText(text = stringResource(id = R.string.home_lastSyncNever))
                    else SmallText(text = stringResource(id = R.string.home_lastSync, state.lastSync.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))))
                }
                ViewSwitcher(viewType = state.viewMode, onViewModeChanged = onViewModeChanged)
            }
            Column {
                val width by animateFloatAsState(
                    targetValue = if (state.viewMode == ViewType.DAY) LocalConfiguration.current.screenWidthDp.toFloat() else LocalConfiguration.current.screenWidthDp / 5f,
                    label = "Plan View Changed Animation"
                )
                HorizontalPager(
                    state = lessonPagerState,
                    pageSize = PageSize.Fixed(width.dp),
                    verticalAlignment = Alignment.Top,
                ) { index ->
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(top = 8.dp)
                    ) {
                        val date = LocalDate.now().plusDays(index - Int.MAX_VALUE / 2L)
                        if (state.lessons[date]?.state == DayDataState.NO_DATA || state.lessons[date]?.state == null) NoData(state.viewMode == ViewType.WEEK)
                        else when (state.lessons[date]!!.type) {
                            DayType.WEEKEND -> WeekendPlaceholder(
                                type = if (date == LocalDate.now()) WeekendType.TODAY else if (date.isBefore(
                                        LocalDate.now()
                                    )
                                ) WeekendType.OVER else WeekendType.COMING_UP,
                                compactMode = state.viewMode == ViewType.WEEK
                            )
                            DayType.HOLIDAY -> Holiday(state.viewMode == ViewType.WEEK)
                            DayType.NORMAL -> {
                                val hiddenLessons = state.lessons[date]!!.lessons.count {
                                    !state.activeProfile!!.isDefaultLessonEnabled(it.vpId)
                                }
                                if (hiddenLessons > 0) {
                                    if (state.viewMode == ViewType.DAY) Text(
                                        text = stringResource(
                                            id = R.string.home_lessonsHidden,
                                            hiddenLessons
                                        ),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Default.VisibilityOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                            Text(
                                                text = "$hiddenLessons", style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }
                                }

                                val lessons = state.lessons[date]!!.lessons.sortedBy { it.lessonNumber }
                                    .filter {
                                        state.activeProfile!!.isDefaultLessonEnabled(it.vpId)
                                    }.groupBy { it.lessonNumber }
                                val importantLessons = state.lessons[date]!!.lessons.filter { l ->
                                    state.activeProfile!!.isDefaultLessonEnabled(l.vpId) && l.displaySubject != "-"
                                }.sortedBy { l -> l.lessonNumber }
                                LazyColumn {
                                    items(lessons.keys.toList()) { lessonNumber ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(4.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(top = 16.dp, start = 16.dp)
                                                ) {
                                                    Text(text = "$lessonNumber.", style = MaterialTheme.typography.headlineMedium)
                                                }
                                                val isNotFirstOrLastLesson = lessonNumber in (importantLessons.firstOrNull()?.lessonNumber?:0)..(importantLessons.lastOrNull()?.lessonNumber?:Integer.MAX_VALUE)
                                                Column(
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    lessons[lessonNumber]!!.forEachIndexed { index, it ->
                                                        Box(
                                                            modifier = Modifier
                                                                .padding(end = 4.dp)
                                                        ) {
                                                            LessonCard(
                                                                time = state.time,
                                                                lesson = it,
                                                                width = width.dp,
                                                                displayMode = state.activeProfile!!.type,
                                                                isCompactMode = state.viewMode == ViewType.WEEK,
                                                                showFindAvailableRoom =
                                                                date.isEqual(LocalDate.now()) &&
                                                                        isNotFirstOrLastLesson &&
                                                                        state.activeProfile.type == ProfileType.STUDENT &&
                                                                        it.displaySubject == "-",
                                                                onFindAvailableRoomClicked = onFindAvailableRoomClicked
                                                            )
                                                        }
                                                        if (index != lessons[lessonNumber]!!.size - 1) HorizontalDivider(
                                                            color = MaterialTheme.colorScheme.surface,
                                                            modifier = Modifier.padding(start = 16.dp, end = 24.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider()

                                LazyColumn {
                                    items(
                                        state.lessons[date]!!.lessons.sortedBy { it.lessonNumber }
                                            .filter {
                                                state.activeProfile!!.isDefaultLessonEnabled(it.vpId)
                                            },
                                    ) {
                                        val isNotFirstOrLastLesson = it.lessonNumber in (importantLessons.firstOrNull()?.lessonNumber?:0)..(importantLessons.lastOrNull()?.lessonNumber?:Integer.MAX_VALUE)
                                        LessonCard(
                                            time = state.time,
                                            lesson = it,
                                            width = width.dp,
                                            displayMode = state.activeProfile!!.type,
                                            isCompactMode = state.viewMode == ViewType.WEEK,
                                            showFindAvailableRoom =
                                                    date.isEqual(LocalDate.now()) &&
                                                    isNotFirstOrLastLesson &&
                                                    state.activeProfile.type == ProfileType.STUDENT &&
                                                    it.displaySubject == "-",
                                            onFindAvailableRoomClicked = onFindAvailableRoomClicked
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        val scope = rememberCoroutineScope()
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            DateIndicator(
                displayDate = state.date,
                alpha = 1 - abs(lessonPagerState.currentPageOffsetFraction) * 2,
                onClick = {
                    scope.launch {
                        lessonPagerState.animateScrollToPage(Int.MAX_VALUE / 2)
                    }
                }
            )

        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun HomeScreenPreview() {
    HomeScreenContent(
        HomeState(
            lastSync = LocalDateTime.now(),
            isLoading = true,
            lessons = hashMapOf(
                LocalDate.now() to Day(
                    type = DayType.NORMAL,
                    state = DayDataState.DATA,
                    date = LocalDate.now(),
                    lessons = Lessons.generateLessons(4)
                )
            ),
            syncing = true,
        ),
        onMenuOpened = {}
    )
}