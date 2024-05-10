package es.jvbabi.vplanplus.feature.room_search.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.room_search.ui.components.RoomName
import es.jvbabi.vplanplus.ui.common.BackIcon

@Composable
fun RoomSearch(
    navHostController: NavHostController,
    viewModel: RoomSearchViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    RoomSearchContent(
        onBack = { navHostController.popBackStack() },
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun RoomSearchContent(
    onBack: () -> Unit = {},
    state: RoomSearchState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.searchAvailableRoom_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) { BackIcon() }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
            item {
                val verticalPadding = 4.dp
                LazyRow(Modifier.fillMaxWidth()) {
                    stickyHeader rooms@{
                        Column(verticalArrangement = Arrangement.spacedBy(verticalPadding)) {
                            state.map.forEach room@{ (room, _) ->
                                RoomName(roomName = room.name)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun RoomSearchPreview() {
    RoomSearchContent(state = RoomSearchState())
}