package es.jvbabi.vplanplus.feature.main_home.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NextWeek
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.SearchView
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.Menu
import es.jvbabi.vplanplus.feature.main_home.ui.components.Greeting
import es.jvbabi.vplanplus.feature.main_home.ui.components.LastSyncText
import es.jvbabi.vplanplus.feature.main_home.ui.components.LessonCard
import es.jvbabi.vplanplus.feature.main_home.ui.components.NextDaySubjectCard
import es.jvbabi.vplanplus.feature.main_home.ui.components.VersionHintsInformation
import es.jvbabi.vplanplus.ui.common.CollapsableInfoCard
import es.jvbabi.vplanplus.ui.common.Grid
import es.jvbabi.vplanplus.ui.common.SegmentedButtonItem
import es.jvbabi.vplanplus.ui.common.SegmentedButtons
import es.jvbabi.vplanplus.ui.common.keyboardAsState
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    navBar: @Composable () -> Unit
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(state.goToNextDay) {
        if (state.goToNextDay == null) return@LaunchedEffect
        pagerState.animateScrollToPage(if (state.goToNextDay) 0 else 1)
    }

    HomeScreenContent(
        state = state,
        navBar = navBar,
        pagerState = pagerState,
        onOpenMenu = viewModel::onMenuOpenedChange,
        onChangeInfoExpandState = viewModel::onInfoExpandChange,
        onProfileClicked = { viewModel.onMenuOpenedChange(false); viewModel.switchProfile(it) },
        onProfileLongClicked = {
            viewModel.onMenuOpenedChange(false); navHostController.navigate(
            Screen.SettingsProfileScreen.route + it.id
        )
        },
        onManageProfilesClicked = {
            viewModel.onMenuOpenedChange(false); navHostController.navigate(
            Screen.SettingsProfileScreen.route
        )
        },
        onNewsClicked = { viewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.NewsScreen.route) },
        onSettingsClicked = { viewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.SettingsScreen.route) },
        onPrivacyPolicyClicked = {
            openLink(
                context,
                "https://github.com/VPlanPlus-Project/VPlanPlus/blob/main/PRIVACY-POLICY.md"
            )
        },
        onRepositoryClicked = {
            openLink(
                context,
                "https://github.com/VPlanPlus-Project/VPlanPlus"
            )
        },
        onRefreshClicked = { viewModel.onMenuOpenedChange(false); viewModel.onRefreshClicked(context) },
        onAddHomework = { vpId -> navHostController.navigate(Screen.AddHomeworkScreen.route + "?vpId=$vpId") },
        onBookRoomClicked = { navHostController.navigate(Screen.SearchAvailableRoomScreen.route) },
        onVersionHintsClosed = viewModel::hideVersionHintsDialog,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun HomeScreenContent(
    state: HomeState,
    pagerState: PagerState,
    navBar: @Composable () -> Unit,
    onOpenMenu: (open: Boolean) -> Unit,
    onChangeInfoExpandState: (Boolean) -> Unit,
    onProfileClicked: (Profile) -> Unit = {},
    onProfileLongClicked: (Profile) -> Unit = {},
    onManageProfilesClicked: () -> Unit = {},
    onNewsClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onPrivacyPolicyClicked: () -> Unit = {},
    onRepositoryClicked: () -> Unit = {},
    onRefreshClicked: () -> Unit = {},
    onAddHomework: (vpId: Long?) -> Unit = {},
    onBookRoomClicked: () -> Unit = {},
    onVersionHintsClosed: (untilNextVersion: Boolean) -> Unit = {}
) {
    if (state.isVersionHintsDialogOpen) VersionHintsInformation(
        currentVersion = state.currentVersion,
        hints = state.versionHints,
        onCloseUntilNextTime = { onVersionHintsClosed(false) },
        onCloseUntilNextVersion = { onVersionHintsClosed(true) }
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = !keyboardAsState().value,
                enter = expandVertically(tween(250)),
                exit = shrinkVertically(tween(250))
            ) {
                navBar()
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .fillMaxSize()
        ) {
            SearchView(
                onOpenMenu = { onOpenMenu(true) },
                onFindAvailableRoomClicked = onBookRoomClicked
            )

            LazyColumn content@{
                item {
                    Greeting(
                        modifier = Modifier.padding(8.dp),
                        time = state.time,
                        name = state.currentIdentity?.vppId?.name
                    )
                }
                item {
                    LastSyncText(
                        lastSync = state.lastSync,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                stickyHeader {
                    val scope = rememberCoroutineScope()
                    SegmentedButtons(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                    )
                                )
                            )
                            .padding(8.dp)
                    ) {
                        SegmentedButtonItem(
                            selected = pagerState.currentPage == 0,
                            onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                            label = { Text(text = stringResource(id = R.string.home_planTodayToggle)) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Work,
                                    contentDescription = null
                                )
                            }
                        )
                        SegmentedButtonItem(
                            selected = pagerState.currentPage == 1,
                            onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                            label = {
                                Text(
                                    state.nextDay?.date?.format(DateTimeFormatter.ofPattern("EEEE"))
                                        ?: "-"
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.NextWeek,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }

                item content@{
                    var biggestHeight by remember { mutableStateOf(0.dp) }
                    val density = LocalDensity.current
                    HorizontalPager(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        state = pagerState
                    ) { i ->
                        if (i == 0) Column(
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = biggestHeight)
                                .onSizeChanged {
                                    density.run {
                                        biggestHeight = maxOf(biggestHeight, it.height.toDp())
                                    }
                                }) today@{
                            if (state.todayDay?.type == DayType.NORMAL) {
                                Column {
                                    if (state.todayDay.info != null) CollapsableInfoCard(
                                        imageVector = Icons.Default.Info,
                                        title = stringResource(id = R.string.home_activeDaySchoolInformation),
                                        text = state.todayDay.info,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        ),
                                        isExpanded = state.infoExpanded,
                                        onChangeState = onChangeInfoExpandState
                                    )
                                    state
                                        .todayDay
                                        .getFilteredLessons(state.currentIdentity!!.profile!!)
                                        .groupBy { it.lessonNumber }
                                        .toList()
                                        .forEach { (_, lessons) ->
                                            LessonCard(
                                                lessons = lessons.filter {
                                                    state.currentIdentity.profile!!.isDefaultLessonEnabled(
                                                        it.vpId
                                                    )
                                                },
                                                bookings = state.bookings,
                                                time = state.time,
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                ),
                                                homework = state.userHomework,
                                                onAddHomeworkClicked = { onAddHomework(it) },
                                                onBookRoomClicked = onBookRoomClicked
                                            )
                                        }
                                    val end = state
                                        .todayDay
                                        .lessons
                                        .last {
                                            state.currentIdentity.profile!!.isDefaultLessonEnabled(
                                                it.vpId
                                            )
                                        }
                                        .end
                                    val difference = state.time.until(end, ChronoUnit.SECONDS)
                                    if (difference > 0) Column(
                                        Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SportsEsports,
                                            contentDescription = null,
                                            modifier = Modifier.size(30.dp)
                                        )
                                        Text(
                                            text = stringResource(
                                                id = R.string.home_activeDayCountdown,
                                                formatDuration(difference)
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }
                            } else if (state.todayDay?.type == DayType.WEEKEND) {
                                Column(
                                    Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        modifier = Modifier.padding(16.dp),
                                        painter = getWeekendPainter(),
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(id = R.string.home_activeDayWeekendTitle),
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    HorizontalDivider(Modifier.padding(8.dp))
                                }
                            }
                        }
                        else Column((Modifier
                            .fillMaxWidth()
                            .heightIn(min = biggestHeight)
                            .onSizeChanged {
                                density.run {
                                    biggestHeight = maxOf(biggestHeight, it.height.toDp())
                                }
                            })) nextDay@{
                            if (state.nextDay != null) {
                                if (state.nextDay.state == DayDataState.NO_DATA || state.nextDay.type != DayType.NORMAL) {
                                    Text(
                                        text = stringResource(id = R.string.home_nextDayNoData),
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    return@nextDay
                                }
                                val nextDayLessons = state.nextDay.lessons
                                    .filter { lesson ->
                                        state.currentIdentity!!.profile!!.isDefaultLessonEnabled(
                                            lesson.vpId
                                        )
                                    }
                                    .filter { lesson -> lesson.displaySubject != "-" }
                                    .sortedBy { lesson -> lesson.lessonNumber }
                                Text(
                                    text = stringResource(
                                        id = R.string.home_nextDayStartingAt,
                                        state.nextDay.date.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")),
                                        nextDayLessons.firstOrNull()?.start?.toZonedLocalDateTime()
                                            ?.format(
                                                DateTimeFormatter.ofPattern("HH:mm")
                                            ) ?: "-"
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp)
                                )

                                if (state.nextDay.info != null) {
                                    Text(
                                        text = state.nextDay.info,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontStyle = FontStyle.Italic,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                                val subjects = nextDayLessons
                                    .filter { lesson -> lesson.displaySubject != "-" }
                                    .map { lesson -> lesson.displaySubject }
                                    .distinct()
                                if (subjects.isEmpty()) return@nextDay

                                Grid(
                                    columns = 2,
                                    modifier = Modifier.padding(8.dp),
                                    content = subjects.map { subject ->
                                        { _, _, i ->
                                            val lessonsForSubject =
                                                nextDayLessons.filter { lesson -> lesson.displaySubject == subject }
                                            val subjectHomework = state.userHomework
                                                .filter { lesson -> lesson.until.toLocalDate() == state.nextDay.date }
                                                .filter { lessonsForSubject.any { lesson -> lesson.vpId == it.defaultLesson.vpId } }

                                            val bigRadius = 24.dp
                                            val smallRadius = 4.dp
                                            val borderRadiusTopLeft =
                                                if (i == 0) bigRadius else smallRadius
                                            val borderRadiusTopRight =
                                                if ((i == 1 && subjects.size > 1) || (i == 0 && subjects.size == 1)) bigRadius else smallRadius
                                            val borderRadiusBottomLeft =
                                                if ((i == subjects.lastIndex && subjects.size % 2 == 1) || (i == subjects.lastIndex - 1 && subjects.size % 2 == 0)) bigRadius else smallRadius
                                            val borderRadiusBottomRight =
                                                if (i == subjects.lastIndex) bigRadius else smallRadius

                                            val modifier = Modifier
                                                .padding(1.dp)
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = borderRadiusTopLeft,
                                                        topEnd = borderRadiusTopRight,
                                                        bottomStart = borderRadiusBottomLeft,
                                                        bottomEnd = borderRadiusBottomRight
                                                    )
                                                )

                                            NextDaySubjectCard(
                                                subject = subject,
                                                lessonNumbers = lessonsForSubject.map { it.lessonNumber },
                                                homework = subjectHomework.count { homework -> homework.tasks.any { !it.done } && !homework.isHidden },
                                                modifier = modifier
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.currentIdentity != null) Menu(
        isVisible = state.menuOpened,
        profiles = state.profiles,
        hasUnreadNews = false,
        selectedProfile = state.currentIdentity.profile!!,
        onCloseMenu = { onOpenMenu(false) },
        onProfileClicked = onProfileClicked,
        onManageProfilesClicked = onManageProfilesClicked,
        onProfileLongClicked = onProfileLongClicked,
        onNewsClicked = onNewsClicked,
        onSettingsClicked = onSettingsClicked,
        onPrivacyPolicyClicked = onPrivacyPolicyClicked,
        onRepositoryClicked = onRepositoryClicked,
        onRefreshClicked = onRefreshClicked
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true)
private fun HomeScreenPreview() {
    HomeScreenContent(
        state = HomeState(),
        onOpenMenu = {},
        navBar = {},
        onChangeInfoExpandState = {},
        pagerState = rememberPagerState { 2 }
    )
}

private fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

@Composable
private fun getWeekendPainter(): Painter {
    return if (isSystemInDarkTheme()) painterResource(id = R.drawable.weekend_dark)
    else painterResource(id = R.drawable.weekend)
}