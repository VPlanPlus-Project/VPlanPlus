package es.jvbabi.vplanplus.feature.home_screen_v2.ui.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.ProfileIcon
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.SearchPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearch(
    identity: Identity,
    isExpanded: Boolean,
    isSyncRunning: Boolean,
    searchQuery: String,
    onChangeOpenCloseState: (to: Boolean) -> Unit,
    onUpdateQuery: (query: String) -> Unit,
    onOpenMenu: () -> Unit,
    onFindAvailableRoomClicked: () -> Unit,
) {
    val openModifier = animateFloatAsState(
        targetValue = if (isExpanded) 0f else 1f,
        animationSpec = tween(250),
        label = "search expansion"
    ).value
    SearchBar(
        query = searchQuery,
        onQueryChange = onUpdateQuery,
        onSearch = onUpdateQuery,
        active = isExpanded,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (8 * openModifier).dp),
        onActiveChange = onChangeOpenCloseState,
        leadingIcon = {
            IconButton(
                onClick = { onChangeOpenCloseState(!isExpanded) },
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.AutoMirrored.Default.ArrowBack else Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.back),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = !isExpanded,
                enter = fadeIn(animationSpec = TweenSpec(200)),
                exit = fadeOut(animationSpec = TweenSpec(200))
            ) {
                ProfileIcon(
                    name = identity.profile?.displayName ?: "?",
                    isSyncing = isSyncRunning,
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
        if (searchQuery.isBlank()) {
            SearchPlaceholder(fullyCompatible = identity.school?.fullyCompatible == true)
            return@SearchBar
        }
        /*if (searchQuery.isNotBlank() && state.results.isEmpty() && !state.isSearchRunning) {
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
        }*/
    }
}