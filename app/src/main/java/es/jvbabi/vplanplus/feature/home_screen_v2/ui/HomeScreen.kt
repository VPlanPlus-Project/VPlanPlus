package es.jvbabi.vplanplus.feature.home_screen_v2.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.home_screen_v2.ui.components.DateEntry
import es.jvbabi.vplanplus.feature.home_screen_v2.ui.components.Greeting
import es.jvbabi.vplanplus.feature.home_screen_v2.ui.components.HomeSearch
import es.jvbabi.vplanplus.feature.home_screen_v2.ui.preview.navBar
import es.jvbabi.vplanplus.feature.main_home.ui.components.DayView
import es.jvbabi.vplanplus.ui.preview.Profile
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.screens.Screen
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val PAGER_SIZE = 200

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
        onAddHomework = { vpId -> navHostController.navigate(Screen.AddHomeworkScreen.route + "?vpId=$vpId") },
        onBookRoomClicked = { navHostController.navigate(Screen.SearchAvailableRoomScreen.route) },
        onSearchExpandStateChanges = homeViewModel::setSearchState,
        onSetSelectedDate = homeViewModel::setSelectedDate,
        onInfoExpandChange = homeViewModel::onInfoExpandChange
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    navBar: @Composable (expanded: Boolean) -> Unit,
    state: HomeState,
    onSearchExpandStateChanges: (to: Boolean) -> Unit = {},
    onSetSelectedDate: (date: LocalDate) -> Unit = {},
    onInfoExpandChange: (to: Boolean) -> Unit = {},
    onAddHomework: (vpId: Long?) -> Unit,
    onBookRoomClicked: () -> Unit
) {
    if (state.currentIdentity == null) return

    val datePagerState = rememberPagerState(pageCount = { PAGER_SIZE })
    val contentPagerState = rememberPagerState(pageCount = { PAGER_SIZE })

    LaunchedEffect(key1 = state.selectedDate) {
        val difference = LocalDate.now().until(state.selectedDate).days
        contentPagerState.animateScrollToPage(difference)
    }

    LaunchedEffect(key1 = contentPagerState.targetPage) {
        val date = LocalDate.now().plusDays(contentPagerState.targetPage.toLong())
        onSetSelectedDate(date)
    }

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
        Column(Modifier.padding(paddingValues)) {
            Greeting(
                time = state.currentTime,
                name = state.currentIdentity.vppId?.name,
                modifier = Modifier.padding(start = 8.dp)
            )

            Box {
                HorizontalPager(
                    state = datePagerState,
                    pageSize = PageSize.Fixed(90.dp)
                ) { offset ->
                    val date = LocalDate.now().plusDays(offset.toLong())
                    Row {
                        DateEntry(
                            date = date,
                            homework = 3,
                            isActive = date == state.selectedDate,
                            onClick = { onSetSelectedDate(date) }
                        )
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = datePagerState.settledPage > 7,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val scope = rememberCoroutineScope()
                    Button(
                        modifier = Modifier.shadow(elevation = 4.dp, shape = RoundedCornerShape(50)),
                        onClick = {
                            onSetSelectedDate(LocalDate.now())
                            scope.launch { datePagerState.animateScrollToPage(0) }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                        Text(text = stringResource(id = R.string.home_backToToday))
                    }
                }
            }
            HorizontalDivider()

            HorizontalPager(
                state = contentPagerState,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                val date = LocalDate.now().plusDays(it.toLong())

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val start by rememberSaveable { mutableLongStateOf(System.currentTimeMillis() / 1000) }
                    val timeOffset = 1
                    AnimatedVisibility(visible = state.days[date] == null && start + timeOffset < System.currentTimeMillis() / 1000) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            LinearProgressIndicator(Modifier.fillMaxWidth(.5f))
                            Text(
                                text = stringResource(id = R.string.home_longerThanExpected),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
                            )
                        }
                    }

                    val animationDuration = 300
                    AnimatedVisibility(
                        modifier = Modifier.fillMaxSize(),
                        visible = state.days[date] != null,
                        enter = fadeIn(animationSpec = tween(animationDuration)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(animationDuration)),
                        exit = fadeOut(animationSpec = tween(animationDuration))
                    ) {
                        Column(
                            Modifier
                                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.home_planTitle,
                                    date.format(DateTimeFormatter.ofPattern("EEEE"))
                                ),
                                style = MaterialTheme.typography.displaySmall,
                            )
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("d. MMMM")),
                                style = MaterialTheme.typography.labelMedium,
                            )
                            DayView(
                                day = state.days[date]!!,
                                currentTime = state.currentTime,
                                showCountdown = date == state.selectedDate,
                                isInfoExpanded = if (date == LocalDate.now()) state.infoExpanded else null,
                                currentIdentity = state.currentIdentity,
                                bookings = state.bookings,
                                homework = state.homework,
                                onChangeInfoExpandState = onInfoExpandChange,
                                onAddHomework = onAddHomework,
                                onBookRoomClicked = onBookRoomClicked
                            )
                        }
                    }
                }
            }
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
        ),
        onAddHomework = {},
        onBookRoomClicked = {}
    )
}