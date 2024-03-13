package es.jvbabi.vplanplus.feature.home.feature_search.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.home.feature_search.ui.components.ProfileIcon
import es.jvbabi.vplanplus.feature.home.feature_search.ui.components.SearchNoResults
import es.jvbabi.vplanplus.feature.home.feature_search.ui.components.SearchPlaceholder
import es.jvbabi.vplanplus.feature.home.feature_search.ui.components.SearchResult

@Composable
fun SearchView(
    viewModel: SearchViewModel = hiltViewModel(),
    onOpenMenu: () -> Unit
) {
    val state = viewModel.state.value
    SearchViewContent(
        state = state,
        onSearchActiveChange = viewModel::onSearchActiveChange,
        onUpdateQuery = viewModel::onQueryChange,
        onOpenMenu = onOpenMenu
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchViewContent(
    state: SearchState,
    onOpenMenu: () -> Unit = {},
    onSearchActiveChange: (expanded: Boolean) -> Unit,
    onUpdateQuery: (query: String) -> Unit
) {
    androidx.compose.material3.SearchBar(
        query = state.query,
        onQueryChange = onUpdateQuery,
        onSearch = onUpdateQuery,
        active = state.expanded,
        modifier = Modifier.fillMaxWidth(),
        onActiveChange = { onSearchActiveChange(it) },
        leadingIcon = {
            IconButton(
                onClick = { onSearchActiveChange(!state.expanded) },
            ) {
                Icon(
                    imageVector = if (state.expanded) Icons.AutoMirrored.Default.ArrowBack else Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.back),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = !state.expanded,
                enter = fadeIn(animationSpec = TweenSpec(200)),
                exit = fadeOut(animationSpec = TweenSpec(200))
            ) {
                ProfileIcon(
                    name = state.identity?.profile?.displayName ?: "?",
                    isSyncing = state.isSyncRunning,
                    showNotificationDot = false,
                    onClicked = onOpenMenu
                )
            }
        },
        placeholder = { Text(stringResource(id = R.string.home_search)) },
    ) {
        if (state.query.isBlank()) {
            SearchPlaceholder(fullyCompatible = state.identity?.school?.fullyCompatible == true)
            return@SearchBar
        }
        if (state.query.isNotBlank() && state.results.isEmpty() && !state.isSearchRunning) {
            SearchNoResults(state.query)
            return@SearchBar
        }

        state.results.filter { it.lessons != null }.forEach { result ->
            SearchResult(searchResult = result, time = state.time)
        }
        Text(text = "Search more...")
        state.results.filter { it.lessons == null }.forEach { result ->
            SearchResult(searchResult = result, time = state.time)
        }
    }
}