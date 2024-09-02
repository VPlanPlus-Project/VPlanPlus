package es.jvbabi.vplanplus.feature.main_calendar.home.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.DayDisplayState
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.Week
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.WeekHeader
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.grades.GradeRecord
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.ui.common.Spacer12Dp
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.common.toLocalizedString
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils
import es.jvbabi.vplanplus.util.DateUtils.atStartOfMonth
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import es.jvbabi.vplanplus.util.DateUtils.epoch
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

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
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state = viewModel.state

    CalendarScreenContent(
        onBack = { navHostController.navigateUp() },
        onOpenHomeworkScreen = { homeworkId -> navHostController.navigate(Screen.HomeworkDetailScreen.route + "/$homeworkId") },
        navBar = navBar,
        doAction = viewModel::doAction,
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarScreenContent(
    onBack: () -> Unit = {},
    onOpenHomeworkScreen: (homeworkId: Int) -> Unit = {},
    doAction: (action: CalendarViewAction) -> Unit = {},
    navBar: @Composable (Boolean) -> Unit = {},
    state: CalendarViewState
) {
    val localDensity = LocalDensity.current
    val headerScrollState = rememberScrollState()
    val contentPagerState = rememberPagerState(initialPage = CONTENT_PAGER_SIZE / 2) { CONTENT_PAGER_SIZE }

    val calendarSelectHeightSmall by remember { mutableFloatStateOf(with(localDensity) { (70 + HEADER_STATIC_HEIGHT_DP).dp.toPx() }) }
    var calendarSelectHeightMedium by remember { mutableFloatStateOf(with(localDensity) { (70 + HEADER_STATIC_HEIGHT_DP).dp.toPx() }) }
    var calendarSelectHeightLarge by remember { mutableFloatStateOf(with(localDensity) { (70 + HEADER_STATIC_HEIGHT_DP).dp.toPx() }) }
    var currentHeadSize by remember { mutableFloatStateOf(calendarSelectHeightSmall) }
    var closest by remember(currentHeadSize) {
        mutableFloatStateOf(
            when (currentHeadSize) {
                in calendarSelectHeightSmall..listOf(calendarSelectHeightSmall, calendarSelectHeightMedium).average().toFloat() -> {
                    calendarSelectHeightSmall
                }

                in listOf(calendarSelectHeightSmall, calendarSelectHeightMedium).average()..listOf(calendarSelectHeightMedium, calendarSelectHeightLarge).average() -> calendarSelectHeightMedium
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
                    println("$useHeadSize | $delta")
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
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.calendar_title)) },
                navigationIcon = { IconButton(onClick = onBack) { BackIcon() } },
                actions = {
                    Box(
                        modifier = Modifier.height(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(Modifier.fillMaxHeight()) {
                            Spacer(Modifier.weight(.17f, true))
                            Box(
                                modifier = Modifier.weight(.83f, true),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = LocalDate.now().dayOfMonth.toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        IconButton(onClick = { doAction(CalendarViewAction.SelectDate(LocalDate.now())) }) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                        }
                    }
                }
            )
        },
        bottomBar = { navBar(true) }
    ) { innerPadding ->
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
            var firstVisibleDate by remember { mutableStateOf(LocalDate.now().atStartOfWeek()) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(LocalDensity.current) { displayHeadSize.toDp() })
                    .nestedScroll(scrollConnection)
                    .verticalScroll(headerScrollState)
            ) calendarView@{
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MONTH_HEADER_HEIGHT_DP.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstVisibleDate.format(DateTimeFormatter.ofPattern("LLL yy", Locale.getDefault())).uppercase(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                WeekHeader(height = WEEK_HEADER_HEIGHT_DP.dp)
                val isMoving = isAnimating || isScrolling

                if (!isMoving && displayHeadSize == calendarSelectHeightSmall) {
                    val weekPager = rememberPagerState(
                        initialPage = WEEK_PAGER_SIZE / 2 - state.selectedDate.atStartOfWeek()
                            .until(
                                LocalDate.now().atStartOfWeek(),
                                ChronoUnit.WEEKS
                            ).toInt()
                    ) { WEEK_PAGER_SIZE }
                    LaunchedEffect(
                        key1 = weekPager.targetPage,
                        weekPager.currentPageOffsetFraction
                    ) {
                        if (!(weekPager.currentPageOffsetFraction > -.25 && weekPager.currentPageOffsetFraction < .25)) return@LaunchedEffect
                        val weekStart = LocalDate.now().atStartOfWeek().plusWeeks(getOffsetFromMiddle(weekPager.pageCount, weekPager.targetPage).toLong())

                        firstVisibleDate = weekStart
                        if (state.selectedDate.atStartOfWeek() == weekStart) return@LaunchedEffect
                        doAction(CalendarViewAction.SelectDate(if (weekStart == LocalDate.now().atStartOfWeek()) LocalDate.now() else weekStart))

                    }

                    LaunchedEffect(key1 = state.selectedDate) {
                        weekPager.animateScrollToPage(
                            WEEK_PAGER_SIZE / 2 - state.selectedDate.atStartOfWeek()
                                .until(
                                    LocalDate.now().atStartOfWeek(),
                                    ChronoUnit.WEEKS
                                ).toInt()
                        )
                    }

                    HorizontalPager(
                        state = weekPager,
                        beyondViewportPageCount = 3
                    ) { currentPage ->
                        val weekStart = LocalDate.now().atStartOfWeek().plusWeeks(
                            getOffsetFromMiddle(
                                weekPager.pageCount,
                                currentPage
                            ).toLong()
                        )

                        Week(
                            selectedDay = state.selectedDate,
                            state = DayDisplayState.SMALL,
                            progress = 1f,
                            days = List(7) { index ->
                                val date = weekStart.plusDays(index.toLong())
                                state.days[date] ?: SchoolDay(date)
                            },
                            displayMonth = null,
                            onDayClicked = { doAction(CalendarViewAction.SelectDate(it)) },
                            smallMaxHeight = with(LocalDensity.current) { calendarSelectHeightSmall.toDp() } - HEADER_STATIC_HEIGHT_DP.dp,
                            mediumMaxHeight = with(LocalDensity.current) { calendarSelectHeightMedium.toDp() } - HEADER_STATIC_HEIGHT_DP.dp,
                            largeMaxHeight = with(LocalDensity.current) { calendarSelectHeightLarge.toDp() } - HEADER_STATIC_HEIGHT_DP.dp
                        )
                    }
                } else if (isMoving && displayHeadSize in calendarSelectHeightSmall..calendarSelectHeightMedium) {
                    // show month without pager
                    val firstDayOfMonth = state.selectedDate.atStartOfMonth()
                    val firstDayOfFirstWeekOfMonth = firstDayOfMonth.atStartOfWeek()
                    val weekHeaderMiddleHeight = (with(LocalDensity.current) { calendarSelectHeightMedium.toDp() } - HEADER_STATIC_HEIGHT_DP.dp) / 5
                    val calendarHeightSmallDp = with(LocalDensity.current) { calendarSelectHeightSmall.toDp() } - HEADER_STATIC_HEIGHT_DP.dp
                    repeat(5) { weekOffset ->
                        val weekStart = firstDayOfFirstWeekOfMonth.plusWeeks(weekOffset.toLong())
                        val isWeekWithFirstVisibleDate = weekStart <= state.selectedDate && weekStart.plusWeeks(1) > state.selectedDate
                        Box(
                            modifier = Modifier
                                .height(((weekHeaderMiddleHeight - calendarHeightSmallDp) * transitionProgress + calendarHeightSmallDp) * if (isWeekWithFirstVisibleDate) 1f else transitionProgress)
                                .alpha((if (isWeekWithFirstVisibleDate) 1f else ((transitionProgress - 0.5f).coerceAtLeast(0f) * 2)))
                        ) {
                            Week(
                                selectedDay = state.selectedDate,
                                state = DayDisplayState.REGULAR,
                                progress = transitionProgress,
                                days = List(7) { index ->
                                    val date = weekStart.plusDays(index.toLong())
                                    state.days[date] ?: SchoolDay(date)
                                },
                                displayMonth = firstDayOfMonth.month,
                                onDayClicked = { doAction(CalendarViewAction.SelectDate(it)) },
                                smallMaxHeight = calendarSelectHeightSmall.dp - HEADER_STATIC_HEIGHT_DP.dp,
                                mediumMaxHeight = weekHeaderMiddleHeight,
                                largeMaxHeight = weekHeaderMiddleHeight
                            )
                        }
                    }
                } else if (!isMoving && displayHeadSize == calendarSelectHeightMedium) {
                    // Show Month with pager
                    val monthPager = rememberPagerState(initialPage = MONTH_PAGER_SIZE / 2) { MONTH_PAGER_SIZE }
                    LaunchedEffect(key1 = monthPager.settledPage) {
                        val monthStart = LocalDate.now().atStartOfMonth().plusMonths(getOffsetFromMiddle(monthPager.pageCount, monthPager.settledPage).toLong())
                        firstVisibleDate = monthStart
                        if (state.selectedDate.atStartOfMonth() == monthStart) return@LaunchedEffect
                        doAction(CalendarViewAction.SelectDate(if (LocalDate.now().atStartOfMonth() == monthStart.atStartOfMonth()) LocalDate.now() else monthStart))
                    }

                    LaunchedEffect(state.selectedDate) {
                        monthPager.animateScrollToPage(
                            MONTH_PAGER_SIZE / 2 - state.selectedDate.atStartOfMonth()
                                .until(
                                    LocalDate.now().atStartOfMonth(),
                                    ChronoUnit.MONTHS
                                ).toInt()
                        )
                    }

                    HorizontalPager(
                        state = monthPager,
                        beyondViewportPageCount = 1
                    ) { currentPage ->
                        val month = LocalDate.now().atStartOfMonth().plusMonths(getOffsetFromMiddle(monthPager.pageCount, currentPage).toLong()).month
                        val monthStart = LocalDate.now().atStartOfMonth().plusMonths(getOffsetFromMiddle(monthPager.pageCount, currentPage).toLong()).atStartOfWeek()
                        Column {
                            repeat(5) { weekOffset ->
                                val weekStart = monthStart.plusWeeks(weekOffset.toLong())
                                Week(
                                    selectedDay = state.selectedDate,
                                    state = DayDisplayState.REGULAR,
                                    progress = 1f,
                                    days = List(7) { index ->
                                        val date = weekStart.plusDays(index.toLong())
                                        state.days[date] ?: SchoolDay(date)
                                    },
                                    displayMonth = month,
                                    onDayClicked = { doAction(CalendarViewAction.SelectDate(it)) },
                                    smallMaxHeight = with(LocalDensity.current) { calendarSelectHeightSmall.toDp() } - HEADER_STATIC_HEIGHT_DP.dp,
                                    mediumMaxHeight = (with(LocalDensity.current) { calendarSelectHeightMedium.toDp() } - HEADER_STATIC_HEIGHT_DP.dp) / 5,
                                    largeMaxHeight = (with(LocalDensity.current) { calendarSelectHeightLarge.toDp() } - HEADER_STATIC_HEIGHT_DP.dp) / 5
                                )
                            }
                        }
                    }
                } else if (isMoving && displayHeadSize in calendarSelectHeightMedium..calendarSelectHeightLarge) {
                    // show month without pager
                    val firstDayOfMonth = state.selectedDate.atStartOfMonth()
                    val firstDayOfFirstWeekOfMonth = firstDayOfMonth.atStartOfWeek()
                    val weekHeaderMiddleHeight = (with(LocalDensity.current) { calendarSelectHeightMedium.toDp() } - HEADER_STATIC_HEIGHT_DP.dp) / 5
                    val weekHeaderLargeHeight = (with(LocalDensity.current) { calendarSelectHeightLarge.toDp() } - HEADER_STATIC_HEIGHT_DP.dp) / 5
                    repeat(5) { weekOffset ->
                        val weekStart = firstDayOfFirstWeekOfMonth.plusWeeks(weekOffset.toLong())
                        Column(
                            modifier = Modifier
                                .height((weekHeaderLargeHeight - weekHeaderMiddleHeight) * transitionProgress + weekHeaderMiddleHeight)
                        ) {
                            Week(
                                selectedDay = state.selectedDate,
                                state = DayDisplayState.DETAILED,
                                progress = transitionProgress,
                                days = List(7) { index ->
                                    val date = weekStart.plusDays(index.toLong())
                                    state.days[date] ?: SchoolDay(date)
                                },
                                displayMonth = firstDayOfMonth.month,
                                onDayClicked = { doAction(CalendarViewAction.SelectDate(it)) },
                                smallMaxHeight = calendarSelectHeightMedium.dp - HEADER_STATIC_HEIGHT_DP.dp,
                                mediumMaxHeight = weekHeaderMiddleHeight,
                                largeMaxHeight = weekHeaderLargeHeight - if (weekOffset == 4) 0.dp else 1.dp
                            )
                            if (weekOffset != 4) HorizontalDivider(modifier = Modifier.alpha(transitionProgress))
                        }
                    }
                } else {
                    // show month with pager
                    val weekHeaderLargeHeight = (with(LocalDensity.current) { calendarSelectHeightLarge.toDp() } - HEADER_STATIC_HEIGHT_DP.dp) / 5
                    val monthPager = rememberPagerState(initialPage = MONTH_PAGER_SIZE / 2) { MONTH_PAGER_SIZE }
                    LaunchedEffect(key1 = monthPager.targetPage) {
                        val monthStart = LocalDate.now().atStartOfMonth().plusMonths(getOffsetFromMiddle(monthPager.pageCount, monthPager.targetPage).toLong())
                        firstVisibleDate = monthStart
                        if (state.selectedDate.atStartOfMonth() == monthStart) return@LaunchedEffect

                        doAction(CalendarViewAction.SelectDate(if (LocalDate.now().atStartOfMonth() == monthStart.atStartOfMonth()) LocalDate.now() else monthStart))
                    }

                    LaunchedEffect(state.selectedDate) {
                        monthPager.animateScrollToPage(
                            MONTH_PAGER_SIZE / 2 - state.selectedDate.atStartOfMonth()
                                .until(
                                    LocalDate.now().atStartOfMonth(),
                                    ChronoUnit.MONTHS
                                ).toInt()
                        )
                    }

                    HorizontalPager(
                        state = monthPager,
                        beyondViewportPageCount = 1
                    ) { currentPage ->
                        val month = LocalDate.now().atStartOfMonth().plusMonths(getOffsetFromMiddle(monthPager.pageCount, currentPage).toLong()).month
                        val monthStart = LocalDate.now().atStartOfMonth().plusMonths(getOffsetFromMiddle(monthPager.pageCount, currentPage).toLong()).atStartOfWeek()
                        Column {
                            repeat(5) { weekOffset ->
                                val weekStart = monthStart.plusWeeks(weekOffset.toLong())
                                Week(
                                    selectedDay = state.selectedDate,
                                    state = DayDisplayState.DETAILED,
                                    progress = 1f,
                                    days = List(7) { index ->
                                        val date = weekStart.plusDays(index.toLong())
                                        state.days[date] ?: SchoolDay(date)
                                    },
                                    displayMonth = month,
                                    onDayClicked = {
                                        doAction(CalendarViewAction.SelectDate(it))
                                        isAnimating = false
                                        closest = calendarSelectHeightLarge
                                        isAnimating = true
                                        closest = calendarSelectHeightSmall
                                    },
                                    smallMaxHeight = with(LocalDensity.current) { calendarSelectHeightSmall.toDp() } - HEADER_STATIC_HEIGHT_DP.dp,
                                    mediumMaxHeight = with(LocalDensity.current) { calendarSelectHeightMedium.toDp() } - HEADER_STATIC_HEIGHT_DP.dp,
                                    largeMaxHeight = weekHeaderLargeHeight - if (weekOffset == 4) 0.dp else 1.dp
                                )
                                if (weekOffset != 4) HorizontalDivider()
                            }
                        }
                    }
                }
            }
            HorizontalDivider()

            LaunchedEffect(state.selectedDate) {
                contentPagerState.animateScrollToPage(
                    CONTENT_PAGER_SIZE / 2 - state.selectedDate.atStartOfDay()
                        .until(
                            LocalDate.now().atStartOfDay(),
                            ChronoUnit.DAYS
                        ).toInt()
                )
            }

            LaunchedEffect(key1 = contentPagerState.targetPage) {
                val date = LocalDate.now().plusDays(getOffsetFromMiddle(CONTENT_PAGER_SIZE, contentPagerState.targetPage).toLong())
                if (state.selectedDate == date) return@LaunchedEffect
                doAction(CalendarViewAction.SelectDate(date))
            }

            HorizontalPager(
                state = contentPagerState,
                beyondViewportPageCount = 3,
                modifier = Modifier.fillMaxSize(),
                pageSize = PageSize.Fill
            ) { currentPage ->
                val date = LocalDate.now().plusDays(getOffsetFromMiddle(CONTENT_PAGER_SIZE, currentPage).toLong())
                val day = state.days[date] ?: SchoolDay(date)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) content@{
                    RowVerticalCenterSpaceBetweenFill(
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
                    ) title@{
                        RowVerticalCenter {
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("d. LLL", Locale.getDefault())),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            if (DateUtils.localizedRelativeDate(LocalContext.current, date, false) != null) Text(
                                text = " $DOT " + DateUtils.localizedRelativeDate(LocalContext.current, date, false)!!,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (state.lastSync != null) Text(
                            text =
                            if (state.lastSync == epoch()) stringResource(id = R.string.calendar_lastSyncNever)
                            else stringResource(id = R.string.calendar_lastSync, state.lastSync.toLastSyncText()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()

                        androidx.compose.animation.AnimatedVisibility(
                            visible = state.days[date] != null,
                            enter = slideInVertically(animationSpec = tween(), initialOffsetY = { 50 }) + fadeIn(animationSpec = tween())
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface),
                                state = rememberLazyListState()
                            ) {
                                item {
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = day.info != null,
                                        enter = expandVertically(),
                                        exit = shrinkVertically(),
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                                    ) {
                                        InfoCard(
                                            imageVector = Icons.Default.Campaign,
                                            title = stringResource(id = R.string.calendar_infoTitle),
                                            text = day.info ?: ""
                                        )
                                    }
                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                    ) {
                                        item { Spacer16Dp() }
                                        item {
                                            androidx.compose.animation.AnimatedVisibility(
                                                visible = day.lessons.isNotEmpty(),
                                                enter = expandHorizontally(),
                                                exit = shrinkVertically()
                                            ) {
                                                Row {
                                                    FilterChip(
                                                        selected = DayViewFilter.LESSONS in state.enabledFilters,
                                                        onClick = { doAction(CalendarViewAction.ToggleFilter(DayViewFilter.LESSONS)) },
                                                        label = { Text(text = stringResource(id = R.string.calendar_dayFilterLessons)) },
                                                        leadingIcon = {
                                                            Icon(imageVector = if (DayViewFilter.LESSONS in state.enabledFilters) Icons.Default.Check else Icons.Default.School, contentDescription = null)
                                                        }
                                                    )
                                                    Spacer8Dp()
                                                }
                                            }
                                        }
                                        item {
                                            androidx.compose.animation.AnimatedVisibility(
                                                visible = day.homework.isNotEmpty(),
                                                enter = expandHorizontally(),
                                                exit = shrinkVertically()
                                            ) {
                                                Row {
                                                    FilterChip(
                                                        selected = DayViewFilter.HOMEWORK in state.enabledFilters,
                                                        onClick = { doAction(CalendarViewAction.ToggleFilter(DayViewFilter.HOMEWORK)) },
                                                        label = { Text(text = stringResource(id = R.string.calendar_dayFilterHomework)) },
                                                        leadingIcon = {
                                                            Icon(imageVector = if (DayViewFilter.HOMEWORK in state.enabledFilters) Icons.Default.Check else Icons.AutoMirrored.Default.MenuBook, contentDescription = null)
                                                        }
                                                    )
                                                    Spacer8Dp()
                                                }
                                            }
                                        }
                                        item {
                                            androidx.compose.animation.AnimatedVisibility(
                                                visible = day.grades.isNotEmpty(),
                                                enter = expandHorizontally(),
                                                exit = shrinkVertically()
                                            ) {
                                                Row {
                                                    FilterChip(
                                                        selected = DayViewFilter.GRADES in state.enabledFilters,
                                                        onClick = { doAction(CalendarViewAction.ToggleFilter(DayViewFilter.GRADES)) },
                                                        label = { Text(text = stringResource(id = R.string.calendar_dayFilterGrades)) },
                                                        leadingIcon = {
                                                            if (DayViewFilter.GRADES in state.enabledFilters) Icon(Icons.Default.Check, contentDescription = null)
                                                            else Icon(painterResource(id = R.drawable.order_approve), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                                        }
                                                    )
                                                    Spacer8Dp()
                                                }
                                            }
                                        }
                                    }

                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = (state.enabledFilters.isEmpty() || DayViewFilter.HOMEWORK in state.enabledFilters) && day.homework.isNotEmpty(),
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        HorizontalDivider()
                                        Column {
                                            Spacer8Dp()
                                            if (day.homework.isNotEmpty()) Box(Modifier.padding(start = 16.dp)) {
                                                Text(text = stringResource(id = R.string.calendar_dayFilterHomework), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                                            }
                                            Spacer4Dp()
                                            day.homework.forEach { hw ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .defaultMinSize(minHeight = 40.dp)
                                                        .padding(vertical = 4.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .clickable { onOpenHomeworkScreen(hw.homework.id) }
                                                ) {
                                                    Spacer16Dp()
                                                    Spacer16Dp()
                                                    SubjectIcon(
                                                        subject = hw.homework.defaultLesson?.subject,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier
                                                            .padding(top = 8.dp, bottom = 8.dp)
                                                            .size(24.dp),
                                                    )
                                                    Spacer16Dp()
                                                    Column {
                                                        RowVerticalCenter {
                                                            CircularProgressIndicator(
                                                                progress = { hw.tasks.count { it.isDone }.toFloat() / hw.tasks.size.coerceAtLeast(1) },
                                                                modifier = Modifier.size(16.dp),
                                                                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                            Spacer8Dp()
                                                            Text(
                                                                text = hw.homework.defaultLesson?.subject ?: stringResource(id = R.string.homework_noSubject),
                                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, textDecoration = if (hw.allDone()) TextDecoration.LineThrough else null)
                                                            )
                                                            Spacer8Dp()
                                                            Text(
                                                                text = when (hw) {
                                                                    is PersonalizedHomework.LocalHomework -> stringResource(id = R.string.homework_thisDevice)
                                                                    is PersonalizedHomework.CloudHomework -> {
                                                                        if (hw.homework.createdBy.id == (state.currentProfile as? ClassProfile)?.vppId?.id) stringResource(id = R.string.homework_homeworkSubtitleCreatedByYou)
                                                                        else hw.homework.createdBy.name
                                                                    }
                                                                },
                                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                            )
                                                        }
                                                        Text(
                                                            text = buildAnnotatedString {
                                                                val style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant).toSpanStyle()
                                                                hw.tasks.sortedBy { it.isDone }.forEachIndexed { i, task ->
                                                                    withStyle(if (task.isDone) style.copy(textDecoration = TextDecoration.LineThrough) else style) {
                                                                        append(task.content)
                                                                        append(if (i != hw.tasks.size - 1) ", " else "")
                                                                    }
                                                                }
                                                            },
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer8Dp()
                                        }
                                    }

                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = (state.enabledFilters.isEmpty() || DayViewFilter.LESSONS in state.enabledFilters) && day.lessons.isNotEmpty(),
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        HorizontalDivider()
                                        Column {
                                            Column(Modifier.padding(horizontal = 16.dp)) {
                                                Spacer8Dp()
                                                Text(text = stringResource(id = R.string.calendar_dayFilterLessons), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                                            }
                                            Column(Modifier.padding(horizontal = 24.dp)) {
                                                val lessonsGroupedByLessonNumber = day.lessons.groupBy { it.lessonNumber }.toList().sortedBy { it.first }
                                                lessonsGroupedByLessonNumber.forEachIndexed { i, (lessonNumber, lessons) ->
                                                    Spacer8Dp()
                                                    Row {
                                                        Column(
                                                            modifier = Modifier
                                                                .width(40.dp)
                                                                .height(24.dp),
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            Text(
                                                                text = stringResource(id = R.string.calendar_dayLessonNumber, lessonNumber.toLocalizedString()),
                                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                                            )
                                                        }
                                                        Column {
                                                            lessons.forEach { lesson ->
                                                                Row(
                                                                    modifier = Modifier.defaultMinSize(minHeight = 40.dp),
                                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                                ) {
                                                                    SubjectIcon(
                                                                        subject = lesson.displaySubject,
                                                                        modifier = Modifier.size(24.dp),
                                                                        tint = if (lesson.changedSubject != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                                                    )
                                                                    Column {
                                                                        RowVerticalCenter(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                                            Text(
                                                                                text = lesson.displaySubject,
                                                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                                                                color = if (lesson.changedSubject != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                                                            )
                                                                            Text(
                                                                                text = buildAnnotatedString {
                                                                                    val style = MaterialTheme.typography.bodyMedium.toSpanStyle()
                                                                                    val changed = style.copy(color = MaterialTheme.colorScheme.error)
                                                                                    val booked = style.copy(color = MaterialTheme.colorScheme.secondary)
                                                                                    withStyle(if (lesson.roomIsChanged) changed else style) {
                                                                                        append(lesson.rooms.joinToString(", ") { it.name })
                                                                                        if (lesson.roomBooking != null) append(", ")
                                                                                    }
                                                                                    if (lesson.roomBooking != null) withStyle(booked) {
                                                                                        append(lesson.roomBooking.room.name)
                                                                                    }
                                                                                }
                                                                            )
                                                                        }
                                                                        RowVerticalCenter(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                                            Text(
                                                                                text = buildAnnotatedString {
                                                                                    val style = MaterialTheme.typography.bodySmall.copy(
                                                                                        fontWeight = FontWeight.Light,
                                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                                    ).toSpanStyle()
                                                                                    val changed = style.copy(color = MaterialTheme.colorScheme.error)
                                                                                    withStyle(style) {
                                                                                        append(lesson.start.format(DateTimeFormatter.ofPattern("HH:mm")))
                                                                                        append(" - ")
                                                                                        append(lesson.end.format(DateTimeFormatter.ofPattern("HH:mm")))
                                                                                    }
                                                                                    if (lesson.teachers.isNotEmpty()) {
                                                                                        withStyle(if (lesson.teacherIsChanged) changed else style) {
                                                                                            append(" $DOT ")
                                                                                            append(lesson.teachers.joinToString(", ") { it.acronym })
                                                                                        }
                                                                                    }
                                                                                }
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    Spacer12Dp()
                                                    if (i != lessonsGroupedByLessonNumber.lastIndex) HorizontalDivider()
                                                }
                                            }
                                        }
                                    }

                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = (state.enabledFilters.isEmpty() || DayViewFilter.GRADES in state.enabledFilters) && day.grades.isNotEmpty(),
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        HorizontalDivider()
                                        Column {
                                            Column(Modifier.padding(horizontal = 16.dp)) {
                                                Spacer8Dp()
                                                Text(text = stringResource(id = R.string.calendar_dayFilterGrades), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
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
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun CalendarScreenPreview() {
    CalendarScreenContent(
        state = CalendarViewState(
            days = mapOf(
                LocalDate.now() to SchoolDay(LocalDate.now(), "This is an information for all students!", listOf())
            ),
            lastSync = ZonedDateTimeConverter().timestampToZonedDateTime(0)
        )
    )
}

private fun getOffsetFromMiddle(pages: Int, currentPage: Int): Int {
    return currentPage - pages / 2
}

private fun ZonedDateTime.toLastSyncText(): String {
    val time = this.toZonedLocalDateTime()
    return if (time.toLocalDate()
            .isEqual(LocalDate.now())
    ) DateTimeFormatter.ofPattern("HH:mm").format(time)
    else DateTimeFormatter.ofPattern("EE, dd.MM.yyyy").format(time)
}