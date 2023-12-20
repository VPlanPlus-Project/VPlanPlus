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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.preview.School

@Composable
fun FindAvailableRoomScreen(
    navController: NavHostController,
    roomSearchViewModel: RoomSearchViewModel = hiltViewModel()
) {
    val state = roomSearchViewModel.state.value

    FindAvailableRoomScreenContent(
        state = state,
        onBackClicked = { navController.popBackStack() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindAvailableRoomScreenContent(
    state: RoomSearchState,
    onBackClicked: () -> Unit,
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.search_searchAvailableRoom)) },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        BackIcon()
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = state.currentSchool?.name?: stringResource(id = R.string.loadingData))
                Text(text = stringResource(id = R.string.searchAvailableRoom_text))
                Box(modifier = Modifier.fillMaxSize()) {
                    if (!state.loading) Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            repeat(12) { lessonNumber ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .padding(horizontal = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .weight(1 / 12f, false),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceAround
                                ) {
                                    if (lessonNumber > 0) {
                                        Text(
                                            text = "${lessonNumber - 1}",
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            state.rooms.forEach {
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
                                            text = it.key,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            style = MaterialTheme.typography.labelSmall,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.rotate(90f)
                                        )
                                    }
                                    it.value.forEach { available ->
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
        state = RoomSearchState(currentSchool = School.generateRandomSchools(1).first(), loading = true),
        onBackClicked = {},
    )
}