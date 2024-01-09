package es.jvbabi.vplanplus.ui.screens.home.search.room

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.preview.School
import kotlin.random.Random

@Composable
fun FindAvailableRoomScreen(
    navController: NavHostController,
    roomSearchViewModel: RoomSearchViewModel = hiltViewModel()
) {
    val state = roomSearchViewModel.state.value

    FindAvailableRoomScreenContent(
        state = state,
        onBackClicked = { navController.popBackStack() },
        onRoomFilterValueChanged = { roomSearchViewModel.onRoomFilterValueChanged(it) },
        onNowToggled = { roomSearchViewModel.toggleFilterNow() },
        onNextToggled = { roomSearchViewModel.toggleFilterNext() },
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
) {
    val show0 = state.rooms?.rooms?.any { it.availability[0] != null }?:true
    val zeroMod = if (show0) 1 else 0

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
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = state.currentSchool?.name?: stringResource(id = R.string.loadingData))
                Text(text = stringResource(id = R.string.searchAvailableRoom_text))
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    leadingIcon = { Icon(imageVector = Icons.Default.FilterAlt, contentDescription = null) },
                    placeholder = { Text(text = stringResource(id = R.string.searchAvailableRoom_findPlaceholder)) },
                    value = state.roomFilter,
                    onValueChange = { onRoomFilterValueChanged(it) }
                )
                if (state.currentClass != null && (state.currentLesson?:0.toDouble())+ 0.5 != state.rooms?.maxLessons?.toDouble()) Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(id = R.string.searchAvailableRoom_labelAvailability),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    if ((state.currentLesson?:0.5) % 1 == 0.toDouble()) FilterChip(
                        enabled = state.currentLesson != null,
                        selected = state.filterNow,
                        leadingIcon = { Icon(
                            imageVector = if (state.filterNow) Icons.Default.Check else Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        ) },
                        onClick = { onNowToggled() },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        label = { Text(text = stringResource(id = R.string.searchAvailableRoom_filterNow)) },
                    )
                    FilterChip(
                        enabled = true,
                        selected = state.filterNext,
                        leadingIcon = { Icon(
                            imageVector = if (state.filterNext) Icons.Default.Check else Icons.Default.MoreTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        ) },
                        onClick = { onNextToggled() },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        label = { Text(text = stringResource(id = R.string.searchAvailableRoom_filterNext)) },
                    )
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    if (!state.loading && state.rooms != null) Column {
                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            repeat(state.rooms.maxLessons+zeroMod) { lessonNumber ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(horizontal = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (lessonNumber - zeroMod.toDouble() == state.currentLesson) MaterialTheme.colorScheme.tertiaryContainer
                                            else if (lessonNumber - zeroMod.toDouble() < (state.currentLesson ?: (-1).toDouble())) MaterialTheme.colorScheme.secondaryContainer
                                            else MaterialTheme.colorScheme.primaryContainer
                                        )
                                        .weight(1 / (11f + zeroMod), false),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceAround
                                ) {
                                    if (lessonNumber > 0) {
                                        Text(
                                            text = "${lessonNumber - zeroMod}",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                        ) {
                            if (state.roomsFiltered.isNotEmpty()) {
                                state.roomsFiltered
                                    .sortedBy { it.room.name }
                                    .forEach {
                                        var map = it.availability.map { a -> a == null }
                                        if (!show0) map = map.drop(1)
                                        RoomListRecord(
                                            name = it.room.name,
                                            available = map
                                        )
                                    }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
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
                                        Icon(imageVector = Icons.Default.SearchOff, contentDescription = null)
                                        Text(text = stringResource(id = R.string.search_noResultsFound))
                                    }
                                }
                            }
                            state.roomsFiltered
                                .sortedBy { it.room.name }
                                .forEach {
                                    var map = it.availability.map { a -> a == null }
                                    if (!show0) map = map.drop(1)
                                    RoomListRecord(
                                        name = it.room.name,
                                        available = map
                                    )
                                }

                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
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
    FindAvailableRoomScreenContent(
        state = RoomSearchState(
            currentSchool = School.generateRandomSchools(1).first(),
            loading = false,
        ),
        onBackClicked = {},
    )
}

@Composable
private fun RoomListRecord(
    name: String,
    available: List<Boolean>
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .weight(1 / 12f, false),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.rotate(90f)
            )
        }
        available.forEach { available ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (available) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error)
                    .weight(1 / 12f, false),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
            }
        }
    }
}

@Preview
@Composable
private fun RoomListRecordPreview() {
    RoomListRecord(
        name = "220",
        available = listOf(
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
        )
    )
}