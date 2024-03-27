package es.jvbabi.vplanplus.feature.home_screen_v2.feature_search.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.home_screen_v2.feature_search.ui.components.ProfileIcon
import es.jvbabi.vplanplus.feature.home_screen_v2.feature_search.ui.components.SearchNoResults
import es.jvbabi.vplanplus.feature.home_screen_v2.feature_search.ui.components.SearchPlaceholder
import es.jvbabi.vplanplus.feature.home_screen_v2.feature_search.ui.components.SearchResult
import es.jvbabi.vplanplus.feature.home_screen_v2.feature_search.ui.components.SearchSearching

@Composable
fun SearchView(
    viewModel: SearchViewModel = hiltViewModel(),
    onOpenMenu: () -> Unit,
    onFindAvailableRoomClicked: () -> Unit
) {
    val state = viewModel.state.value
    SearchViewContent(
        state = state,
        onSearchActiveChange = viewModel::onSearchActiveChange,
        onUpdateQuery = viewModel::onQueryChange,
        onOpenMenu = onOpenMenu,
        onFindAvailableRoomClicked = onFindAvailableRoomClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchViewContent(
    state: SearchState,
    onOpenMenu: () -> Unit = {},
    onSearchActiveChange: (expanded: Boolean) -> Unit,
    onUpdateQuery: (query: String) -> Unit,
    onFindAvailableRoomClicked: () -> Unit
) {
    val openModifier = animateFloatAsState(
        targetValue = if (state.expanded) 0f else 1f,
        animationSpec = tween(250),
        label = "search expansion"
    ).value
    androidx.compose.material3.SearchBar(
        query = state.query,
        onQueryChange = onUpdateQuery,
        onSearch = onUpdateQuery,
        active = state.expanded,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (8 * openModifier).dp),
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
        AssistChip(
            onClick = onFindAvailableRoomClicked,
            label = { Text(text = stringResource(id = R.string.search_searchAvailableRoom)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null)
            },
            modifier = Modifier.padding(start = 8.dp)
        )
        if (state.query.isBlank()) {
            SearchPlaceholder(fullyCompatible = state.identity?.school?.fullyCompatible == true)
            return@SearchBar
        }
        if (state.query.isNotBlank() && state.results.isEmpty() && !state.isSearchRunning) {
            SearchNoResults(state.query)
            return@SearchBar
        }

        if (state.isSearchRunning) {
            SearchSearching()
        }

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