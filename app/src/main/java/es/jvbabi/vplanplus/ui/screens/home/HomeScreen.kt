package es.jvbabi.vplanplus.ui.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.screens.Screen
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val coroutineScope = rememberCoroutineScope()
    var menuOpened by remember { mutableStateOf(false) }

    LaunchedEffect("Init") {
        viewModel.init()
    }

    if (state.initDone && state.activeProfile == null) {
        navHostController.navigate(Screen.OnboardingWelcomeScreen.route) {
            popUpTo(0)
        }
    } else {
        Button(onClick = {
            if (state.viewMode == ViewType.WEEK) viewModel.setViewType(ViewType.DAY)
            else viewModel.setViewType(ViewType.WEEK)
        }) {
            Text(text = "WEEK")
        }
        HomeScreenContent(state = state, onGetVPlan = {
            coroutineScope.launch {
                viewModel.getVPlanData()
            }
        }, onMenuOpened = {
            menuOpened = true
        })
    }
    val context = LocalContext.current

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
                viewModel.onProfileSelected(it)
            },
            onCloseClicked = {
                menuOpened = false
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    state: HomeState,
    onGetVPlan: () -> Unit,
    onMenuOpened: () -> Unit = {}
) {
    val context = LocalContext.current
    if (!state.initDone) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(color = MaterialTheme.colorScheme.secondaryContainer)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            Toast
                                .makeText(context, "Not implemented", LENGTH_SHORT)
                                .show()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .height(20.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = stringResource(id = R.string.home_search),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .height(40.dp)
                            .width(40.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = MaterialTheme.colorScheme.secondary)
                            .clickable(enabled = true) {
                                onMenuOpened()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.activeProfile?.name ?: "--",
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)

            ) {
                Column {
                    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE/2, pageCount = { Int.MAX_VALUE })
                    val width by animateFloatAsState(targetValue = if (state.viewMode == ViewType.DAY) LocalConfiguration.current.screenWidthDp.toFloat() else LocalConfiguration.current.screenWidthDp / 5f,
                        label = "Plan View Changed Animation"
                    )
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .clickable {
                                val clickedItem = pagerState.settledPage % Int.MAX_VALUE
                                Log.d("HomeScreen", "Clicked item: $clickedItem")
                            },
                        pageSize = PageSize.Fixed(width.dp),
                    ) {
                        Box(
                            modifier = Modifier.width(200.dp)
                        ) pagerItem@{
                            androidx.compose.animation.AnimatedVisibility(
                                visible = state.viewMode == ViewType.WEEK,
                                enter = fadeIn(animationSpec = TweenSpec(200)),
                                exit = fadeOut(animationSpec = TweenSpec(200))
                            ) { Text(text = "I am text nr $it", overflow = TextOverflow.Ellipsis, maxLines = 1) }
                            androidx.compose.animation.AnimatedVisibility(
                                visible = state.viewMode == ViewType.DAY,
                                enter = fadeIn(animationSpec = TweenSpec(200)),
                                exit = fadeOut(animationSpec = TweenSpec(200))
                            ) { Text(text = "$it", overflow = TextOverflow.Ellipsis, maxLines = 1) }
                        }
                    }
                    Text(text = "Next holiday: ${state.nextHoliday}")
                    Button(
                        enabled = !state.isLoading,
                        onClick = {
                            onGetVPlan()
                        }
                    ) {
                        if (state.isLoading) CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .width(24.dp)
                                .height(24.dp)
                                .padding(6.dp)
                        )
                        else Text(text = "Get VPlan data")
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                if (state.lessons[state.date]?.isNotEmpty() == true) {
                    LazyColumn {
                        items(
                            state.lessons[state.date]!!.sortedBy { it.lessonNumber },
                            key = { it.id }
                        ) {
                            if ((calculateProgress(it.start, LocalTime.now().toString(), it.end)
                                    ?: -1.0) in 0.0..0.99
                            ) {
                                CurrentLessonCard(lesson = it)
                            } else {
                                LessonCard(lesson = it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentLessonCard(lesson: Lesson) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
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
                                text = lesson.room,
                                style = MaterialTheme.typography.titleLarge,
                                color = if (lesson.roomChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Text(
                            text = lesson.teacher,
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
fun LessonCard(lesson: Lesson) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
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
                                        text = lesson.room,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (lesson.roomChanged) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = " • ",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        text = lesson.teacher,
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
        lesson = Lesson(
            id = 0,
            subject = "Informatik",
            teacher = "Tec",
            room = "208",
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
        lesson = Lesson(
            id = 0,
            subject = "Informatik",
            teacher = "Tec",
            room = "208",
            roomChanged = true,
            start = "8:00",
            end = "8:45",
            className = "9e",
            lessonNumber = 1,
        )
    )
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreenContent(
        HomeState(
            initDone = true,
            nextHoliday = LocalDate.now(),
            isLoading = true,
            lessons = hashMapOf(
                LocalDate.now() to listOf(
                    Lesson(
                        id = 0,
                        subject = "Informatik",
                        teacher = "Tec",
                        room = "208",
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
                        teacher = "Pfl",
                        room = "307",
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
                        teacher = "Pfl",
                        room = "307",
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
        onGetVPlan = {},
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