package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.CalendarViewAction
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.CalendarViewState
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.HEADER_STATIC_HEIGHT_DP
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.MONTH_HEADER_HEIGHT_DP
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.MONTH_PAGER_SIZE
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.WEEK_HEADER_HEIGHT_DP
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.WEEK_PAGER_SIZE
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.getOffsetFromMiddle
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.util.DateUtils.atStartOfMonth
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun ScrollableDateSelector(
    state: CalendarViewState,
    displayHeadSize: Float,
    scrollConnection: NestedScrollConnection,
    headerScrollState: ScrollState,
    firstVisibleDate: LocalDate,
    setFirstVisibleDate: (LocalDate) -> Unit,
    isAnimating: Boolean,
    setIsAnimating: (Boolean) -> Unit,
    isScrolling: Boolean,
    calendarSelectHeightSmall: Float,
    calendarSelectHeightMedium: Float,
    calendarSelectHeightLarge: Float,
    transitionProgress: Float,
    setClosest: (Float) -> Unit,
    doAction: (action: CalendarViewAction) -> Unit
) {
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
            CalendarDateHead(firstVisibleDate)
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

                setFirstVisibleDate(weekStart)
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
                    Column {
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
            }
        } else if (!isMoving && displayHeadSize == calendarSelectHeightMedium) {
            // Show Month with pager
            val monthPager = rememberPagerState(initialPage = MONTH_PAGER_SIZE / 2) { MONTH_PAGER_SIZE }
            LaunchedEffect(key1 = monthPager.settledPage) {
                val monthStart = LocalDate.now().atStartOfMonth().plusMonths(getOffsetFromMiddle(monthPager.pageCount, monthPager.settledPage).toLong())
                setFirstVisibleDate(monthStart)
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
            FullMonthPager(
                calendarSelectHeightLarge,
                setFirstVisibleDate,
                state,
                doAction,
                setIsAnimating,
                setClosest,
                calendarSelectHeightSmall,
                calendarSelectHeightMedium
            )
        }
    }
}

@Composable
fun CalendarDateHead(
    firstVisibleDate: LocalDate,
    onClickToday: ((date: LocalDate) -> Unit)? = null
) {
    RowVerticalCenter(Modifier.height(HEADER_STATIC_HEIGHT_DP.dp)) {
        AnimatedContent(
            targetState = firstVisibleDate.month to firstVisibleDate.year, label = "month",
        ) { (month, year) ->
            val date = LocalDate.of(year, month, 1)
            Text(
                text = date.format(DateTimeFormatter.ofPattern("LLL yy", Locale.getDefault()))
                    .uppercase(),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (onClickToday != null) {
            Spacer8Dp()
            TodayButton(selectDate = onClickToday)
        }
    }
}

@Composable
fun FullMonthPager(
    calendarSelectHeightLarge: Float?,
    setFirstVisibleDate: (LocalDate) -> Unit,
    state: CalendarViewState,
    doAction: (action: CalendarViewAction) -> Unit,
    setIsAnimating: (Boolean) -> Unit,
    setClosest: (Float) -> Unit,
    calendarSelectHeightSmall: Float,
    calendarSelectHeightMedium: Float
) {
    val weekHeaderLargeHeight = if (calendarSelectHeightLarge == null) null else
        (with(LocalDensity.current) { calendarSelectHeightLarge.toDp() } - HEADER_STATIC_HEIGHT_DP.dp) / 5
    val monthPager = rememberPagerState(initialPage = MONTH_PAGER_SIZE / 2) { MONTH_PAGER_SIZE }
    LaunchedEffect(key1 = monthPager.targetPage) {
        val monthStart = LocalDate.now().atStartOfMonth()
            .plusMonths(getOffsetFromMiddle(monthPager.pageCount, monthPager.targetPage).toLong())
        setFirstVisibleDate(monthStart)
        if (state.selectedDate.atStartOfMonth() == monthStart) return@LaunchedEffect

        doAction(
            CalendarViewAction.SelectDate(
                if (LocalDate.now()
                        .atStartOfMonth() == monthStart.atStartOfMonth()
                ) LocalDate.now() else monthStart
            )
        )
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
        val month = LocalDate.now().atStartOfMonth()
            .plusMonths(getOffsetFromMiddle(monthPager.pageCount, currentPage).toLong()).month
        val monthStart = LocalDate.now().atStartOfMonth()
            .plusMonths(getOffsetFromMiddle(monthPager.pageCount, currentPage).toLong())
            .atStartOfWeek()
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
                        if (calendarSelectHeightLarge == null) return@Week
                        setIsAnimating(false)
                        setClosest(calendarSelectHeightLarge)
                        setIsAnimating(true)
                        setClosest(calendarSelectHeightSmall)
                    },
                    smallMaxHeight = with(LocalDensity.current) { calendarSelectHeightSmall.toDp() } - HEADER_STATIC_HEIGHT_DP.dp,
                    mediumMaxHeight = with(LocalDensity.current) { calendarSelectHeightMedium.toDp() } - HEADER_STATIC_HEIGHT_DP.dp,
                    largeMaxHeight = if (weekHeaderLargeHeight == null) null else weekHeaderLargeHeight - if (weekOffset == 4) 0.dp else 1.dp
                )
                if (weekOffset != 4) HorizontalDivider()
            }
        }
    }
}