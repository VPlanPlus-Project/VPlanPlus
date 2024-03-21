package es.jvbabi.vplanplus.feature.main_home.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.SearchView
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.Menu
import es.jvbabi.vplanplus.feature.main_home.ui.components.DayView
import es.jvbabi.vplanplus.feature.main_home.ui.components.Greeting
import es.jvbabi.vplanplus.feature.main_home.ui.components.LastSyncText
import es.jvbabi.vplanplus.feature.main_home.ui.components.ToggleButtons
import es.jvbabi.vplanplus.feature.main_home.ui.components.VersionHintsInformation
import es.jvbabi.vplanplus.feature.main_home.ui.components.views.NoDataNextDay
import es.jvbabi.vplanplus.ui.common.keyboardAsState
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.screens.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    navBar: @Composable () -> Unit
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(state.goToNextDay) {
        if (state.goToNextDay == null) return@LaunchedEffect
        pagerState.animateScrollToPage(if (state.goToNextDay) 0 else 1)
    }

    HomeScreenContent(
        state = state,
        navBar = navBar,
        pagerState = pagerState,
        onOpenMenu = viewModel::onMenuOpenedChange,
        onChangeInfoExpandState = viewModel::onInfoExpandChange,
        onProfileClicked = { viewModel.onMenuOpenedChange(false); viewModel.switchProfile(it) },
        onProfileLongClicked = {
            viewModel.onMenuOpenedChange(false); navHostController.navigate(
            Screen.SettingsProfileScreen.route + it.id
        )
        },
        onManageProfilesClicked = {
            viewModel.onMenuOpenedChange(false); navHostController.navigate(
            Screen.SettingsProfileScreen.route
        )
        },
        onNewsClicked = { viewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.NewsScreen.route) },
        onSettingsClicked = { viewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.SettingsScreen.route) },
        onPrivacyPolicyClicked = {
            openLink(
                context,
                "https://github.com/VPlanPlus-Project/VPlanPlus/blob/main/PRIVACY-POLICY.md"
            )
        },
        onRepositoryClicked = {
            openLink(
                context,
                "https://github.com/VPlanPlus-Project/VPlanPlus"
            )
        },
        onRefreshClicked = { viewModel.onMenuOpenedChange(false); viewModel.onRefreshClicked(context) },
        onAddHomework = { vpId -> navHostController.navigate(Screen.AddHomeworkScreen.route + "?vpId=$vpId") },
        onBookRoomClicked = { navHostController.navigate(Screen.SearchAvailableRoomScreen.route) },
        onVersionHintsClosed = viewModel::hideVersionHintsDialog,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun HomeScreenContent(
    state: HomeState,
    pagerState: PagerState,
    navBar: @Composable () -> Unit,
    onOpenMenu: (open: Boolean) -> Unit,
    onChangeInfoExpandState: (Boolean) -> Unit,
    onProfileClicked: (Profile) -> Unit = {},
    onProfileLongClicked: (Profile) -> Unit = {},
    onManageProfilesClicked: () -> Unit = {},
    onNewsClicked: () -> Unit = {},
    onSettingsClicked: () -> Unit = {},
    onPrivacyPolicyClicked: () -> Unit = {},
    onRepositoryClicked: () -> Unit = {},
    onRefreshClicked: () -> Unit = {},
    onAddHomework: (vpId: Long?) -> Unit = {},
    onBookRoomClicked: () -> Unit = {},
    onVersionHintsClosed: (untilNextVersion: Boolean) -> Unit = {}
) {
    if (state.isVersionHintsDialogOpen) VersionHintsInformation(
        currentVersion = state.currentVersion,
        hints = state.versionHints,
        onCloseUntilNextTime = { onVersionHintsClosed(false) },
        onCloseUntilNextVersion = { onVersionHintsClosed(true) }
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = !keyboardAsState().value,
                enter = expandVertically(tween(250)),
                exit = shrinkVertically(tween(250))
            ) {
                navBar()
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .fillMaxSize()
        ) {
            SearchView(
                onOpenMenu = { onOpenMenu(true) },
                onFindAvailableRoomClicked = onBookRoomClicked
            )

            if (state.currentIdentity == null) return@Scaffold

            LazyColumn content@{
                item {
                    Greeting(
                        modifier = Modifier.padding(8.dp),
                        time = state.time,
                        name = state.currentIdentity.vppId?.name
                    )
                }
                item {
                    LastSyncText(
                        lastSync = state.lastSync,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                stickyHeader {
                    val scope = rememberCoroutineScope()
                    ToggleButtons(
                        isTodaySelected = pagerState.currentPage == 0,
                        nextDate = state.nextDay?.date,
                        onTodaySelect = { scope.launch { pagerState.animateScrollToPage(0) } },
                        onNextDaySelect = { scope.launch { pagerState.animateScrollToPage(1) } }
                    )
                }

                item content@{
                    var biggestHeight by remember { mutableStateOf(0.dp) }
                    val density = LocalDensity.current
                    HorizontalPager(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        state = pagerState
                    ) { i ->
                        if (i == 0) Column(
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = biggestHeight)
                                .onSizeChanged {
                                    density.run {
                                        biggestHeight = maxOf(biggestHeight, it.height.toDp())
                                    }
                                }) today@{
                            DayView(
                                day = state.todayDay,
                                currentTime = state.time,
                                showCountdown = true,
                                isInfoExpanded = state.infoExpanded,
                                currentIdentity = state.currentIdentity,
                                bookings = state.bookings,
                                homework = state.userHomework,
                                onChangeInfoExpandState = onChangeInfoExpandState,
                                onAddHomework = onAddHomework,
                                onBookRoomClicked = onBookRoomClicked
                            )
                        }
                        else Column(Modifier
                            .fillMaxWidth()
                            .heightIn(min = biggestHeight)
                            .onSizeChanged {
                                density.run {
                                    biggestHeight = maxOf(biggestHeight, it.height.toDp())
                                }
                            }
                        ) nextDay@{
                            if (state.nextDay != null) {
                                if (state.nextDay.state == DayDataState.NO_DATA || state.nextDay.type != DayType.NORMAL) {
                                    NoDataNextDay(date = state.nextDay.date)
                                    return@nextDay
                                }
                                DayView(
                                    day = state.nextDay,
                                    currentTime = state.time,
                                    showCountdown = false,
                                    isInfoExpanded = true,
                                    currentIdentity = state.currentIdentity,
                                    bookings = state.bookings,
                                    homework = state.userHomework,
                                    onChangeInfoExpandState = {},
                                    onAddHomework = {},
                                    onBookRoomClicked = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.currentIdentity != null) Menu(
        isVisible = state.menuOpened,
        profiles = state.profiles,
        hasUnreadNews = false,
        selectedProfile = state.currentIdentity.profile!!,
        onCloseMenu = { onOpenMenu(false) },
        onProfileClicked = onProfileClicked,
        onManageProfilesClicked = onManageProfilesClicked,
        onProfileLongClicked = onProfileLongClicked,
        onNewsClicked = onNewsClicked,
        onSettingsClicked = onSettingsClicked,
        onPrivacyPolicyClicked = onPrivacyPolicyClicked,
        onRepositoryClicked = onRepositoryClicked,
        onRefreshClicked = onRefreshClicked
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview(showBackground = true)
private fun HomeScreenPreview() {
    HomeScreenContent(
        state = HomeState(),
        onOpenMenu = {},
        navBar = {},
        onChangeInfoExpandState = {},
        pagerState = rememberPagerState { 2 }
    )
}