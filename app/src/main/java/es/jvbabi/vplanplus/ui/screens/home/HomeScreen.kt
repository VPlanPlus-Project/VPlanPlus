package es.jvbabi.vplanplus.ui.screens.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.ViewWeek
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.home.components.SearchBar
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.calculateProgress
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
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
    val lessonPagerState =
        rememberPagerState(initialPage = Int.MAX_VALUE / 2, pageCount = { Int.MAX_VALUE })

    LaunchedEffect(key1 = "Init", block = {
        viewModel.init(context)
    })

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.setNotificationPermissionGranted(isGranted)
            if (!isGranted) {
                Toast.makeText(
                    context,
                    context.getString(R.string.notification_accessNotGranted),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )
    LaunchedEffect(key1 = state.notificationPermissionGranted, block = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    })

    if (state.initDone && state.activeProfile == null) {
        navHostController.navigate(Screen.OnboardingWelcomeScreen.route) {
            popUpTo(0)
        }
    } else {
        HomeScreenContent(state = state,
            onMenuOpened = {
                menuOpened = true
            }, onViewModeChanged = {
                viewModel.setViewType(it)
                coroutineScope.launch {
                    delay(450)
                    if (it == ViewType.WEEK) lessonPagerState.animateScrollToPage(Int.MAX_VALUE / 2)
                    if (it == ViewType.DAY) lessonPagerState.animateScrollToPage(Int.MAX_VALUE / 2)
                }
            }, lessonPagerState = lessonPagerState,
            onSetDayType = {
                viewModel.setDayType(it)
            },
            onSearchOpened = {
                navHostController.navigate(Screen.SearchScreen.route)
            }
        )
    }

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
                viewModel.onProfileSelected(context, it)
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
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeState,
    onSearchOpened: (Boolean) -> Unit = {},
    onMenuOpened: () -> Unit = {},
    onViewModeChanged: (type: ViewType) -> Unit = {},
    onSetDayType: (date: LocalDate) -> Unit = {},
    lessonPagerState: PagerState = rememberPagerState(
        initialPage = LocalDate.now().dayOfWeek.value,
        pageCount = { 5 }),
) {
    if (!state.initDone) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                SearchBar(if ((state.activeProfile?.displayName
                        ?: "").length > 4
                ) state.activeProfile?.originalName ?: "" else state.activeProfile?.displayName ?: "",
                    onMenuOpened,
                    { onSearchOpened(it) },
                    false,
                    "",
                    {})
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = state.viewMode == ViewType.WEEK,
                            onClick = { onViewModeChanged(ViewType.WEEK) },
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ViewWeek,
                                    contentDescription = null
                                )
                                Text(
                                    text = "Alpha",
                                    color = Color.Cyan,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        SegmentedButton(
                            selected = state.viewMode == ViewType.DAY,
                            onClick = { onViewModeChanged(ViewType.DAY) },
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Icon(imageVector = Icons.Default.ViewDay, contentDescription = null)
                        }
                    }
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
                            val date = state.date.plusDays(index - Int.MAX_VALUE / 2L)
                            if (state.lessons[date] == null) {
                                onSetDayType(date)
                            }
                            when (state.lessons[date]?.dayType ?: DayType.NO_DATA) {
                                DayType.LOADING -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }

                                DayType.NO_DATA -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "No data")
                                    }
                                }

                                DayType.WEEKEND -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .padding(horizontal = 24.dp)
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Weekend,
                                                contentDescription = null,
                                                modifier = Modifier.size(80.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = stringResource(id = R.string.home_weekendTitle),
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                            var todayIsSameWeekendAsPagerDay = true
                                            if (!LocalDate.now().isEqual(date)) {
                                                var d = LocalDate.now()
                                                while (!d.isEqual(date)) {
                                                    d = if (LocalDate.now()
                                                            .isBefore(date)
                                                    ) d.plusDays(1)
                                                    else d.minusDays(1)

                                                    if (state.lessons[d]?.dayType != DayType.WEEKEND) {
                                                        todayIsSameWeekendAsPagerDay = false
                                                        break
                                                    }
                                                }
                                            }
                                            if (todayIsSameWeekendAsPagerDay) Text(
                                                text = stringResource(id = R.string.home_weekendText),
                                                textAlign = TextAlign.Center
                                            )
                                            else if (LocalDate.now().isBefore(date)) Text(
                                                text = stringResource(id = R.string.home_weekendCommingUpText),
                                                textAlign = TextAlign.Center
                                            )
                                            else Text(
                                                text = stringResource(id = R.string.home_weekendOverText),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                DayType.HOLIDAY -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "Holiday")
                                    }
                                }

                                DayType.DATA -> {
                                    val hiddenLessons = state.lessons[date]!!.lessons.count { !state.activeProfile!!.isDefaultLessonEnabled(it.vpId) }
                                    if (hiddenLessons > 0) {
                                        Text(text = stringResource(id = R.string.home_lessonsHidden, hiddenLessons), style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(start = 8.dp))
                                    }
                                    LazyColumn {
                                        items(
                                            state.lessons[date]!!.lessons.sortedBy { it.lessonNumber }.filter { state.activeProfile!!.isDefaultLessonEnabled(it.vpId) },
                                        ) {
                                            if ((calculateProgress(
                                                    DateUtils.localDateTimeToTimeString(it.start),
                                                    LocalTime.now().toString(),
                                                    DateUtils.localDateTimeToTimeString(it.end)
                                                )
                                                    ?: -1.0) in 0.0..0.99
                                                &&
                                                date == LocalDate.now()
                                            ) {
                                                CurrentLessonCard(lesson = it, width = width)
                                            } else {
                                                LessonCard(lesson = it, width = width)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (state.syncing) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    trackColor = MaterialTheme.colorScheme.secondary,
                )
            }
            val scope = rememberCoroutineScope()
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .shadow(10.dp, shape = RoundedCornerShape(20.dp))
                    .height(40.dp)
                    .width(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .clickable {
                        scope.launch {
                            lessonPagerState.animateScrollToPage(Int.MAX_VALUE / 2)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val visibleDate =
                    state.date.plusDays(lessonPagerState.currentPage - Int.MAX_VALUE / 2L)
                val text = if (visibleDate == LocalDate.now()) stringResource(id = R.string.today)
                else if (visibleDate.minusDays(1L) == LocalDate.now()) stringResource(id = R.string.tomorrow)
                else if (visibleDate.plusDays(1L) == LocalDate.now()) stringResource(id = R.string.yesterday)
                else visibleDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.alpha(1 - abs(lessonPagerState.currentPageOffsetFraction) * 2)
                )
            }
        }
    }
}

@Composable
fun CurrentLessonCard(lesson: Lesson, width: Float) {
    Card(
        modifier = Modifier
            .width(width.dp)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (lesson.info == "") 100.dp else 130.dp)
        ) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val time = LocalTime.now()
            val currentTime = time.format(formatter)
            val percentage = calculateProgress(
                DateUtils.localDateTimeToTimeString(lesson.start),
                currentTime,
                DateUtils.localDateTimeToTimeString(lesson.end)
            )

            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .fillMaxWidth((percentage ?: 0).toFloat())
                    .fillMaxHeight()
            ) {}
            Box(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Jetzt: ", style = MaterialTheme.typography.titleSmall)
                        Row {
                            Text(
                                text = lesson.displaySubject,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (lesson.changedSubject != null) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = lesson.rooms.joinToString(", "),
                                style = MaterialTheme.typography.titleLarge,
                                color = if (lesson.roomIsChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Text(
                            text = lesson.teachers.joinToString(", "),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (lesson.teacherIsChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        if (lesson.info != null) Text(
                            text = lesson.info,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    SubjectIcon(
                        subject = lesson.displaySubject, modifier = Modifier
                            .height(70.dp)
                            .width(70.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LessonCard(lesson: Lesson, width: Float) {
    Card(
        modifier = Modifier
            .width(width.dp)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (lesson.info == "") 70.dp else 100.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f, false)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = lesson.lessonNumber.toString(),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            Column {
                                Row {
                                    Text(
                                        text = lesson.displaySubject,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (lesson.subjectIsChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = " • ",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = if (lesson.rooms.isNotEmpty()) lesson.rooms.joinToString(", ") { it } else "-",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (lesson.roomIsChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = " • ",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = if (lesson.teachers.isNotEmpty()) lesson.teachers.joinToString(", ") { it } else "-",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (lesson.teacherIsChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                if (lesson.info != null) {
                                    Text(
                                        text = lesson.info,
                                        maxLines = 1,
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .basicMarquee(
                                                // Animate forever.
                                                iterations = Int.MAX_VALUE,
                                                velocity = 80.dp,
                                                spacing = MarqueeSpacing(12.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }
                    SubjectIcon(
                        subject = lesson.displaySubject, modifier = Modifier
                            .height(50.dp)
                            .width(50.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun CurrentLessonCardPreview() {
    CurrentLessonCard(
        width = 200f,
        lesson = Lessons.generateLessons(1).first()
    )
}


@Composable
@Preview
fun LessonCardPreview() {
    LessonCard(
        width = 200f,
        lesson = Lessons.generateLessons(1).first()
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true)
fun WeekendPreview() {
    HomeScreenContent(
        state = HomeState(
            initDone = true,
            isLoading = false,
            lessons = hashMapOf(
                LocalDate.now() to Day(
                    dayType = DayType.WEEKEND,
                    lessons = listOf()
                )
            ),
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreenContent(
        HomeState(
            initDone = true,
            isLoading = true,
            lessons = hashMapOf(
                LocalDate.now() to Day(
                    dayType = DayType.DATA,
                    lessons = Lessons.generateLessons(4)
                )
            ),
            syncing = true,
        ),
        onMenuOpened = {}
    )
}