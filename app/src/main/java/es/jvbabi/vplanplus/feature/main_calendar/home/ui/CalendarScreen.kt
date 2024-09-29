package es.jvbabi.vplanplus.feature.main_calendar.home.ui

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.CalendarDateHead
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.CalendarFloatingActionButton
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.DateBar
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.DayInfoCard
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.FullMonthPager
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.ScrollableDateSelector
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.TopBar
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.TypeFilters
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.homework.HomeworkSection
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.lessons.LessonsSection
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.grades.GradeRecord
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheet
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheetInitialValues
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

const val MONTH_PAGER_SIZE = 12 * 10
const val WEEK_PAGER_SIZE = 52
const val CONTENT_PAGER_SIZE = 365 * 2
const val WEEK_HEADER_HEIGHT_DP = 16
const val MONTH_HEADER_HEIGHT_DP = 48
const val HEADER_STATIC_HEIGHT_DP = WEEK_HEADER_HEIGHT_DP + MONTH_HEADER_HEIGHT_DP

@Composable
fun CalendarScreen(
    navHostController: NavHostController,
    navBar: @Composable (isVisible: Boolean) -> Unit,
    navRail: @Composable (isVisible: Boolean, fab: @Composable () -> Unit) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val context = LocalContext.current

    CalendarScreenContent(
        onBack = { navHostController.navigateUp() },
        onOpenHomeworkScreen = { homeworkId -> navHostController.navigate(Screen.HomeworkDetailScreen.route + "/$homeworkId") },
        onTimetableInfoBannerClicked = {
            openLink(
                context,
                "https://vplan.plus/faq/stundenplan-filter-funktioniert-nicht"
            )
        },
        navBar = navBar,
        navRail = navRail,
        doAction = viewModel::doAction,
        state = state
    )
}

@Composable
private fun CalendarScreenContent(
    onBack: () -> Unit = {},
    onOpenHomeworkScreen: (homeworkId: Int) -> Unit = {},
    onTimetableInfoBannerClicked: () -> Unit = {},
    doAction: (action: CalendarViewAction) -> Unit = {},
    navBar: @Composable (Boolean) -> Unit = {},
    navRail: @Composable (Boolean, fab: @Composable () -> Unit) -> Unit = { _, _ ->},
    state: CalendarViewState
) {

    var addHomeworkSheetInitialValues by rememberSaveable<MutableState<AddHomeworkSheetInitialValues?>> {
        mutableStateOf(
            null
        )
    }
    if (addHomeworkSheetInitialValues != null) {
        AddHomeworkSheet(
            onClose = { addHomeworkSheetInitialValues = null },
            initialValues = addHomeworkSheetInitialValues ?: AddHomeworkSheetInitialValues()
        )
    }

    val localDensity = LocalDensity.current
    val localConfiguration = LocalConfiguration.current
    val headerScrollState = rememberScrollState()
    val contentPagerState =
        rememberPagerState(initialPage = CONTENT_PAGER_SIZE / 2) { CONTENT_PAGER_SIZE }

    val calendarSelectHeightSmall by remember { mutableFloatStateOf(with(localDensity) { (70 + HEADER_STATIC_HEIGHT_DP).dp.toPx() }) }
    var calendarSelectHeightMedium by remember { mutableFloatStateOf(with(localDensity) { (70 + HEADER_STATIC_HEIGHT_DP).dp.toPx() }) }
    var calendarSelectHeightLarge by remember { mutableFloatStateOf(with(localDensity) { (70 + HEADER_STATIC_HEIGHT_DP).dp.toPx() }) }
    var currentHeadSize by remember { mutableFloatStateOf(calendarSelectHeightSmall) }
    var closest by remember(currentHeadSize) {
        mutableFloatStateOf(
            when (currentHeadSize) {
                in calendarSelectHeightSmall..listOf(
                    calendarSelectHeightSmall,
                    calendarSelectHeightMedium
                ).average().toFloat() -> {
                    calendarSelectHeightSmall
                }

                in listOf(calendarSelectHeightSmall, calendarSelectHeightMedium).average()..listOf(
                    calendarSelectHeightMedium,
                    calendarSelectHeightLarge
                ).average() -> calendarSelectHeightMedium

                else -> calendarSelectHeightLarge
            }
        )
    }
    var isScrolling by remember { mutableStateOf(false) }
    var isAnimating by remember { mutableStateOf(false) }
    val animateCurrentHeadSize by animateFloatAsState(
        targetValue = if (isScrolling) currentHeadSize else closest.let { if (it == 0f) calendarSelectHeightSmall else it },
        finishedListener = {
            isAnimating = false
            if (!isScrolling) currentHeadSize = closest
        }, label = "headerSizeAnimation"
    )

    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val useHeadSize = if (isAnimating) animateCurrentHeadSize else currentHeadSize
                val isHeaderScrolling = headerScrollState.isScrollInProgress
                val delta = available.y
                if (isHeaderScrolling || (delta > 0)) {
                    // scroll head
                    currentHeadSize = (currentHeadSize + delta).coerceIn(
                        calendarSelectHeightSmall,
                        calendarSelectHeightLarge
                    )
                    return Offset(0f, delta)
                } else if (delta < 0 && useHeadSize > calendarSelectHeightSmall) {
                    // scroll head
                    currentHeadSize = (currentHeadSize + delta).coerceIn(
                        calendarSelectHeightSmall,
                        calendarSelectHeightLarge
                    )
                    return Offset(0f, delta)
                }
                return super.onPreScroll(available, source)
            }
        }
    }

    LaunchedEffect(
        key1 = headerScrollState.isScrollInProgress,
    ) {
        isScrolling = headerScrollState.isScrollInProgress
        if (!isScrolling && currentHeadSize != closest && closest != 0f) isAnimating = true
    }

    val displayHeadSize = if (!isScrolling) animateCurrentHeadSize else currentHeadSize

    Scaffold(
        topBar = { if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) TopBar(onBack = onBack, selectDate = { doAction(CalendarViewAction.SelectDate(it, DateSelectCause.CALENDAR_CLICK)) }) },
        floatingActionButton = {
            CalendarFloatingActionButton(
                isVisible = closest != calendarSelectHeightLarge,
                onClick = {
                    addHomeworkSheetInitialValues =
                        AddHomeworkSheetInitialValues(until = state.selectedDate)
                })
        },
        bottomBar = { navBar(localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT && addHomeworkSheetInitialValues == null) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->

        var firstVisibleDate by remember { mutableStateOf(LocalDate.now().atStartOfWeek()) }
        if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val transitionProgress = when (displayHeadSize) {
                in calendarSelectHeightSmall..calendarSelectHeightMedium -> (displayHeadSize - calendarSelectHeightSmall) / (calendarSelectHeightMedium - calendarSelectHeightSmall)
                in calendarSelectHeightMedium..calendarSelectHeightLarge -> (displayHeadSize - calendarSelectHeightMedium) / (calendarSelectHeightLarge - calendarSelectHeightMedium)
                else -> 0f
            }
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .onSizeChanged {
                        calendarSelectHeightMedium = it.height.toFloat() / 2
                        calendarSelectHeightLarge = it.height.toFloat()
                        if (currentHeadSize == 0f) currentHeadSize = calendarSelectHeightSmall
                    }
            ) {
                ScrollableDateSelector(
                    state = state,
                    displayHeadSize = displayHeadSize,
                    scrollConnection = scrollConnection,
                    headerScrollState = headerScrollState,
                    firstVisibleDate = firstVisibleDate,
                    setFirstVisibleDate = { firstVisibleDate = it },
                    isAnimating = isAnimating,
                    setIsAnimating = { isAnimating = it },
                    isScrolling = isScrolling,
                    calendarSelectHeightSmall = calendarSelectHeightSmall,
                    calendarSelectHeightMedium = calendarSelectHeightMedium,
                    calendarSelectHeightLarge = calendarSelectHeightLarge,
                    transitionProgress = transitionProgress,
                    setClosest = { closest = it },
                    doAction = doAction
                )
                HorizontalDivider()

                DayPager(
                    state = state,
                    contentPagerState = contentPagerState,
                    doAction = doAction,
                    onOpenHomeworkScreen = onOpenHomeworkScreen,
                    onTimetableInfoBannerClicked = onTimetableInfoBannerClicked,
                )
            }
        }
        else {
            Row {
                navRail(true) {
                    Column {
                        Spacer8Dp()
                        FloatingActionButton(onClick = { addHomeworkSheetInitialValues = AddHomeworkSheetInitialValues(until = state.selectedDate) }) { Icon(Icons.Default.Add, null) }
                    }
                }
                val topBarHeight = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Brush.linearGradient(listOf(NavigationRailDefaults.ContainerColor, MaterialTheme.colorScheme.surface))),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(topBarHeight/2))
                    CalendarDateHead(
                        firstVisibleDate = firstVisibleDate,
                        onClickToday = { doAction(CalendarViewAction.SelectDate(it, DateSelectCause.CALENDAR_CLICK)) }
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 8.dp)
                            .fillMaxSize()
                    ) {
                        FullMonthPager(
                            calendarSelectHeightLarge = null,
                            setFirstVisibleDate = { firstVisibleDate = it },
                            state = state,
                            doAction = doAction,
                            setIsAnimating = {},
                            setClosest = {},
                            calendarSelectHeightSmall = 0f,
                            calendarSelectHeightMedium = 0f
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .padding(top = topBarHeight/2)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    DayPager(
                        state = state,
                        contentPagerState = contentPagerState,
                        doAction = doAction,
                        onOpenHomeworkScreen = onOpenHomeworkScreen,
                        onTimetableInfoBannerClicked = onTimetableInfoBannerClicked
                    )
                }
            }
        }
    }
}

@Composable
private fun DayPager(
    state: CalendarViewState,
    contentPagerState: PagerState,
    doAction: (action: CalendarViewAction) -> Unit,
    onOpenHomeworkScreen: (homeworkId: Int) -> Unit,
    onTimetableInfoBannerClicked: () -> Unit,
) {
    val localConfiguration = LocalConfiguration.current
    LaunchedEffect(state.selectedDate) {
        if (state.currentSelectCause == DateSelectCause.DAY_SWIPE) return@LaunchedEffect
        val targetPage = CONTENT_PAGER_SIZE / 2 - state.selectedDate.atStartOfDay()
            .until(
                LocalDate.now().atStartOfDay(),
                ChronoUnit.DAYS
            ).toInt()
        contentPagerState.animateScrollToPage(targetPage)
    }

    LaunchedEffect(key1 = contentPagerState.targetPage) {
        if (state.currentSelectCause == DateSelectCause.DAY_SWIPE) return@LaunchedEffect
        val date = LocalDate.now().plusDays(
            getOffsetFromMiddle(
                CONTENT_PAGER_SIZE,
                contentPagerState.targetPage
            ).toLong()
        )
        if (state.selectedDate == date) return@LaunchedEffect
        doAction(CalendarViewAction.SelectDate(date, DateSelectCause.DAY_SWIPE))
    }

    HorizontalPager(
        state = contentPagerState,
        beyondViewportPageCount = 3,
        modifier = Modifier.fillMaxSize(),
        pageSize = PageSize.Fill
    ) { currentPage ->
        val date = LocalDate.now()
            .plusDays(getOffsetFromMiddle(CONTENT_PAGER_SIZE, currentPage).toLong())
        val day = remember(state.version, state.days.size) { state.days[date] ?: SchoolDay(date = date) }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) content@{
            DateBar(
                date = date,
                lastSync = state.lastSync,
                isSubstitutionPlan = if (day.lessons.isNotEmpty()) day.lessons.any { it is Lesson.SubstitutionPlanLesson } else null,
                isLarge = localConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()

                androidx.compose.animation.AnimatedVisibility(
                    visible = state.days[date] != null,
                    enter = slideInVertically(
                        animationSpec = tween(),
                        initialOffsetY = { 50 }) + fadeIn(animationSpec = tween())
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        state = rememberLazyListState()
                    ) {
                        item { DayInfoCard(day.info) }
                        item {
                            TypeFilters(
                                hasLessons = day.lessons.isNotEmpty(),
                                hasHomework = day.homework.isNotEmpty(),
                                hasGrades = day.grades.isNotEmpty(),
                                enabledFilters = state.enabledFilters,
                                toggleFilter = { doAction(CalendarViewAction.ToggleFilter(it)) }
                            )
                        }
                        item {
                            HomeworkSection(
                                showSection = (state.enabledFilters.isEmpty() || DayViewFilter.HOMEWORK in state.enabledFilters) && day.homework.isNotEmpty(),
                                homework = day.homework,
                                onOpenHomeworkScreen = onOpenHomeworkScreen,
                                currentProfile = state.currentProfile!!,
                                contextDate = date,
                                includeUntil = false
                            )

                            LessonsSection(
                                state = state,
                                day = day,
                                doAction = doAction,
                                onTimetableInfoBannerClicked = onTimetableInfoBannerClicked
                            )

                            androidx.compose.animation.AnimatedVisibility(
                                visible = (state.enabledFilters.isEmpty() || DayViewFilter.GRADES in state.enabledFilters) && day.grades.isNotEmpty(),
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                HorizontalDivider()
                                Column {
                                    Column(Modifier.padding(horizontal = 16.dp)) {
                                        Spacer8Dp()
                                        Text(
                                            text = stringResource(id = R.string.calendar_dayFilterGrades),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                    ) {
                                        Spacer8Dp()
                                        if (day.grades.isNotEmpty()) day.grades.forEach { grade ->
                                            GradeRecord(grade = grade, showSubject = true)
                                        }
                                        Spacer8Dp()
                                    }
                                }
                            }

                            when (day.type) {
                                DayType.WEEKEND, DayType.HOLIDAY -> {
                                    Column(
                                        modifier = Modifier
                                            .fillParentMaxSize()
                                            .defaultMinSize(minHeight = 148.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = painterResource(id = if (day.type == DayType.WEEKEND) R.drawable.undraw_fun_moments else R.drawable.undraw_beach_day),
                                            contentDescription = null,
                                            modifier = Modifier.size(148.dp)
                                        )
                                        Spacer16Dp()
                                        Text(
                                            text = stringResource(id = if (day.type == DayType.WEEKEND) R.string.calendar_dayTypeWeekend else R.string.calendar_dayTypeHoliday),
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                brush = Brush.horizontalGradient(
                                                    listOf(
                                                        MaterialTheme.colorScheme.primary,
                                                        MaterialTheme.colorScheme.secondary
                                                    )
                                                )
                                            )
                                        )
                                        state.days.values.sortedBy { it.date }
                                            .firstOrNull { it.date.isAfter(date) && it.lessons.isNotEmpty() }
                                            ?.let { nextDay ->
                                                Spacer16Dp()
                                                TextButton(onClick = {
                                                    doAction(
                                                        CalendarViewAction.SelectDate(
                                                            nextDay.date,
                                                            DateSelectCause.CALENDAR_CLICK
                                                        )
                                                    )
                                                }) {
                                                    RowVerticalCenter {
                                                        Text(
                                                            text = stringResource(
                                                                id = R.string.calendar_dayTypeNextDay,
                                                                nextDay.date.format(
                                                                    DateTimeFormatter.ofPattern(
                                                                        "EEEE"
                                                                    )
                                                                )
                                                            )
                                                        )
                                                        Spacer8Dp()
                                                        Icon(
                                                            Icons.AutoMirrored.Default.ArrowForward,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                            }
                                    }
                                }

                                else -> Unit
                            }
                        }
                        if(localConfiguration.orientation == Configuration.ORIENTATION_LANDSCAPE) item {
                            val bottomBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                            Spacer(Modifier.height(bottomBarHeight))
                        }
                    }
                }
            }
        }
    }
}

@Composable
@PreviewScreenSizes
private fun CalendarScreenPreview() {
    CalendarScreenContent(
        state = CalendarViewState(
            days = mapOf(
                LocalDate.now() to SchoolDay(
                    date = LocalDate.now(),
//                    info = "This is an information for all students!",
                    lessons = listOf(),
                    type = DayType.HOLIDAY
                )
            ),
            lastSync = ZonedDateTimeConverter().timestampToZonedDateTime(0)
        ),
        navRail = { _, fab ->
            fab()
            NavigationRailItem(selected = true, onClick = { }, label = { Text("Home") }, icon = { Icon(Icons.Default.Home, null) })
        }
    )
}

fun getOffsetFromMiddle(pages: Int, currentPage: Int): Int {
    return currentPage - pages / 2
}