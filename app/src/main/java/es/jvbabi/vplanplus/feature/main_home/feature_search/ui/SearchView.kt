package es.jvbabi.vplanplus.feature.main_home.feature_search.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.ChangeDate
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.ProfileIcon
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.SearchNoResults
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.SearchPlaceholder
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.SearchResult
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.SearchSearching
import java.time.LocalDate

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
        onFindAvailableRoomClicked = onFindAvailableRoomClicked,
        onSetDate = viewModel::onSetDate
    )
}

@Composable
fun SearchViewContent(
    state: SearchState,
    onOpenMenu: () -> Unit = {},
    onSetDate: (date: LocalDate?) -> Unit = {},
    onSearchActiveChange: (expanded: Boolean) -> Unit,
    onUpdateQuery: (query: String) -> Unit,
    onFindAvailableRoomClicked: () -> Unit
) {
    val animationDuration = 250
    val modifier =
        animateFloatAsState(
            targetValue = if (state.expanded) 1f else 0f,
            animationSpec = tween(animationDuration),
            label = "Search Bar Animation"
        )

    BackHandler(state.expanded) {
        onSearchActiveChange(false)
    }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val colorScheme = MaterialTheme.colorScheme

    val transparent = colorScheme.surface.copy(alpha = 0f)
    val color = colorScheme.surface
    val background = Color(
        ColorUtils.blendARGB(
            color.toArgb(),
            transparent.toArgb(),
            1 - modifier.value
        )
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawWithContent {
                drawRect(
                    color = background,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height)
                )
                drawContent()
            }
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))
        Row(
            modifier = Modifier
                .padding(horizontal = (4 * (1 - modifier.value)).dp, vertical = 8.dp)
                .fillMaxWidth()
                .shadow(
                    (8 * (1 - modifier.value)).dp,
                    RoundedCornerShape(percent = (50 * (1 - modifier.value)).toInt())
                )
                .clip(RoundedCornerShape(percent = (50 * (1 - modifier.value)).toInt()))
                .background(MaterialTheme.colorScheme.surface)
                .height(56.dp)
                .clickable { onSearchActiveChange(true) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.padding(start = 4.dp),
                onClick = { focusManager.clearFocus(); onSearchActiveChange(!state.expanded) },
            ) {
                Icon(
                    imageVector = if (state.expanded) Icons.AutoMirrored.Default.ArrowBack else Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.back),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            BasicTextField(
                value = state.query,
                modifier = Modifier
                    .weight(1f, true)
                    .onFocusChanged { if (it.isFocused) onSearchActiveChange(true) },
                onValueChange = { onUpdateQuery(it) },
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                decorationBox = { innerTextField ->
                    Box(
                        Modifier.focusRequester(focusRequester),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (state.query.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.home_search),
                                color = Color.Gray.copy(alpha = 0.5f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.SemiBold
                ),
                cursorBrush = Brush.verticalGradient(colors = listOf(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)), // I don't like this
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
            )
            AnimatedVisibility(visible = !state.expanded) {
                ProfileIcon(
                    modifier = Modifier.padding(end = 6.dp),
                    name = state.identity?.profile?.displayName ?: "?",
                    isSyncing = state.isSyncRunning,
                    showNotificationDot = false,
                    onClicked = onOpenMenu
                )
            }
        }
        AnimatedVisibility(
            modifier = Modifier.background(background),
            visible = state.expanded,
            enter = expandVertically(tween(animationDuration)),
            exit = shrinkVertically(tween(animationDuration))
        ) {
            Column(Modifier.fillMaxSize()) {
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
                    return@AnimatedVisibility
                }
                if (state.query.isNotBlank() && state.results.isEmpty() && !state.isSearchRunning) {
                    SearchNoResults(state.query)
                    return@AnimatedVisibility
                }

                ChangeDate(
                    selectedDate = state.selectedDate,
                    onSetDate = onSetDate
                )

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
    }
}