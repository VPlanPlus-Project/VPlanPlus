package es.jvbabi.vplanplus.feature.home.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        onChangeInfoExpandState = viewModel::onInfoExpandChange
    )
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun HomeScreenContent(
    state: HomeState,
    navBar: @Composable () -> Unit,
    onOpenMenu: (open: Boolean) -> Unit,
    onChangeInfoExpandState: (Boolean) -> Unit
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

                stickyHeader {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surface,
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(8.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.home_planToday,
                                state.todayDay?.date?.format(DateTimeFormatter.ofPattern("EEE, dd.MM.yyyy"))
                                    ?: ""
                            )
                        )
                    }
                }

                if (state.todayDay?.info != null) {
                    item {
                        CollapsableInfoCard(
                            imageVector = Icons.Default.Info,
                            title = stringResource(id = R.string.home_activeDaySchoolInformation),
                            text = state.todayDay.info,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            isExpanded = state.infoExpanded,
                            onChangeState = onChangeInfoExpandState
                        )
                    }
                }

                items(
                    state
                        .todayDay
                        ?.getFilteredLessons(state.currentIdentity!!.profile!!)
                        ?.groupBy { it.lessonNumber }
                        ?.toList() ?: emptyList()
                ) { (_, lessons) ->
                    LessonCard(
                        lessons = lessons,
                        time = state.time,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        homework = state.userHomework
                    )
                }

                stickyHeader {
                    Text(text = "Morgen")
                }
                items((1..50).toList()) { Text(text = "Item $it") }

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
        onChangeInfoExpandState = {}
    )
}