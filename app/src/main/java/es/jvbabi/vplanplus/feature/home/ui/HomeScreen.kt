package es.jvbabi.vplanplus.feature.home.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.home.feature_search.ui.SearchView
import es.jvbabi.vplanplus.feature.home.feature_search.ui.components.Menu
import es.jvbabi.vplanplus.feature.home.ui.components.Greeting
import es.jvbabi.vplanplus.feature.home.ui.components.LastSyncText
import es.jvbabi.vplanplus.feature.home.ui.components.LessonCard
import es.jvbabi.vplanplus.feature.home.ui.components.customStickyHeader
import es.jvbabi.vplanplus.ui.common.CollapsableInfoCard
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    navBar: @Composable () -> Unit
) {
    val state = viewModel.state.value

    HomeScreenContent(
        state = state,
        navBar = navBar,
        onOpenMenu = viewModel::onMenuOpenedChange,
        onChangeInfoExpandState = viewModel::onInfoExpandChange,
        onToggleTodayLessonExpanded = viewModel::onTodayLessonExpandedToggle
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun HomeScreenContent(
    state: HomeState,
    navBar: @Composable () -> Unit,
    onOpenMenu: (open: Boolean) -> Unit,
    onChangeInfoExpandState: (Boolean) -> Unit,
    onToggleTodayLessonExpanded: () -> Unit
) {
    Scaffold(
        bottomBar = navBar
    ) { paddingValues ->
        Column(
            Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .fillMaxSize()
        ) {
            SearchView { onOpenMenu(true) }
            LazyColumn content@{
                item {
                    Greeting(
                        modifier = Modifier.padding(8.dp),
                        time = state.time,
                        name = state.currentIdentity?.vppId?.name?.substringBefore(" ")
                    )
                }

                item { Text(text = state.time.toString()) }

                customStickyHeader(Modifier.clickable { onToggleTodayLessonExpanded() }) {
                    Row(
                        modifier =  Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val rotation = animateFloatAsState(
                            targetValue = if (state.todayLessonExpanded) 180f else 0f,
                            label = "Expand Card"
                        )
                        Text(
                            text = stringResource(
                                id = R.string.home_planToday,
                                state.todayDay?.date?.format(DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy"))
                                    ?: ""
                            )
                        )
                        IconButton(onClick = onToggleTodayLessonExpanded) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                modifier = Modifier.rotate(rotation.value)
                            )
                        }
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = state.todayLessonExpanded,
                        enter = expandVertically(tween(300)),
                        exit = shrinkVertically(tween(300))
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            if (state.todayDay?.info != null) CollapsableInfoCard(
                                imageVector = Icons.Default.Info,
                                title = stringResource(id = R.string.home_activeDaySchoolInformation),
                                text = state.todayDay.info,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                isExpanded = state.infoExpanded,
                                onChangeState = onChangeInfoExpandState
                            )
                            state
                                .todayDay
                                ?.getFilteredLessons(state.currentIdentity!!.profile!!)
                                ?.groupBy { it.lessonNumber }
                                ?.toList()
                                ?.forEach { (_, lessons) ->
                                    LessonCard(
                                        lessons = lessons,
                                        time = state.time,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        homework = state.userHomework
                                    )
                                }
                        }
                    }
                }

                item { LastSyncText(Modifier.padding(start = 8.dp), state.lastSync) }
            }
        }
    }

    if (state.currentIdentity != null) Menu(
        isVisible = state.menuOpened,
        profiles = state.profiles,
        hasUnreadNews = false,
        selectedProfile = state.currentIdentity.profile!!,
        onCloseMenu = { onOpenMenu(false) }
    )
}

@Composable
@Preview(showBackground = true)
private fun HomeScreenPreview() {
    HomeScreenContent(
        state = HomeState(),
        onOpenMenu = {},
        navBar = {},
        onChangeInfoExpandState = {},
        onToggleTodayLessonExpanded = {}
    )
}