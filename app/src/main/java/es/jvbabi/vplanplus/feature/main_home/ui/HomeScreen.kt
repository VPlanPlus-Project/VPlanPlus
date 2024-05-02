package es.jvbabi.vplanplus.feature.main_home.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.SearchView
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.Menu
import es.jvbabi.vplanplus.feature.main_home.ui.components.DayPager
import es.jvbabi.vplanplus.feature.main_home.ui.components.DayView
import es.jvbabi.vplanplus.feature.main_home.ui.components.Greeting
import es.jvbabi.vplanplus.feature.main_home.ui.components.ImportantHeader
import es.jvbabi.vplanplus.feature.main_home.ui.components.LastSyncText
import es.jvbabi.vplanplus.feature.main_home.ui.components.PlanHeader
import es.jvbabi.vplanplus.feature.main_home.ui.components.VersionHintsInformation
import es.jvbabi.vplanplus.feature.main_home.ui.components.cards.MissingVppIdLinkToProfileCard
import es.jvbabi.vplanplus.feature.main_home.ui.components.views.NoData
import es.jvbabi.vplanplus.feature.main_home.ui.preview.navBar
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.onLogin
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.keyboardAsState
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.withDayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

const val PAGER_SIZE = 200

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    navBar: @Composable (expanded: Boolean) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
    startDate: LocalDate = LocalDate.now()
) {
    val state = homeViewModel.state
    val context = LocalContext.current

    LaunchedEffect(key1 = startDate) { homeViewModel.setSelectedDate(startDate) }

    HomeScreenContent(
        navBar = navBar,
        state = state,
        onAddHomework = { vpId -> navHostController.navigate(Screen.AddHomeworkScreen.route + "?vpId=$vpId") },
        onBookRoomClicked = { navHostController.navigate(Screen.SearchAvailableRoomScreen.route) },
        onOpenMenu = homeViewModel::onMenuOpenedChange,
        onSetSelectedDate = homeViewModel::setSelectedDate,
        onInfoExpandChange = homeViewModel::onInfoExpandChange,
        onVersionHintsClosed = homeViewModel::hideVersionHintsDialog,

        onSwitchProfile = homeViewModel::switchProfile,
        onManageProfiles = {
            homeViewModel.onMenuOpenedChange(false)
            navHostController.navigate(Screen.SettingsProfileScreen.route)
        },
        onManageProfile = {
            homeViewModel.onMenuOpenedChange(false)
            navHostController.navigate(Screen.SettingsProfileScreen.route + it.id)
        },
        onOpenNews = { homeViewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.NewsScreen.route) },
        onOpenSettings = { homeViewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.SettingsScreen.route) },
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
        onRefreshClicked = { homeViewModel.onMenuOpenedChange(false); homeViewModel.onRefreshClicked(context) },
        onFixVppIdSessionClicked = { onLogin(context, state.server) },
        onFixVppIdLinksClicked = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
        onIgnoreInvalidVppIdSessions = homeViewModel::ignoreInvalidVppIdSessions,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    navBar: @Composable (expanded: Boolean) -> Unit,
    state: HomeState,
    onOpenMenu: (state: Boolean) -> Unit = {},
    onSetSelectedDate: (date: LocalDate) -> Unit = {},
    onInfoExpandChange: (to: Boolean) -> Unit = {},
    onAddHomework: (vpId: Long?) -> Unit,
    onBookRoomClicked: () -> Unit,

    onSwitchProfile: (to: Profile) -> Unit,
    onManageProfiles: () -> Unit = {},
    onManageProfile: (profile: Profile) -> Unit = {},
    onOpenNews: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onPrivacyPolicyClicked: () -> Unit = {},
    onRepositoryClicked: () -> Unit = {},
    onRefreshClicked: () -> Unit = {},

    onFixVppIdSessionClicked: () -> Unit = {},
    onIgnoreInvalidVppIdSessions: () -> Unit = {},
    onFixVppIdLinksClicked: () -> Unit = {},

    onVersionHintsClosed: (untilNextVersion: Boolean) -> Unit = {}
) {
    if (state.currentIdentity == null) return

    if (state.isVersionHintsDialogOpen) VersionHintsInformation(
        currentVersion = state.currentVersion,
        hints = state.versionHints,
        onCloseUntilNextTime = { onVersionHintsClosed(false) },
        onCloseUntilNextVersion = { onVersionHintsClosed(true) }
    )

    val contentPagerState = rememberPagerState(pageCount = { PAGER_SIZE }, initialPage = PAGER_SIZE / 2)

    LaunchedEffect(key1 = state.selectedDate) {
        contentPagerState.animateScrollToPage(page = LocalDate.now().until(state.selectedDate, ChronoUnit.DAYS).toInt() + PAGER_SIZE / 2)
    }

    LaunchedEffect(key1 = contentPagerState.targetPage) {
        val date = LocalDate.now().plusDays(contentPagerState.targetPage.toLong() - PAGER_SIZE / 2)
        onSetSelectedDate(date)
    }

    var topHeight by remember { mutableIntStateOf(0) }
    var topOffset by remember { mutableIntStateOf(0) }
    val lazyListState = rememberLazyListState()

    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y.toInt()
                val newOffset = topOffset + delta
                val previousOffset = topOffset
                topOffset = newOffset.coerceIn(-topHeight, 0)
                val consumed = topOffset - previousOffset
                return Offset(0f, consumed.toFloat())
            }
        }
    }

    LaunchedEffect(key1 = lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            val target = if (topOffset < -topHeight / 2) -topHeight else 0
            topOffset = topOffset.coerceIn(-topHeight, 0)
            if (topOffset != target) {
                topOffset = target
            }
        }
    }

    var shadowAlpha by remember { mutableFloatStateOf(0f) }


    LaunchedEffect(key1 = lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }.collect {
            shadowAlpha = minOf(it + lazyListState.layoutInfo.visibleItemsInfo.subList(0, lazyListState.firstVisibleItemIndex).sumOf { e -> e.size }, 200) / 200f
        }
    }

    Log.d("Shadow", shadowAlpha.toString())

    Scaffold(
        bottomBar = { navBar(!keyboardAsState().value) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .nestedScroll(scrollConnection)
        ) {
            Box(Modifier.zIndex(5f)) {
                SearchView(
                    onOpenMenu = { onOpenMenu(true) },
                    onFindAvailableRoomClicked = onBookRoomClicked
                )
            }

            val context = LocalContext.current
            val offset = animateFloatAsState(
                targetValue = topOffset.toFloat(),
                label = "Header"
            )

            Column(Modifier.fillMaxSize()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .onSizeChanged { topHeight = maxOf(it.height, topHeight) }
                        .offset { IntOffset(0, offset.value.toInt()) }
                        .then(
                            if (topHeight == 0) Modifier else Modifier.height(
                                ((topHeight + offset.value) / context.resources.displayMetrics.density).dp
                            )
                        )
                ) {
                    Greeting(
                        time = state.currentTime,
                        name = state.currentIdentity.profile?.vppId?.name,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    LastSyncText(lastSync = state.lastSync, modifier = Modifier.padding(start = 8.dp))

                    Collapsable(
                        expand = state.hasMissingVppIdToProfileLinks || state.hasInvalidVppIdSession
                    ) { ImportantHeader(Modifier.padding(start = 8.dp)) }
                    Collapsable(expand = state.hasInvalidVppIdSession) {
                        InfoCard(
                            imageVector = Icons.Default.NoAccounts,
                            title = stringResource(id = R.string.home_invalidVppIdSessionTitle),
                            text = stringResource(id = R.string.home_invalidVppIdSessionText),
                            buttonText1 = stringResource(id = R.string.ignore),
                            buttonAction1 = onIgnoreInvalidVppIdSessions,
                            buttonText2 = stringResource(id = R.string.fix),
                            buttonAction2 = onFixVppIdSessionClicked,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    Collapsable(expand = state.hasMissingVppIdToProfileLinks) {
                        MissingVppIdLinkToProfileCard(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            onFixClicked = onFixVppIdLinksClicked
                        )
                    }
                }
                // TODO recommended section
                LazyColumn(
                    state = lazyListState
                ) {
                    stickyHeader {
                        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
                            PlanHeader(Modifier.padding(start = 8.dp))
                            DayPager(
                                selectedDate = state.selectedDate,
                                today = state.currentTime.toLocalDate(),
                                onDateSelected = onSetSelectedDate
                            )
                        }
                        Column(Modifier.fillMaxWidth()) {
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                TextButton(
                                    onClick = {
                                        onSetSelectedDate(state.selectedDate.minusWeeks(1L).withDayOfWeek(1))
                                    },
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(id = R.string.home_calendarWeek, state.selectedDate.minusWeeks(1L).format(DateTimeFormatter.ofPattern("w")).toInt())
                                    )
                                }
                                Collapsable(expand = state.selectedDate != LocalDate.now()) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formatDayDuration(state.selectedDate) + "  $DOT",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Light,
                                                color = Color.Gray
                                            )
                                        )
                                        TextButton(
                                            onClick = { onSetSelectedDate(LocalDate.now()) },
                                            enabled = state.selectedDate != LocalDate.now()
                                        ) { Text(stringResource(id = R.string.back)) }
                                    }
                                }
                                TextButton(
                                    onClick = {
                                        onSetSelectedDate(state.selectedDate.plusWeeks(1L).withDayOfWeek(1))
                                    },
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.home_calendarWeek, state.selectedDate.plusWeeks(1L).format(DateTimeFormatter.ofPattern("w")).toInt())
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                                        contentDescription = null
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(16.dp)
                                    .alpha(shadowAlpha)
                                    .drawWithContent {
                                        drawContent()
                                        drawRect(brush = Brush.verticalGradient(listOf(Color.DarkGray.copy(alpha = .3f), Color.DarkGray.copy(alpha = 0f))), topLeft = Offset(0f, 0f), size = size)
                                    }
                            ) {}
                        }
                    }
                    item {
                        HorizontalPager(
                            state = contentPagerState,
                            verticalAlignment = Alignment.Top,
                            beyondBoundsPageCount = 10
                        ) {
                            val date = LocalDate.now().plusDays(it.toLong() - PAGER_SIZE / 2)

                            val day = state.days[date]

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val start by rememberSaveable { mutableLongStateOf(System.currentTimeMillis() / 1000) }
                                val timeOffset = 1
                                AnimatedVisibility(visible = day == null && start + timeOffset < System.currentTimeMillis() / 1000) {
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
                                    visible = day != null,
                                    enter = fadeIn(animationSpec = tween(animationDuration)) + slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(animationDuration)),
                                    exit = fadeOut(animationSpec = tween(animationDuration))
                                ) dayViewRoot@{
                                    Column(Modifier.fillMaxSize()) {
                                        if (day?.lessons?.size == 0 && day.type == DayType.NORMAL) {
                                            NoData(date)
                                            return@dayViewRoot
                                        }
                                        DayView(
                                            day = day,
                                            currentTime = state.currentTime,
                                            showCountdown = state.currentTime.toLocalDate().isEqual(date),
                                            isInfoExpanded = if (state.currentTime.toLocalDate().isEqual(date)) state.infoExpanded else null,
                                            currentIdentity = state.currentIdentity,
                                            bookings = state.bookings,
                                            homework = state.homework,
                                            onChangeInfoExpandState = onInfoExpandChange,
                                            onAddHomework = onAddHomework,
                                            onBookRoomClicked = onBookRoomClicked,
                                            hideFinishedLessons = state.hideFinishedLessons,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Menu(
        isVisible = state.menuOpened,
        profiles = state.profiles,
        hasUnreadNews = state.hasUnreadNews,
        selectedProfile = state.currentIdentity.profile!!,
        onCloseMenu = { onOpenMenu(false) },
        onProfileClicked = onSwitchProfile,
        onManageProfilesClicked = onManageProfiles,
        onProfileLongClicked = onManageProfile,
        onNewsClicked = onOpenNews,
        onSettingsClicked = onOpenSettings,
        onPrivacyPolicyClicked = onPrivacyPolicyClicked,
        onRepositoryClicked = onRepositoryClicked,
        onRefreshClicked = onRefreshClicked
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val school = School.generateRandomSchools(1).first()
    val profile = ProfilePreview.generateClassProfile()
    HomeScreenContent(
        navBar = navBar,
        state = HomeState(
            currentIdentity = Identity(
                school = school,
                profile = profile
            ),
            menuOpened = true,
            hasUnreadNews = true,
            profiles = listOf(profile)
        ),
        onAddHomework = {},
        onBookRoomClicked = {},
        onOpenMenu = {},
        onSetSelectedDate = {},
        onInfoExpandChange = {},
        onSwitchProfile = {}
    )
}

@Composable
fun Collapsable(modifier: Modifier = Modifier, expand: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        modifier = modifier,
        visible = expand,
        enter = expandVertically(tween(250)),
        exit = shrinkVertically(tween(250))
    ) {
        content()
    }
}

@Composable
private fun formatDayDuration(compareTo: LocalDate): String {
    return DateUtils.localizedRelativeDate(LocalContext.current, compareTo, false) ?: run {
        if (compareTo.isAfter(LocalDate.now())) return stringResource(id = R.string.home_inNDays, LocalDate.now().until(compareTo, ChronoUnit.DAYS))
        else return stringResource(id = R.string.home_NdaysAgo, compareTo.until(LocalDate.now(), ChronoUnit.DAYS))
    }
}