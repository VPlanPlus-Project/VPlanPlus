package es.jvbabi.vplanplus.feature.home.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.feature.home.feature_search.ui.SearchView
import es.jvbabi.vplanplus.feature.home.feature_search.ui.components.Menu
import es.jvbabi.vplanplus.feature.home.ui.components.Greeting
import es.jvbabi.vplanplus.feature.home.ui.components.LastSyncText

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
        onOpenMenu = viewModel::onMenuOpenedChange
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun HomeScreenContent(
    state: HomeState,
    navBar: @Composable () -> Unit,
    onOpenMenu: (open: Boolean) -> Unit
) {
    Scaffold(
        bottomBar = navBar
    ) { _ ->
        Column(Modifier.fillMaxSize()) {
            SearchView { onOpenMenu(true) }
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())) content@{
                Greeting(
                    modifier = Modifier.padding(8.dp),
                    time = state.time,
                    name = state.currentIdentity?.vppId?.name?.substringBefore(" ")
                )

                LastSyncText(Modifier.padding(start = 8.dp), state.lastSync)
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
        navBar = {}
    )
}