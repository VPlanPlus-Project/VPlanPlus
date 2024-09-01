package es.jvbabi.vplanplus.feature.main_calendar.home.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.Day
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.DayDisplayState
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.Week
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.WeekHeader
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random

const val MONTH_PAGER_SIZE = 12 * 10
const val WEEK_PAGER_SIZE = 52
const val WEEK_HEADER_HEIGHT_DP = 16

@Composable
fun CalendarScreen(
    navHostController: NavHostController,
    navBar: @Composable (isVisible: Boolean) -> Unit
) {
    CalendarScreenContent(
        onBack = { navHostController.navigateUp() },
        navBar = navBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarScreenContent(
    onBack: () -> Unit = {},
    navBar: @Composable (Boolean) -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val days = remember { (0..365*2).associate { i ->
        val date = LocalDate.now().minusDays(getOffsetFromMiddle(2*365, i).toLong())
        date to Day(
            date = date,
            homework = Random.nextInt(0, 3),
            exams = Random.nextInt(0, 2)
        )
    } }
    val localDensity = LocalDensity.current
    val headerScrollState = rememberScrollState()
    val contentScrollState = rememberScrollState()
    val calendarSelectHeightSmall by remember { mutableFloatStateOf(with (localDensity) { 80.dp.toPx() }) }
    var calendarSelectHeightMedium by remember { mutableFloatStateOf(with (localDensity) { 81.dp.toPx() }) }
    var calendarSelectHeightLarge by remember { mutableFloatStateOf(with (localDensity) { 82.dp.toPx() }) }
    var currentHeadSize by remember { mutableFloatStateOf(calendarSelectHeightSmall) }
    val closest by remember(currentHeadSize) {
        mutableFloatStateOf(
            when (currentHeadSize) {
                in calendarSelectHeightSmall..listOf(calendarSelectHeightSmall, calendarSelectHeightMedium).average().toFloat() -> { calendarSelectHeightSmall }
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
                val isContentOnTop = contentScrollState.value == 0
                val isContentScrolling = contentScrollState.isScrollInProgress
                val isHeaderScrolling = headerScrollState.isScrollInProgress
                val delta = available.y
                if (isHeaderScrolling || (delta > 0 && isContentOnTop && isContentScrolling)) {
                    // scroll head
                    println("$useHeadSize | $delta")
                    currentHeadSize = (currentHeadSize + delta).coerceIn(
                        calendarSelectHeightSmall,
                        calendarSelectHeightLarge
                    )
                    return Offset(0f, delta)
                } else if (isContentScrolling && isContentOnTop && delta < 0 && useHeadSize > calendarSelectHeightSmall) {
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
        key2 = contentScrollState.isScrollInProgress
    ) {
        isScrolling = headerScrollState.isScrollInProgress || contentScrollState.isScrollInProgress
        if (!isScrolling && currentHeadSize != closest && closest != 0f) isAnimating = true
    }

    val displayHeadSize = if (!isScrolling) animateCurrentHeadSize else currentHeadSize

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.calendar_title)) },
                navigationIcon = { IconButton(onClick = onBack) { BackIcon() } }
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
                    calendarSelectHeightMedium = (it.height.toFloat() / 2)
                    calendarSelectHeightLarge = (it.height.toFloat())
                    if (currentHeadSize == 0f) currentHeadSize = calendarSelectHeightSmall
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(LocalDensity.current) { displayHeadSize.toDp() })
                    .nestedScroll(scrollConnection)
                    .verticalScroll(headerScrollState)
            ) calendarView@{
                WeekHeader(height = WEEK_HEADER_HEIGHT_DP.dp)
                val isMoving = isAnimating || headerScrollState.isScrollInProgress

                if (!isMoving && displayHeadSize == calendarSelectHeightSmall) {
                    val weekPager = rememberPagerState(
                        initialPage = WEEK_PAGER_SIZE / 2 - selectedDate.atStartOfWeek()
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

                        if (selectedDate.atStartOfWeek() == weekStart) return@LaunchedEffect
                        selectedDate =
                            if (weekStart == LocalDate.now().atStartOfWeek()) LocalDate.now()
                            else weekStart
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
                            selectedDay = selectedDate,
                            state = DayDisplayState.SMALL,
                            progress = 1f,
                            days = List(7) { index ->
                                val date = weekStart.plusDays(index.toLong())
                                days[date] ?: Day(
                                    date = date,
                                    homework = 0,
                                    exams = 0
                                )
                            },
                            onDayClicked = { selectedDate = it },
                            smallMaxHeight = with(LocalDensity.current) { calendarSelectHeightSmall.toDp() } - WEEK_HEADER_HEIGHT_DP.dp
                        )
                    }
                }
            }
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollConnection)
                    .verticalScroll(contentScrollState)
            ) content@{
                repeat(500) {
                    Text(text = "Item $it", fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
@Preview
private fun CalendarScreenPreview() {
    CalendarScreenContent()
}
private fun getOffsetFromMiddle(pages: Int, currentPage: Int): Int {
    return currentPage - pages / 2
}