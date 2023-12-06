package es.jvbabi.vplanplus.ui.screens.home.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.home.components.SearchBar
import java.util.UUID

@Composable
fun SearchScreen(
    navHostController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val state = searchViewModel.state.value
    SearchContent(
        state = state,
        onSearchTyping = { searchViewModel.type(it) },
        onSearchClosed = { navHostController.popBackStack() },
        onFilterToggle = { searchViewModel.toggleFilter(it) },
        onFindAvailableRoomClicked = { navHostController.navigate(Screen.SearchAvailableRoomScreen.route) }
    )
}

@Composable
fun SearchContent(
    state: SearchState,
    onSearchTyping: (String) -> Unit,
    onSearchClosed: () -> Unit,
    onFilterToggle: (FilterType) -> Unit = {},
    onFindAvailableRoomClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            currentProfileName = "",
            onMenuOpened = { },
            onSearchClicked = { if (!it) onSearchClosed() },
            searchOpen = true,
            searchValue = state.searchValue,
            onSearchTyping = { onSearchTyping(it) },
            isSyncing = false,
            content = {
                AssistChip(
                    onClick = { onFindAvailableRoomClicked() },
                    label = { Text(text = stringResource(id = R.string.search_searchAvailableRoom)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
                val chipScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .horizontalScroll(state = chipScrollState)
                        .padding(start = 8.dp)
                ) {
                    val paddingModifier = Modifier.padding(end = 8.dp)
                    FilterChip(
                        selected = state.filter[FilterType.TEACHER]!!,
                        onClick = { onFilterToggle(FilterType.TEACHER) },
                        label = { Text(text = stringResource(id = R.string.search_teacherFilter)) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null)
                        },
                        modifier = paddingModifier
                    )
                    FilterChip(
                        selected = state.filter[FilterType.ROOM]!!,
                        onClick = { onFilterToggle(FilterType.ROOM) },
                        label = { Text(text = stringResource(id = R.string.search_roomFilter)) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.DoorBack, contentDescription = null)
                        },
                        modifier = paddingModifier
                    )
                    FilterChip(
                        selected = state.filter[FilterType.CLASS]!!,
                        onClick = { onFilterToggle(FilterType.CLASS) },
                        label = { Text(text = stringResource(id = R.string.search_classesFilter)) },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.People, contentDescription = null)
                        },
                        modifier = paddingModifier
                    )
                }
                if (state.result.isNotEmpty()) Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    state.result.forEach { resultGroup ->
                        SchoolResult(
                            name = resultGroup.school.name,
                            results = resultGroup.results,
                            filterMap = state.filter
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(id = R.string.search_searchTitle),
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = stringResource(id = R.string.search_searchText),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SchoolResult(name: String, results: List<Result>, filterMap: Map<FilterType, Boolean>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(),
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            FilterType.entries.forEach { filterType ->
                if (filterMap[filterType]!!) {
                    Text(
                        text = when (filterType) {
                            FilterType.TEACHER -> stringResource(id = R.string.search_teacherFilter)
                            FilterType.ROOM -> stringResource(id = R.string.search_roomFilter)
                            FilterType.CLASS -> stringResource(id = R.string.search_classesFilter)
//                            FilterType.PROFILE -> stringResource(id = R.string.search_profileFilter)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDefaults.cardColors().containerColor)
                    ) {
                        if (results.groupBy { it.type }[filterType]?.isEmpty() != false) {
                            Text(
                                text = stringResource(id = R.string.search_noResultsFound),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                val firstResult = results.sortedBy { it.name }
                                    .first { it.type == filterType }
                                Column {
                                    Text(
                                        text = firstResult.name,
                                        modifier = Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState())
                                    ) {
                                        firstResult.lessons.forEach { lesson ->
                                            Column(
                                                modifier = Modifier
                                                    .padding(start = 8.dp, bottom = 8.dp)
                                                    .width(55.dp)
                                                    .height(55.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(CardDefaults.cardColors().containerColor),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = lesson.lessonNumber.toString() + ". " + lesson.displaySubject,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Text(
                                                    text = when (filterType) {
                                                        FilterType.TEACHER -> lesson.`class`.name + " • " + lesson.rooms.joinToString(
                                                            ", "
                                                        )

                                                        FilterType.ROOM -> lesson.`class`.name + " • " + lesson.teachers.joinToString(
                                                            ", "
                                                        )

                                                        FilterType.CLASS -> lesson.teachers.joinToString(
                                                            ", "
                                                        ) + " • " + lesson.rooms.joinToString(", ")
                                                    },
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.basicMarquee(
                                                        iterations = Int.MAX_VALUE,

                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                results.filter { it.type == filterType }.sortedBy { it.name }.drop(1)
                    .forEach { result ->
                        Text(
                            text = result.name,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                    }
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun SearchPreview() {
    SearchContent(
        state = SearchState(),
        onSearchTyping = {},
        onSearchClosed = {},
        onFilterToggle = {}
    )
}

@Composable
@Preview(showBackground = true)
fun SchoolResultPreview() {
    SchoolResult(
        name = "Grundschule Oberau",
        results = listOf(
            Result(
                UUID.randomUUID(),
                Lessons.randomRoom().first().name,
                FilterType.ROOM,
                Lessons.generateLessons(3)
            ),
            Result(
                UUID.randomUUID(),
                Lessons.randomRoom().first().name,
                FilterType.ROOM,
                Lessons.generateLessons(3)
            ),
            Result(
                UUID.randomUUID(),
                Lessons.randomTeacher().first().acronym,
                FilterType.TEACHER,
                Lessons.generateLessons(3)
            ),
            Result(
                UUID.randomUUID(),
                Lessons.randomTeacher().first().acronym,
                FilterType.TEACHER,
                Lessons.generateLessons(3)
            ),
        ),
        filterMap = mapOf(
            FilterType.TEACHER to true,
            FilterType.ROOM to true,
            FilterType.CLASS to true,
//            FilterType.PROFILE to true
        )
    )
}