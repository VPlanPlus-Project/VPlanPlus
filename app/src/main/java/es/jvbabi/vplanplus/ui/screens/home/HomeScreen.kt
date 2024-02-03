package es.jvbabi.vplanplus.ui.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.home.components.SearchBar
import es.jvbabi.vplanplus.ui.screens.home.components.home.ActiveDayContent
import es.jvbabi.vplanplus.ui.screens.home.components.home.Greeting
import es.jvbabi.vplanplus.ui.screens.home.search.SearchContent
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.HomeState
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.HomeViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import es.jvbabi.vplanplus.ui.preview.Profile as ProfilePreview

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    navBar: @Composable () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var menuOpened by remember { mutableStateOf(false) }
    val context = LocalContext.current

    HomeScreenContent(
        state = state,
        onMenuOpened = {
            menuOpened = true
        },
        onSearchOpened = {
            if (it) viewModel.onSearchOpened()
            else viewModel.onSearchClosed()
        },
        onSearchQueryChanged = {
            viewModel.onSearchQueryUpdate(it)
        },
        onFilterToggle = {
            viewModel.searchToggleFilter(it)
        },
        onFindAvailableRoomClicked = {
            navHostController.navigate(Screen.SearchAvailableRoomScreen.route)
        },
        onSelectSearchResult = { schoolId, type, id ->
            viewModel.selectSearchResult(schoolId, type, id)
        },
        navBar = navBar
    )

    BackHandler(enabled = menuOpened, onBack = {
        if (menuOpened) {
            menuOpened = false
        }
    })

    AnimatedVisibility(
        visible = menuOpened,
        enter = fadeIn(animationSpec = TweenSpec(200)),
        exit = fadeOut(animationSpec = TweenSpec(200))
    ) {
        Menu(
            profiles = state.profiles.map { it.toMenuProfile() },
            selectedProfile = state.activeProfile!!.toMenuProfile(),
            onProfileClicked = {
                menuOpened = false
                viewModel.onProfileSelected(it)
            },
            onCloseClicked = {
                menuOpened = false
            },
            onRefreshClicked = {
                viewModel.getVPlanData(context)
                menuOpened = false
            },
            onRepositoryClicked = {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Julius-Babies/VPlanPlus/")
                )
                startActivity(context, browserIntent, null)
            },
            onWebsiteClicked = {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://vplanplus.jvbabi.es/")
                )
                startActivity(context, browserIntent, null)
            },
            onNewsClicked = {
                navHostController.navigate(Screen.NewsScreen.route)
            },
            onSettingsClicked = {
                navHostController.navigate(Screen.SettingsScreen.route)
            },
            onManageProfilesClicked = {
                navHostController.navigate(Screen.SettingsProfileScreen.route)
            },
            onProfileLongClicked = {
                navHostController.navigate(Screen.SettingsProfileScreen.route + it)
            },
            hasUnreadNews = state.unreadMessages.isNotEmpty(),
        )
    }
}

@Composable
fun HomeScreenContent(
    state: HomeState,
    navBar: @Composable () -> Unit = {},
    onSearchOpened: (Boolean) -> Unit = {},
    onSearchQueryChanged: (String) -> Unit = {},
    onFilterToggle: (SchoolEntityType) -> Unit = {},
    onMenuOpened: () -> Unit = {},
    onFindAvailableRoomClicked: () -> Unit = {},
    onSelectSearchResult: (schoolId: Long, type: SchoolEntityType, id: UUID) -> Unit = { _, _, _ -> }
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            currentProfileName = state.getActiveProfileDisplayName(),
            onMenuOpened = onMenuOpened,
            onSearchActiveChange = { onSearchOpened(it) },
            searchOpen = state.searchOpen,
            searchValue = state.searchQuery,
            onSearchTyping = { onSearchQueryChanged(it) },
            isSyncing = state.syncing,
            showNotificationDot = state.unreadMessages.isNotEmpty()
        ) {
            if (state.searchOpen) {
                SearchContent(
                    state = state,
                    onFindAvailableRoomClicked = { onFindAvailableRoomClicked() },
                    onFilterToggle = { onFilterToggle(it) },
                    time = state.time,
                    onSelectSearchResult = onSelectSearchResult
                )
            }
        }
        Scaffold(
            bottomBar = navBar
        ) { paddingValues ->
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding())
                ) root@{
                    Box(modifier = Modifier.padding(start = 8.dp, top = 8.dp)) greeting@{
                        Greeting(state.time, vppId = state.currentVppId)
                    }

                    val hiddenLessons = state.day?.lessons?.count {
                        !state.activeProfile!!.isDefaultLessonEnabled(it.vpId)
                    } ?: 0

                    if (state.day == null) return@root

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        ActiveDayContent(
                            info = state.day.info,
                            currentTime = state.time,
                            day = state.day,
                            bookings = emptyList(),
                            hiddenLessons,
                            state.lastSync,
                            state.isLoading,
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun HomeScreenPreview() {
    HomeScreenContent(
        HomeState(
            lastSync = LocalDateTime.now(),
            isLoading = true,
            day = Day(
                type = DayType.NORMAL,
                state = DayDataState.DATA,
                date = LocalDate.now(),
                lessons = Lessons.generateLessons(4),
                info = "Test day info"
            ),
            syncing = true,
            activeProfile = ProfilePreview.generateClassProfile()
        ),
        onMenuOpened = {},
    )
}
