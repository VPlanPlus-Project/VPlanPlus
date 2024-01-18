package es.jvbabi.vplanplus.ui.screens.home.search.room

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.preview.Classes
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.FilterChips
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.Guide
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.LessonDialog
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.SearchField
import kotlin.random.Random

@Composable
fun FindAvailableRoomScreen(
    navController: NavHostController,
    roomSearchViewModel: RoomSearchViewModel = hiltViewModel()
) {
    val state = roomSearchViewModel.state.value

    FindAvailableRoomScreenContent(
        state = state,
        onBackClicked = { navController.navigateUp() },
        onRoomFilterValueChanged = { roomSearchViewModel.onRoomFilterValueChanged(it) },
        onNowToggled = { roomSearchViewModel.toggleFilterNow() },
        onNextToggled = { roomSearchViewModel.toggleFilterNext() },
        onOpenLessonDetailDialog = { roomSearchViewModel.showDialog(it) },
        onCloseLessonDetailDialog = { roomSearchViewModel.closeDialog() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindAvailableRoomScreenContent(
    state: RoomSearchState,
    onBackClicked: () -> Unit,
    onRoomFilterValueChanged: (String) -> Unit = {},
    onNowToggled: () -> Unit = {},
    onNextToggled: () -> Unit = {},
    onOpenLessonDetailDialog: (Lesson) -> Unit = {},
    onCloseLessonDetailDialog: () -> Unit = {}
) {
    val show0 = state.rooms?.rooms?.any { it.lessons[0] != null } ?: true
    val zeroMod = if (show0) 1 else 0

    if (state.detailLesson != null) {
        LessonDialog(
            lesson = state.detailLesson,
            onCloseLessonDetailDialog = onCloseLessonDetailDialog
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.search_searchAvailableRoom)) },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        BackIcon()
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(text = state.currentSchool?.name ?: stringResource(id = R.string.loadingData))
            Guide(className = state.currentClass?.name)

            SearchField(state.roomFilter) { onRoomFilterValueChanged(it) }
            if (state.showFilterChips) FilterChips(
                currentLesson = state.currentLesson,
                filterNowActive = state.filterNow,
                filterNextActive = state.filterNext,
                filterNowToggled = { onNowToggled() },
                filterNextToggled = { onNextToggled() }
            )
            if (state.loading || state.rooms == null) {
                Loading()
                return@Scaffold
            }
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    Row(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .weight(2 / (12f + zeroMod), false),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceAround
                        ) {}
                        repeat(state.rooms.maxLessons + zeroMod - 1) { lessonNumber ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (lessonNumber - zeroMod.toDouble() == state.currentLesson) MaterialTheme.colorScheme.tertiaryContainer
                                        else if (lessonNumber - zeroMod.toDouble() < (state.currentLesson
                                                ?: (-1).toDouble())
                                        ) MaterialTheme.colorScheme.secondaryContainer
                                        else MaterialTheme.colorScheme.primaryContainer
                                    )
                                    .weight(1 / (11f + zeroMod), false),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(
                                    text = "${lessonNumber - zeroMod + 1}",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            top = 4.dp,
                            bottom = 2.dp,
                            start = 2.dp,
                            end = 2.dp
                        )
                    )
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (state.rooms.rooms.isNotEmpty()) {
                            state.rooms.rooms
                                .sortedBy { it.room.name }
                                .forEach {
                                    var map = it.lessons
                                    if (!show0) map = map.drop(1)
                                    RoomListRecord(
                                        name = it.room.name,
                                        lessons = map,
                                        displayed = it.displayed,
                                        onLessonClicked = { lesson ->
                                            onOpenLessonDetailDialog(lesson)
                                        }
                                    )
                                }
                        } else {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null
                                    )
                                    Text(text = stringResource(id = R.string.search_noResultsFound))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FindAvailableRoomScreenPreview() {
    val school = School.generateRandomSchools(1).first()
    FindAvailableRoomScreenContent(
        state = RoomSearchState(
            currentSchool = school,
            loading = false,
            currentClass = Classes.generateClass(school),
            detailLesson = null
        ),
        onBackClicked = {},
    )
}

@Composable
private fun RoomListRecord(
    name: String,
    lessons: List<Lesson?>,
    displayed: Boolean,
    onLessonClicked: (Lesson) -> Unit = {}
) {
    val height = animateFloatAsState(targetValue = if (displayed) 48f else 0f, label = "room entry")
    Box(
        modifier = Modifier.height(height.value.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .weight(2 / 13f, false),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            lessons.forEach { lesson ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (lesson == null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error)
                        .weight(1 / 12f, false)
                        .clickable {
                            if (lesson != null) onLessonClicked(lesson)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                }
            }
        }
    }
}

@Preview
@Composable
private fun RoomListRecordPreview() {
    RoomListRecord(
        name = "r220",
        lessons = Array(12) {
            if (Random.nextBoolean()) Lessons.generateLessons(1).first() else null
        }.toList(),
        true
    )
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}