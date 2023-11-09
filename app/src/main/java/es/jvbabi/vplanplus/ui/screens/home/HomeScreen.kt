package es.jvbabi.vplanplus.ui.screens.home

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.home.components.SearchBar
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
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
        rememberPagerState(initialPage = LocalDate.now().dayOfWeek.value - 1, pageCount = { 7 })

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
                    if (it == ViewType.WEEK) lessonPagerState.animateScrollToPage(0)
                    if (it == ViewType.DAY) lessonPagerState.animateScrollToPage(state.date.dayOfWeek.value - 1)
                }
            }, lessonPagerState = lessonPagerState
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
            profiles = state.profiles,
            selectedProfile = state.activeProfile!!,
            onProfileClicked = {
                menuOpened = false
                viewModel.onProfileSelected(context, it)
            },
            onCloseClicked = {
                menuOpened = false
            },
            onRefreshClicked = {
                coroutineScope.launch {
                    viewModel.getVPlanData()
                    menuOpened = false
                }
            },
            onDeletePlansClicked = {
                coroutineScope.launch {
                    viewModel.deletePlans(context)
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
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeState,
    onMenuOpened: () -> Unit = {},
    onViewModeChanged: (type: ViewType) -> Unit = {},
    lessonPagerState: PagerState = rememberPagerState(
        initialPage = LocalDate.now().dayOfWeek.value,
        pageCount = { 5 })
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
                SearchBar(state.activeProfile?.name ?: "", onMenuOpened) {}
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
                            Icon(imageVector = Icons.Default.ViewWeek, contentDescription = null)
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
                    ) { dayOfWeek ->
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(top = 8.dp)
                        ) {
                            val date = state.date.atStartOfWeek().plusDays(dayOfWeek.toLong())
                            if (state.lessons[date] == null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "No lessons")
                                }
                                return@HorizontalPager
                            }
                            if (state.lessons[date]!!.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                                return@HorizontalPager
                            }
                            LazyColumn {
                                items(
                                    state.lessons[date]!!.sortedBy { it.lessonNumber },
                                    key = { it.id }
                                ) {
                                    if ((calculateProgress(
                                            it.start,
                                            LocalTime.now().toString(),
                                            it.end
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
                            lessonPagerState.animateScrollToPage(LocalDate.now().dayOfWeek.value - 1)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val visibleDate =
                    state.date.atStartOfWeek().plusDays(lessonPagerState.currentPage.toLong())
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
            val percentage = calculateProgress(lesson.start, currentTime, lesson.end)

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
                                text = lesson.subject,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (lesson.subjectChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = lesson.room.joinToString(", "),
                                style = MaterialTheme.typography.titleLarge,
                                color = if (lesson.roomChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Text(
                            text = lesson.teacher.joinToString(", "),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (lesson.teacherChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(text = lesson.info, style = MaterialTheme.typography.bodyMedium)
                    }
                    SubjectIcon(
                        subject = lesson.subject, modifier = Modifier
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
                                style = MaterialTheme.typography.titleLarge.copy(textDecoration = if (lesson.subjectChanged && lesson.subject == "-") TextDecoration.LineThrough else null),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            Column {
                                val onSecondaryContainerColor =
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                Row(
                                    modifier = if (lesson.subjectChanged && lesson.subject == "-") Modifier.drawBehind {
                                        drawLine(
                                            color = onSecondaryContainerColor,
                                            start = Offset(0f, size.height / 2 - 1f),
                                            end = Offset(size.width, size.height / 2 - 1f),
                                            strokeWidth = 4f
                                        )
                                    } else Modifier
                                ) {
                                    Text(
                                        text = lesson.subject,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (lesson.subjectChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = " • ",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = lesson.room.joinToString(", "),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (lesson.roomChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = " • ",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = lesson.teacher.joinToString(", "),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (lesson.teacherChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                if (lesson.info != "") {
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
                        subject = lesson.subject, modifier = Modifier
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
        lesson = Lesson(
            id = 0,
            subject = "Informatik",
            teacher = listOf("Tec", "Bat"),
            room = listOf("208", "209"),
            roomChanged = true,
            start = "21:00",
            end = "21:45",
            className = "9e",
            lessonNumber = 1,
            info = "Info!"
        )
    )
}


@Composable
@Preview
fun LessonCardPreview() {
    LessonCard(
        width = 200f,
        lesson = Lesson(
            id = 0,
            subject = "Informatik",
            teacher = listOf("Tec", "Bat"),
            room = listOf("208"),
            roomChanged = true,
            start = "8:00",
            end = "8:45",
            className = "9e",
            lessonNumber = 1,
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
            lessons =
            hashMapOf(
                LocalDate.now() to listOf(
                    Lesson(
                        id = 0,
                        subject = "Informatik",
                        teacher = listOf("Tec"),
                        room = listOf("208"),
                        roomChanged = true,
                        start = "21:00",
                        end = "22:00",
                        className = "9e",
                        lessonNumber = 1
                    ),
                    Lesson(
                        id = 1,
                        subject = "-",
                        subjectChanged = true,
                        teacher = listOf("Pfl"),
                        room = listOf("307"),
                        roomChanged = false,
                        teacherChanged = true,
                        start = "22:00",
                        end = "23:00",
                        className = "9e",
                        lessonNumber = 2,
                        info = "Hier eine Info :)"
                    ),
                    Lesson(
                        id = 2,
                        subject = "Biologie",
                        teacher = listOf("Pfl"),
                        room = listOf("307"),
                        roomChanged = false,
                        teacherChanged = true,
                        start = "22:00",
                        end = "23:00",
                        className = "9e",
                        lessonNumber = 2,
                        info = "Hier eine sehr lange Information, die sich über mehrere Zeilen erstrecken würde. :)"
                    )
                )
            )

        ),
        onMenuOpened = {}
    )
}

@SuppressLint("SimpleDateFormat")
fun calculateProgress(start: String, current: String, end: String): Double? {
    return try {
        val dateFormat = SimpleDateFormat("HH:mm")
        val startTime = dateFormat.parse(start)!!
        val currentTime = dateFormat.parse(current)!!
        val endTime = dateFormat.parse(end)!!

        val totalTime = (endTime.time - startTime.time).toDouble()
        val elapsedTime = (currentTime.time - startTime.time).toDouble()

        (elapsedTime / totalTime)
    } catch (e: ParseException) {
        null
    }
}