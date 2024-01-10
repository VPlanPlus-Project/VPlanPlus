package es.jvbabi.vplanplus.ui.screens.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.screens.home.components.search.DetailedResult
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.HomeState
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.SearchResult
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

@Composable
fun SearchContent(
    state: HomeState,
    onFindAvailableRoomClicked: () -> Unit = {},
    onFilterToggle: (SchoolEntityType) -> Unit = {},
    time: LocalDateTime = LocalDateTime.now(),
    onSelectSearchResult: (schoolId: Long, type: SchoolEntityType, id: UUID) -> Unit
) {
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
            selected = state.filter[SchoolEntityType.TEACHER]!!,
            onClick = { onFilterToggle(SchoolEntityType.TEACHER) },
            label = { Text(text = stringResource(id = R.string.search_teacherFilter)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            },
            modifier = paddingModifier
        )
        FilterChip(
            selected = state.filter[SchoolEntityType.ROOM]!!,
            onClick = { onFilterToggle(SchoolEntityType.ROOM) },
            label = { Text(text = stringResource(id = R.string.search_roomFilter)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.DoorBack, contentDescription = null)
            },
            modifier = paddingModifier
        )
        FilterChip(
            selected = state.filter[SchoolEntityType.CLASS]!!,
            onClick = { onFilterToggle(SchoolEntityType.CLASS) },
            label = { Text(text = stringResource(id = R.string.search_classesFilter)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.People, contentDescription = null)
            },
            modifier = paddingModifier
        )
    }
    if (state.results.isNotEmpty()) Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(state.results.sortedBy { (if (it.school.schoolId == state.activeSchool?.schoolId) "0" else "1") + it.school.name }) { resultGroup ->
                SchoolResult(
                    name = resultGroup.school.name,
                    searchResults = resultGroup.searchResults,
                    filterMap = state.filter,
                    time = time,
                    onSelectSearchResult = { schoolId, type, id ->
                        onSelectSearchResult(schoolId, type, id)
                    },
                    schoolId = resultGroup.school.schoolId
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!state.fullyCompatible) {
                    InfoCard(
                        imageVector = Icons.Default.SearchOff,
                        title = stringResource(id = R.string.search_notFullySupportedTitle),
                        text = stringResource(id = R.string.search_notFullySupportedText),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
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


@Composable
fun SchoolResult(
    schoolId: Long,
    name: String,
    searchResults: List<SearchResult>,
    filterMap: Map<SchoolEntityType, Boolean>,
    time: LocalDateTime,
    onSelectSearchResult: (schoolId: Long, type: SchoolEntityType, id: UUID) -> Unit
) {
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
            filterMap.forEach { filterOption ->
                if (filterOption.value) {
                    Text(
                        text = when (filterOption.key) {
                            SchoolEntityType.TEACHER -> stringResource(id = R.string.search_teacherFilter)
                            SchoolEntityType.ROOM -> stringResource(id = R.string.search_roomFilter)
                            SchoolEntityType.CLASS -> stringResource(id = R.string.search_classesFilter)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDefaults.cardColors().containerColor)
                    ) {
                        if (searchResults.groupBy { it.type }[filterOption.key]?.isEmpty() != false) {
                            Text(
                                text = stringResource(id = R.string.search_noResultsFound),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                            )
                        }
                    }
                }
                searchResults.filter { it.type == filterOption.key }.sortedBy { (!it.detailed).toString() + it.name }
                    .forEach { result ->
                        if (result.detailed) {
                            Column {
                                DetailedResult(result, time)
                                if (searchResults.filter { it.type == filterOption.key }.size > 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                        )
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onSelectSearchResult(schoolId, result.type, result.id)
                                    }
                            ) {
                                Text(
                                    text = stringResource(
                                        id = R.string.search_notDetailedResult,
                                        result.name,
                                        result.lessons.filter { it.displaySubject != "-" }
                                            .groupBy { it.lessonNumber }.size
                                    ),
                                    modifier = Modifier.padding(8.dp),
                                )
                            }
                        }
                    }
            }
        }
    }

}

@Composable
@Preview(showBackground = true)
fun SchoolResultPreview() {
    SchoolResult(
        name = "Grundschule Oberau",
        searchResults = listOf(
            SearchResult(
                UUID.randomUUID(),
                Lessons.randomRoom().first().name,
                SchoolEntityType.ROOM,
                Lessons.generateLessons(3)
            ),
            SearchResult(
                UUID.randomUUID(),
                Lessons.randomRoom().first().name,
                SchoolEntityType.ROOM,
                Lessons.generateLessons(3),
                detailed = true
            ),
            SearchResult(
                UUID.randomUUID(),
                Lessons.randomTeacher().first().acronym,
                SchoolEntityType.TEACHER,
                Lessons.generateLessons(3)
            ),
            SearchResult(
                UUID.randomUUID(),
                Lessons.randomTeacher().first().acronym,
                SchoolEntityType.TEACHER,
                Lessons.generateLessons(3, true)
            ),
        ),
        filterMap = mapOf(
            SchoolEntityType.TEACHER to true,
            SchoolEntityType.ROOM to true,
            SchoolEntityType.CLASS to true
        ),
        time = LocalDateTime.now(),
        onSelectSearchResult = { _, _, _ -> },
        schoolId = Random.nextLong()
    )
}

@Composable
@Preview(showBackground = true)
private fun SearchContentPreview() {
    SearchContent(
        state = HomeState(
            filter = mapOf(
                SchoolEntityType.TEACHER to true,
                SchoolEntityType.ROOM to true,
                SchoolEntityType.CLASS to true
            ),
            fullyCompatible = false
        ),
        onSelectSearchResult = { _, _, _ -> }
    )
}