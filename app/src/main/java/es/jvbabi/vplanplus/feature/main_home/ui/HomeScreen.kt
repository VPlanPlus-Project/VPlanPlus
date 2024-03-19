package es.jvbabi.vplanplus.feature.main_home.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
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
import es.jvbabi.vplanplus.feature.main_home.ui.components.customStickyHeader
import es.jvbabi.vplanplus.ui.common.CollapsableInfoCard
import es.jvbabi.vplanplus.ui.common.Grid
import es.jvbabi.vplanplus.ui.common.keyboardAsState
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    navBar: @Composable () -> Unit
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    HomeScreenContent(
        state = state,
        navBar = navBar,
        onOpenMenu = viewModel::onMenuOpenedChange,
        onChangeInfoExpandState = viewModel::onInfoExpandChange,
        onToggleTodayLessonExpanded = viewModel::onTodayLessonExpandedToggle,
        onProfileClicked = { viewModel.onMenuOpenedChange(false); viewModel.switchProfile(it) },
        onProfileLongClicked = { viewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.SettingsProfileScreen.route + it.id) },
        onManageProfilesClicked = { viewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.SettingsProfileScreen.route) },
        onNewsClicked = { viewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.NewsScreen.route) },
        onSettingsClicked = { viewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.SettingsScreen.route) },
        onPrivacyPolicyClicked = { openLink(context, "https://github.com/VPlanPlus-Project/VPlanPlus/blob/main/PRIVACY-POLICY.md") },
        onRepositoryClicked = { openLink(context, "https://github.com/VPlanPlus-Project/VPlanPlus") },
        onRefreshClicked = { viewModel.onMenuOpenedChange(false); viewModel.onRefreshClicked(context) },
        onAddHomework = { vpId -> navHostController.navigate(Screen.AddHomeworkScreen.route + "?vpId=$vpId") },
        onBookRoomClicked = { navHostController.navigate(Screen.SearchAvailableRoomScreen.route) },
        onVersionHintsClosed = viewModel::hideVersionHintsDialog,
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun HomeScreenContent(
    state: HomeState,
    navBar: @Composable () -> Unit,
    onOpenMenu: (open: Boolean) -> Unit,
    onChangeInfoExpandState: (Boolean) -> Unit,
    onToggleTodayLessonExpanded: () -> Unit,
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

                if (state.todayDay != null && state.todayDay.type == DayType.NORMAL) customStickyHeader(Modifier.clickable { onToggleTodayLessonExpanded() }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val rotation = animateFloatAsState(
                            targetValue = if (state.todayLessonExpanded) 180f else 0f,
                            label = "Expand Card"
                        )
                        Column {
                            Text(
                                text = stringResource(
                                    id = R.string.home_planToday,
                                    state.todayDay.date.format(DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy"))
                                )
                            )
                            LastSyncText(lastSync = state.lastSync)
                        }
                        IconButton(onClick = onToggleTodayLessonExpanded) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                modifier = Modifier.rotate(rotation.value)
                            )
                        }
                    }
                }

                if (state.todayDay?.type == DayType.NORMAL) {
                    item {
                        AnimatedVisibility(
                            visible = state.todayLessonExpanded,
                            enter = expandVertically(tween(300)),
                            exit = shrinkVertically(tween(300))
                        ) {
                            Column(Modifier.fillMaxWidth()) {
                                if (state.todayDay.info != null) CollapsableInfoCard(
                                    imageVector = Icons.Default.Info,
                                    title = stringResource(id = R.string.home_activeDaySchoolInformation),
                                    text = state.todayDay.info,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                                                state.currentIdentity.profile!!.isDefaultLessonEnabled(it.vpId)
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
                                    .last { state.currentIdentity.profile!!.isDefaultLessonEnabled(it.vpId) }
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
                        }
                    }
                }
                else if (state.todayDay?.type == DayType.WEEKEND) {
                    item {
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

                if (state.nextDay != null) customStickyHeader {
                    Column {
                        Text(
                            text = stringResource(
                                id = R.string.home_nextDayTitle,
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )

                    }
                }
                if (state.nextDay != null) item {
                    if (state.nextDay.state == DayDataState.NO_DATA || state.nextDay.type != DayType.NORMAL) {
                        Text(
                            text = stringResource(id = R.string.home_nextDayNoData),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        return@item
                    }
                    val nextDayLessons = state.nextDay.lessons
                        .filter { state.currentIdentity!!.profile!!.isDefaultLessonEnabled(it.vpId) }
                        .filter { it.displaySubject != "-" }
                        .sortedBy { it.lessonNumber }
                    Text(
                        text = stringResource(
                            id = R.string.home_nextDayStartingAt,
                            state.nextDay.date.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")),
                            nextDayLessons.firstOrNull()?.start?.toZonedLocalDateTime()?.format(
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
                        .filter { it.displaySubject != "-" }
                        .map { it.displaySubject }
                        .distinct()
                    if (subjects.isEmpty()) return@item

                    Grid(
                        columns = 2,
                        modifier = Modifier.padding(8.dp),
                        content = subjects.map { subject ->
                            { _, _, i ->
                                val lessonsForSubject =
                                    nextDayLessons.filter { it.displaySubject == subject }
                                val subjectHomework = state.userHomework
                                    .filter { it.until.toLocalDate() == state.nextDay.date }
                                    .filter { lessonsForSubject.any { lesson -> lesson.vpId == it.defaultLesson.vpId } }

                                val bigRadius = 24.dp
                                val smallRadius = 4.dp
                                val borderRadiusTopLeft = if (i == 0) bigRadius else smallRadius
                                val borderRadiusTopRight = if ((i == 1 && subjects.size > 1) || (i == 0 && subjects.size == 1)) bigRadius else smallRadius
                                val borderRadiusBottomLeft = if ((i == subjects.lastIndex && subjects.size % 2 == 1) || (i == subjects.lastIndex-1 && subjects.size % 2 == 0)) bigRadius else smallRadius
                                val borderRadiusBottomRight = if (i == subjects.lastIndex) bigRadius else smallRadius

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

@Composable
@Preview(showBackground = true)
private fun HomeScreenPreview() {
    HomeScreenContent(
        state = HomeState(),
        onOpenMenu = {},
        navBar = {},
        onChangeInfoExpandState = {},
        onToggleTodayLessonExpanded = {}
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