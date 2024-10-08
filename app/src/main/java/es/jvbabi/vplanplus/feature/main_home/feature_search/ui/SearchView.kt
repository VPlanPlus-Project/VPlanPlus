package es.jvbabi.vplanplus.feature.main_home.feature_search.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.ChangeDate
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.SearchNoResults
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.SearchPlaceholder
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.SearchSearching
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.result.SearchResult
import es.jvbabi.vplanplus.feature.main_home.ui.Collapsable
import es.jvbabi.vplanplus.ui.screens.Screen
import java.time.LocalDate

@Composable
fun SearchView(
    navHostController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    SearchViewContent(
        state = state,
        onBack = { if (navHostController.currentDestination?.route?.startsWith(Screen.SearchScreen.route) == true) navHostController.popBackStack() },
        onQueryChange = viewModel::onQueryChange,
        onFindAvailableRoomClicked = { navHostController.navigate(Screen.SearchAvailableRoomScreen.route) },
        onSetDate = viewModel::onSetDate
    )
}

@Composable
fun SearchViewContent(
    state: SearchState,
    onBack: () -> Unit = {},
    onQueryChange: (query: String) -> Unit = {},
    onFindAvailableRoomClicked: () -> Unit = {},
    onSetDate: (date: LocalDate?) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            keyboardController?.show()
                        }
                    },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text(stringResource(id = R.string.home_search)) },
                leadingIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(imageVector = Icons.Outlined.Clear, contentDescription = null)
                    }
                }
            )
            HorizontalDivider(Modifier.padding(top = 2.dp, bottom = 4.dp))
            AssistChip(
                onClick = onFindAvailableRoomClicked,
                label = { Text(text = stringResource(id = R.string.search_searchAvailableRoom)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null)
                },
                modifier = Modifier.padding(start = 8.dp)
            )
            Collapsable(expand = state.query.isNotBlank()) {
                ChangeDate(
                    selectedDate = state.selectedDate,
                    onSetDate = onSetDate
                )
            }

            if (state.query.isBlank()) {
                SearchPlaceholder(fullyCompatible = state.currentProfile?.getSchool()?.fullyCompatible ?: true)
                return@Column
            }
            if (state.query.isNotBlank() && state.results.isEmpty() && !state.isSearchRunning) {
                SearchNoResults(state.query)
                return@Column
            }

            if (state.isSearchRunning) {
                SearchSearching()
                return@Column
            }

            LazyColumn(Modifier.fillMaxSize()) {
                item {
                    val detailedResults = state.results.filter { it.lessons != null }
                    detailedResults.forEachIndexed { i, result ->
                        SearchResult(searchResult = result, time = state.time)
                        if (i != detailedResults.lastIndex) HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    }
                    if (state.results.any { it.lessons == null }) {
                        Text(
                            text = stringResource(id = R.string.search_moreResults),
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    val otherResults = state.results.filter { it.lessons == null }
                    otherResults.forEachIndexed { i, result ->
                        SearchResult(searchResult = result, time = state.time)
                        if (i != otherResults.lastIndex) HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (e: IllegalStateException) {
            Log.w("SearchView", "Focus request failed")
        }
    }
}

@Preview
@Composable
private fun SearchViewPreview() {
    SearchViewContent(
        state = SearchState(
            query = "Test"
        )
    )
}