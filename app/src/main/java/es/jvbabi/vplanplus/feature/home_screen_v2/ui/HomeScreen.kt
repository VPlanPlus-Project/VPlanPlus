package es.jvbabi.vplanplus.feature.home_screen_v2.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.home_screen_v2.ui.components.HomeSearch
import es.jvbabi.vplanplus.feature.home_screen_v2.ui.preview.navBar
import es.jvbabi.vplanplus.ui.preview.Profile
import es.jvbabi.vplanplus.ui.preview.School

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    navBar: @Composable (expanded: Boolean) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val state = homeViewModel.state

    HomeScreenContent(
        navBar = navBar,
        state = state,
        onSearchExpandStateChanges = homeViewModel::setSearchState
    )
}

@Composable
fun HomeScreenContent(
    navBar: @Composable (expanded: Boolean) -> Unit,
    state: HomeState,
    onSearchExpandStateChanges: (to: Boolean) -> Unit = {}
) {
    if (state.currentIdentity == null) return
    Scaffold(
        topBar = {
            HomeSearch(
                identity = state.currentIdentity,
                isExpanded = state.isSearchExpanded,
                isSyncRunning = false,
                searchQuery = "",
                onChangeOpenCloseState = onSearchExpandStateChanges,
                onUpdateQuery = {},
                onOpenMenu = { /*TODO*/ },
                onFindAvailableRoomClicked = {}
            )
        },
        bottomBar = { navBar(!state.isSearchExpanded) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        LazyColumn(Modifier.padding(paddingValues)) {

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val school = School.generateRandomSchools(1).first()
    val profile = Profile.generateClassProfile()
    HomeScreenContent(
        navBar = navBar,
        state = HomeState(
            currentIdentity = Identity(
                school = school,
                profile = profile
            )
        )
    )
}