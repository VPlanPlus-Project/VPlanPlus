package es.jvbabi.vplanplus.feature.main_calendar.home.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.model.ClassProfile
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
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.grades.GradeRecord
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheet
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheetInitialValues
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer12Dp
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.common.toLocalizedString
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.DateUtils.atStartOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.pow
import kotlin.math.sqrt

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
        topBar = { if (localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) TopBar(onBack = onBack, selectDate = { doAction(CalendarViewAction.SelectDate(it)) }) },
        floatingActionButton = {
            CalendarFloatingActionButton(
                isVisible = closest != calendarSelectHeightLarge,
                onClick = {
                    addHomeworkSheetInitialValues =
                        AddHomeworkSheetInitialValues(until = state.selectedDate)
                })
        },
        bottomBar = { navBar(localConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT && addHomeworkSheetInitialValues == null) }
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
                    state,
                    contentPagerState,
                    doAction,
                    onOpenHomeworkScreen,
                    onTimetableInfoBannerClicked,
                )
            }
        }
        else {
            Row(Modifier.padding(innerPadding)) {
                navRail(true) { FloatingActionButton(onClick = { addHomeworkSheetInitialValues = AddHomeworkSheetInitialValues(until = state.selectedDate) }) { Icon(Icons.Default.Add, null) } }
                Spacer8Dp()
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CalendarDateHead(
                        firstVisibleDate = firstVisibleDate,
                        onClickToday = { doAction(CalendarViewAction.SelectDate(it)) }
                    )
                    Spacer8Dp()
                    FullMonthPager(
                        calendarSelectHeightLarge = null,
                        setFirstVisibleDate = { firstVisibleDate = it },
                        state = state,
                        doAction = doAction,
                        setIsAnimating = { isAnimating = it },
                        setClosest = { closest = it },
                        calendarSelectHeightSmall = calendarSelectHeightSmall,
                        calendarSelectHeightMedium = calendarSelectHeightMedium
                    )
                }
                Column(Modifier.weight(1f)) {
                    DayPager(
                        state,
                        contentPagerState,
                        doAction,
                        onOpenHomeworkScreen,
                        onTimetableInfoBannerClicked
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
        val date = LocalDate.now().plusDays(
            getOffsetFromMiddle(
                CONTENT_PAGER_SIZE,
                contentPagerState.targetPage
            ).toLong()
        )
        if (state.selectedDate == date) return@LaunchedEffect
        doAction(CalendarViewAction.SelectDate(date))
    }

    HorizontalPager(
        state = contentPagerState,
        beyondViewportPageCount = 3,
        modifier = Modifier.fillMaxSize(),
        pageSize = PageSize.Fill
    ) { currentPage ->
        val date = LocalDate.now()
            .plusDays(getOffsetFromMiddle(CONTENT_PAGER_SIZE, currentPage).toLong())
        val day = state.days[date] ?: SchoolDay(date)
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) content@{
            DateBar(
                date = date,
                lastSync = state.lastSync,
                isSubstitutionPlan = if (day.lessons.isNotEmpty()) day.lessons.any { it is Lesson.SubstitutionPlanLesson } else null
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
                            androidx.compose.animation.AnimatedVisibility(
                                visible = (state.enabledFilters.isEmpty() || DayViewFilter.HOMEWORK in state.enabledFilters) && day.homework.isNotEmpty(),
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                HorizontalDivider()
                                Column {
                                    Spacer8Dp()
                                    if (day.homework.isNotEmpty()) Box(
                                        Modifier.padding(
                                            start = 16.dp
                                        )
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.calendar_dayFilterHomework),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                    Spacer4Dp()
                                    day.homework.forEach { hw ->
                                        RowVerticalCenter(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .defaultMinSize(minHeight = 40.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable { onOpenHomeworkScreen(hw.homework.id) }
                                                .padding(
                                                    vertical = 8.dp,
                                                    horizontal = 16.dp
                                                )
                                        ) {
                                            Spacer8Dp()
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val circularProgressIndicatorPadding = 3
                                                SubjectIcon(
                                                    subject = hw.homework.defaultLesson?.subject,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier
                                                        .size(
                                                            sqrt(
                                                                ((32 - circularProgressIndicatorPadding) / 2f).pow(
                                                                    2
                                                                ) * 2
                                                            ).dp
                                                        ),
                                                )
                                                CircularProgressIndicator(
                                                    progress = {
                                                        hw.tasks.count { it.isDone }
                                                            .toFloat() / hw.tasks.size.coerceAtLeast(
                                                            1
                                                        )
                                                    },
                                                    modifier = Modifier.size(32.dp),
                                                    trackColor = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.2f
                                                    ),
                                                    color = MaterialTheme.colorScheme.primary,
                                                    strokeWidth = circularProgressIndicatorPadding.dp
                                                )
                                            }
                                            Spacer8Dp()
                                            Column {
                                                RowVerticalCenter {
                                                    Text(
                                                        text = hw.homework.defaultLesson?.subject
                                                            ?: stringResource(id = R.string.homework_noSubject),
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = FontWeight.SemiBold,
                                                            textDecoration = if (hw.allDone()) TextDecoration.LineThrough else null
                                                        )
                                                    )
                                                    Spacer8Dp()
                                                    Text(
                                                        text = when (hw) {
                                                            is PersonalizedHomework.LocalHomework -> stringResource(
                                                                id = R.string.homework_thisDevice
                                                            )

                                                            is PersonalizedHomework.CloudHomework -> {
                                                                if (hw.homework.createdBy.id == (state.currentProfile as? ClassProfile)?.vppId?.id) stringResource(
                                                                    id = R.string.homework_you
                                                                )
                                                                else hw.homework.createdBy.name
                                                            }
                                                        },
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            fontWeight = FontWeight.Light,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    )
                                                }
                                                Text(
                                                    text = buildAnnotatedString {
                                                        val style =
                                                            MaterialTheme.typography.bodySmall.copy(
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            ).toSpanStyle()
                                                        hw.tasks.sortedBy { it.isDone }
                                                            .forEachIndexed { i, task ->
                                                                withStyle(
                                                                    if (task.isDone) style.copy(
                                                                        textDecoration = TextDecoration.LineThrough
                                                                    ) else style
                                                                ) {
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
                                        Text(
                                            text = stringResource(id = R.string.calendar_dayFilterLessons),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        if (day.lessons.any { it is Lesson.TimetableLesson }) AnimatedVisibility(
                                            visible = state.canShowTimetableInfoBanner,
                                            enter = fadeIn(tween(0)),
                                            exit = shrinkVertically()
                                        ) {
                                            Column {
                                                Spacer8Dp()
                                                InfoCard(
                                                    imageVector = Icons.Default.WarningAmber,
                                                    title = stringResource(id = R.string.calendar_timetableBannerTitle),
                                                    text = stringResource(id = R.string.calendar_timetableBannerText),
                                                    buttonText2 = stringResource(id = android.R.string.ok),
                                                    buttonAction2 = {
                                                        doAction(
                                                            CalendarViewAction.DismissTimetableInfoBanner
                                                        )
                                                    },
                                                    buttonText1 = stringResource(id = R.string.learn_more),
                                                    buttonAction1 = onTimetableInfoBannerClicked,
                                                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                                    textColor = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }
                                        }
                                    }
                                    Column(Modifier.padding(horizontal = 24.dp)) {
                                        val lessonsGroupedByLessonNumber =
                                            day.lessons.groupBy { it.lessonNumber }.toList()
                                                .sortedBy { it.first }
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
                                                        text = stringResource(
                                                            id = R.string.calendar_dayLessonNumber,
                                                            lessonNumber.toLocalizedString()
                                                        ),
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    )
                                                }
                                                Column {
                                                    lessons.forEach { lesson ->
                                                        Row(
                                                            modifier = Modifier.defaultMinSize(
                                                                minHeight = 40.dp
                                                            ),
                                                            horizontalArrangement = Arrangement.spacedBy(
                                                                8.dp
                                                            )
                                                        ) {
                                                            SubjectIcon(
                                                                subject = lesson.displaySubject,
                                                                modifier = Modifier.size(24.dp),
                                                                tint = if (lesson is Lesson.SubstitutionPlanLesson && lesson.changedSubject != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                                            )
                                                            Column {
                                                                RowVerticalCenter(
                                                                    horizontalArrangement = Arrangement.spacedBy(
                                                                        8.dp
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        text = lesson.displaySubject,
                                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                                            fontWeight = FontWeight.SemiBold
                                                                        ),
                                                                        color = if (lesson is Lesson.SubstitutionPlanLesson && lesson.changedSubject != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                                                    )
                                                                    Text(
                                                                        text = buildAnnotatedString {
                                                                            val style =
                                                                                MaterialTheme.typography.bodyMedium.toSpanStyle()
                                                                            val changed =
                                                                                style.copy(
                                                                                    color = MaterialTheme.colorScheme.error
                                                                                )
                                                                            val booked =
                                                                                style.copy(
                                                                                    color = MaterialTheme.colorScheme.secondary
                                                                                )
                                                                            withStyle(if (lesson is Lesson.SubstitutionPlanLesson && lesson.roomIsChanged) changed else style) {
                                                                                append(
                                                                                    lesson.rooms.joinToString(
                                                                                        ", "
                                                                                    ) { it.name })
                                                                                if (lesson is Lesson.SubstitutionPlanLesson && lesson.roomBooking != null) append(
                                                                                    ", "
                                                                                )
                                                                            }
                                                                            if (lesson is Lesson.SubstitutionPlanLesson && lesson.roomBooking != null) withStyle(
                                                                                booked
                                                                            ) {
                                                                                append(
                                                                                    lesson.roomBooking.room.name
                                                                                )
                                                                            }
                                                                        }
                                                                    )
                                                                }
                                                                RowVerticalCenter(
                                                                    horizontalArrangement = Arrangement.spacedBy(
                                                                        8.dp
                                                                    )
                                                                ) {
                                                                    Text(
                                                                        text = buildAnnotatedString {
                                                                            val style =
                                                                                MaterialTheme.typography.bodySmall.copy(
                                                                                    fontWeight = FontWeight.Light,
                                                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                                )
                                                                                    .toSpanStyle()
                                                                            val changed =
                                                                                style.copy(
                                                                                    color = MaterialTheme.colorScheme.error
                                                                                )
                                                                            withStyle(style) {
                                                                                append(
                                                                                    lesson.start.format(
                                                                                        DateTimeFormatter.ofPattern(
                                                                                            "HH:mm"
                                                                                        )
                                                                                    )
                                                                                )
                                                                                append(" - ")
                                                                                append(
                                                                                    lesson.end.format(
                                                                                        DateTimeFormatter.ofPattern(
                                                                                            "HH:mm"
                                                                                        )
                                                                                    )
                                                                                )
                                                                            }
                                                                            if (lesson.teachers.isNotEmpty()) {
                                                                                withStyle(if (lesson is Lesson.SubstitutionPlanLesson && lesson.teacherIsChanged) changed else style) {
                                                                                    append(" $DOT ")
                                                                                    append(
                                                                                        lesson.teachers.joinToString(
                                                                                            ", "
                                                                                        ) { it.acronym })
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
                                                            nextDay.date
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