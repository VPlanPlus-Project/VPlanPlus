package es.jvbabi.vplanplus.ui.screens.home.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoorBack
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.home.components.SearchBar

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
        onFilterToggle = { searchViewModel.toggleFilter(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    state: SearchState,
    onSearchTyping: (String) -> Unit,
    onSearchClosed: () -> Unit,
    onFilterToggle: (FilterType) -> Unit = {}
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
            onSearchTyping = { onSearchTyping(it) }
        )
        val chipScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .horizontalScroll(state = chipScrollState)
                .padding(8.dp)
        ) {
            AssistChip(
                onClick = {},
                label = { Text(text = stringResource(id = R.string.search_searchAvailableRoom)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null)
                }
            )
            VerticalDivider(
                modifier = Modifier
                    .height(48.dp)
                    .padding(horizontal = 8.dp)
            )
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
            FilterChip(
                selected = state.filter[FilterType.PROFILE]!!,
                onClick = { onFilterToggle(FilterType.PROFILE) },
                label = { Text(text = stringResource(id = R.string.search_profileFilter)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.ManageAccounts, contentDescription = null)
                },
                modifier = paddingModifier
            )
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