package es.jvbabi.vplanplus.ui.screens.home.search.room

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.repository.BookResult
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.usecase.find_room.BookRoomAbility
import es.jvbabi.vplanplus.domain.usecase.find_room.CancelBookingResult
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.Badge
import es.jvbabi.vplanplus.ui.common.ComposableDialog
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.School
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.BookingDetailDialog
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.CannotBookRoomNotVerifiedDialog
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.CannotBookRoomWrongTypeDialog
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.FilterChips
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.Guide
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.LessonDialog
import es.jvbabi.vplanplus.ui.screens.home.search.room.components.SearchField
import es.jvbabi.vplanplus.util.DateUtils.atBeginningOfTheWorld
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.random.Random
import es.jvbabi.vplanplus.ui.preview.Room as PreviewRoom

@Composable
fun FindAvailableRoomScreen(
    navController: NavHostController,
    roomSearchViewModel: RoomSearchViewModel = hiltViewModel()
) {
    val state = roomSearchViewModel.state.value

    FindAvailableRoomScreenContent(
        state = state,
        onBackClicked = { navController.navigateUp() },
        onRoomFilterValueChanged = { roomSearchViewModel.onRoomFilterValueChanged(it) },
        onNowToggled = { roomSearchViewModel.toggleFilterNow() },
        onNextToggled = { roomSearchViewModel.toggleFilterNext() },
        onOpenLessonDetailDialog = { roomSearchViewModel.showLessonDetailDialog(it) },
        onOpenBookingDetailDialog = { roomSearchViewModel.showBookingDetailDialog(it) },
        onCloseLessonDetailDialog = { roomSearchViewModel.closeDialog() },
        onBookRoomClicked = { room, start, end ->
            roomSearchViewModel.openBookRoomDialog(room, start, end)
        },
        onCancelCurrentBooking = { roomSearchViewModel.cancelCurrentBooking() },
        onConfirmBooking = { roomSearchViewModel.confirmBooking() },
        onCloseBookRoomDialog = { roomSearchViewModel.closeBookRoomDialog() }
    )

    val context = LocalContext.current
    val bookingResultMessage = mapOf(
        BookResult.SUCCESS to stringResource(id = R.string.searchAvailableRoom_bookSuccess),
        BookResult.CONFLICT to stringResource(id = R.string.searchAvailableRoom_bookConflict),
        BookResult.OTHER to stringResource(id = R.string.searchAvailableRoom_bookOther),
        BookResult.NO_INTERNET to stringResource(id = R.string.searchAvailableRoom_bookNoInternet),
    )
    val bookingCancelResult = mapOf(
        CancelBookingResult.SUCCESS to stringResource(id = R.string.searchAvailableRoom_cancelSuccess),
        CancelBookingResult.ERROR to stringResource(id = R.string.unknownError),
        CancelBookingResult.NO_INTERNET to stringResource(id = R.string.noInternet),
        CancelBookingResult.BOOKING_NOT_FOUND to stringResource(id = R.string.searchAvailableRoom_cancelNotFound),
    )
    LaunchedEffect(key1 = state.roomBookingResult) {
        if (state.roomBookingResult == null) return@LaunchedEffect
        Toast.makeText(
            context,
            bookingResultMessage[state.roomBookingResult]!!,
            Toast.LENGTH_SHORT
        ).show()
    }

    LaunchedEffect(key1 = state.roomCancelBookingResult, block = {
        if (state.roomCancelBookingResult == null) return@LaunchedEffect
        Toast.makeText(
            context,
            bookingCancelResult[state.roomCancelBookingResult]!!,
            Toast.LENGTH_SHORT
        ).show()
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindAvailableRoomScreenContent(
    state: RoomSearchState,
    onBackClicked: () -> Unit,
    onRoomFilterValueChanged: (String) -> Unit = {},
    onNowToggled: () -> Unit = {},
    onNextToggled: () -> Unit = {},
    onOpenLessonDetailDialog: (Lesson) -> Unit = {},
    onOpenBookingDetailDialog: (es.jvbabi.vplanplus.domain.model.RoomBooking) -> Unit = {},
    onCloseLessonDetailDialog: () -> Unit = {},
    onBookRoomClicked: (Room, ZonedDateTime, ZonedDateTime) -> Unit = { _, _, _ -> },
    onCancelCurrentBooking: () -> Unit = {},
    onConfirmBooking: () -> Unit = {},
    onCloseBookRoomDialog: () -> Unit = {}
) {
    if (state.detailLesson != null) {
        LessonDialog(
            lesson = state.detailLesson,
            onCloseLessonDetailDialog = onCloseLessonDetailDialog
        )
    }

    if (state.detailBooking != null) {
        BookingDetailDialog(
            booking = state.detailBooking,
            onCloseBookingDetailDialog = {
                onCloseLessonDetailDialog()
            },
            onCancelBooking = onCancelCurrentBooking,
            userIsAuthor = state.detailBooking.bookedBy?.id == state.identity?.vppId?.id && state.identity?.vppId != null
        )
    }

    if (state.currentRoomBooking != null) {
        when (state.canBookRoom) {
            BookRoomAbility.NO_VPP_ID -> CannotBookRoomNotVerifiedDialog {
                onCloseBookRoomDialog()
            }

            BookRoomAbility.WRONG_TYPE -> CannotBookRoomWrongTypeDialog {
                onCloseBookRoomDialog()
            }

            else -> ComposableDialog(
                icon = Icons.Default.MeetingRoom,
                title = stringResource(
                    id = R.string.searchAvailableRoom_bookTitle,
                    state.currentRoomBooking.room.name
                ),
                content = {
                    Column {
                        Badge(
                            color = MaterialTheme.colorScheme.primary,
                            text = stringResource(id = R.string.beta)
                        )
                        Text(
                            text = stringResource(
                                id = R.string.searchAvailableRoom_bookText,
                                state.currentRoomBooking.start.toZonedLocalDateTime().format(
                                    DateTimeFormatter.ofPattern("HH:mm")
                                ),
                                state.currentRoomBooking.end.toZonedLocalDateTime().format(
                                    DateTimeFormatter.ofPattern("HH:mm")
                                ),
                                state.`class`!!.name
                            )
                        )
                    }
                },
                okEnabled = state.canBookRoom == BookRoomAbility.CAN_BOOK,
                onDismiss = onCloseBookRoomDialog,
                onCancel = onCloseBookRoomDialog,
                onOk = {
                    onConfirmBooking()
                },
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.search_searchAvailableRoom)) },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        BackIcon()
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // user info
            Text(text = state.identity?.school?.name ?: stringResource(id = R.string.loadingData))
            Guide(className = state.`class`?.name)

            // filter
            SearchField(state.roomFilter) { onRoomFilterValueChanged(it) }
            if (state.showFilterChips) FilterChips(
                filterNowActive = state.filterNow,
                filterNextActive = state.filterNext,
                filterNowToggled = { onNowToggled() },
                filterNextToggled = { onNextToggled() },
                filterNowTimespan = state.filterNowTimespan,
                filterNextTimespan = state.filterNextTimespan,
                showNowFilter = state.showNowFilter
            )
            if (state.loading || state.rooms == null) {
                Loading()
                return@Scaffold
            }

            // matrix
            val scaling = 1f

            val lessonTimes = state.rooms.rooms
                .flatMap { it.lessons }
                .filterNotNull()

            val first = lessonTimes.minByOrNull { it.start }
            val last = lessonTimes.maxByOrNull { it.end }
            val width = (first?.start
                ?.until(last?.end, ChronoUnit.MINUTES) ?: 0) * scaling
            val currentOffset =
                ((state.profileStart ?: first?.start)?.until(
                    ZonedDateTime.now(),
                    ChronoUnit.MINUTES
                ) ?: 0) * scaling
            var scrollWidth = 0

            val scrollState = rememberScrollState()
            val currentOffsetDp = LocalDensity.current.run { currentOffset.dp.roundToPx() }
            LaunchedEffect(key1 = scrollState.value) {
                Log.d(
                    "scroll",
                    "scrollState: ${scrollState.value} | $currentOffsetDp | $scrollWidth"
                )
            }

            LaunchedEffect(key1 = scrollWidth, block = {
                if (scrollWidth == 0) return@LaunchedEffect
                scrollState.animateScrollTo(currentOffsetDp - (scrollWidth / 2))
            })

            if (state.lessonTimes != null && first != null && state.profileStart != null) Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.width(50.dp))
                Box(
                    modifier = Modifier.width(width.dp)
                ) {
                    state.lessonTimes.entries.sortedBy { it.key }
                        .forEach { (lessonNumber, lessonTime) ->
                            if (lessonNumber == 0 && !state.showLesson0) return@forEach
                            val offset = state.profileStart.until(
                                lessonTime.start,
                                ChronoUnit.MINUTES
                            ) * scaling
                            val length = lessonTime.start.until(
                                lessonTime.end,
                                ChronoUnit.MINUTES
                            ) * scaling
                            Box(
                                modifier = Modifier
                                    .offset(x = offset.toInt().dp)
                                    .width(length.toInt().dp)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lessonNumber.toString(),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }
                }
            }

            if (state.rooms.rooms.isNotEmpty() && first != null && last != null) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 8.dp)
                        .verticalScroll(rememberScrollState())
                        .width(width.dp)
                ) {
                    // lesson data
                    Row(
                        modifier = Modifier
                            .padding(start = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .horizontalScroll(scrollState)
                                .onPlaced {
                                    scrollWidth = it.boundsInWindow().width.toInt()
                                }
                        ) {
                            Column {
                                state.rooms.rooms.sortedBy { it.room.name }.forEach { room ->
                                    Row {
                                        Spacer(modifier = Modifier.width(30.dp))
                                        RoomListRecord(
                                            start = state.profileStart
                                                ?: first.start,
                                            lessons = room.lessons,
                                            bookings = room.bookings,
                                            displayed = room.displayed,
                                            onLessonClicked = { lesson ->
                                                onOpenLessonDetailDialog(lesson)
                                            },
                                            onBookingClicked = {
                                                onOpenBookingDetailDialog(it)
                                            },
                                            onBookRoomClicked = { start, end ->
                                                onBookRoomClicked(room.room, start, end)
                                            },
                                            scaling = scaling,
                                            width = width.dp,
                                            currentClassName = state.`class`?.name,
                                            lessonTimes = state.lessonTimes
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // room names
                    Column {
                        state.rooms.rooms.sortedBy { it.room.name }.forEach { room ->
                            val height = animateFloatAsState(
                                targetValue = if (room.displayed) 1f else 0f,
                                label = "room entry"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(bottom = (4 * height.value).dp)
                                    .width(40.dp)
                                    .height((40 * height.value).dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = room.room.name,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null
                        )
                        Text(text = stringResource(id = R.string.search_noResultsFound))
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FindAvailableRoomScreenPreview() {
    val school = School.generateRandomSchools(1).first()
    val `class` = ClassesPreview.generateClass(school)
    val profile = ProfilePreview.generateClassProfile()
    FindAvailableRoomScreenContent(
        state = RoomSearchState(
            Identity(
                school,
                profile,
                VppIdPreview.generateVppId(`class`)
            ),
            loading = false,
            `class` = `class`,
            detailLesson = null,
            currentRoomBooking = RoomBooking(
                PreviewRoom.generateRoom(school),
                ZonedDateTime.now(),
                ZonedDateTime.now().plusHours(1)
            ),
        ),
        onBackClicked = {}
    )
}

@Composable
private fun RoomListRecord(
    start: ZonedDateTime,
    lessons: List<Lesson?>,
    bookings: List<es.jvbabi.vplanplus.domain.model.RoomBooking>,
    displayed: Boolean,
    onLessonClicked: (Lesson) -> Unit = {},
    onBookingClicked: (es.jvbabi.vplanplus.domain.model.RoomBooking) -> Unit = {},
    onBookRoomClicked: (start: ZonedDateTime, end: ZonedDateTime) -> Unit = { _, _ -> },
    scaling: Float = 1f,
    width: Dp,
    currentClassName: String?,
    lessonTimes: Map<Int, LessonTime>?
) {
    val height = animateFloatAsState(targetValue = if (displayed) 1f else 0f, label = "room entry")

    Box {
        Box(
            modifier = Modifier
                .padding(bottom = (4 * height.value).dp)
                .height((40 * height.value).dp)
        ) {
            Box(
                modifier = Modifier.width(width)
            ) {
                lessonTimes?.entries?.sortedBy { it.key }?.forEach { (lessonNumber, times) ->
                    if (!lessons.filterNotNull().any { it.lessonNumber == lessonNumber }) {

                        if (bookings.any { booking ->
                                (booking.from
                                    .isBefore(times.end) && booking.to
                                    .isAfter(
                                        times.start
                                    )) || (booking.from
                                    .isEqual(times.start)) || (booking.to
                                    .isEqual(
                                        times.end
                                    ))
                            }) return@forEach

                        val lessonStart = times.start.withYear(start.year).withDayOfYear(start.dayOfYear)
                        if (lessonStart.isBefore(start)) return@forEach
                        val offset = start.until(lessonStart, ChronoUnit.MINUTES) * scaling
                        val length = lessonStart.until(times.end.withYear(start.year).withDayOfYear(start.dayOfYear), ChronoUnit.MINUTES) * scaling
                        val prevHasLesson =
                            lessons.filterNotNull().any { it.lessonNumber == lessonNumber - 1 }
                        var roundPrevious = 1
                        if (!prevHasLesson && lessonTimes[lessonNumber - 1]?.end?.isEqual(times.start.withYear(start.year).withDayOfYear(start.dayOfYear)) == true) {
                            roundPrevious = 0
                        }

                        val nextHasLesson =
                            lessons.filterNotNull().any { it.lessonNumber == lessonNumber + 1 }
                        var roundNext = 1
                        if (!nextHasLesson && lessonTimes[lessonNumber + 1]?.start?.isEqual(times.end.withYear(start.year).withDayOfYear(start.dayOfYear)) == true) {
                            roundNext = 0
                        }
                        Box(
                            modifier = Modifier
                                .offset(x = offset.toInt().dp)
                                .width(length.toInt().dp)
                                .height(40.dp)
                                .clip(
                                    RoundedCornerShape(
                                        (roundPrevious * 8).dp,
                                        (roundNext * 8).dp,
                                        (roundNext * 8).dp,
                                        (roundPrevious * 8).dp
                                    )
                                )
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    onBookRoomClicked(times.start, times.end)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(
                                    id = R.string.searchAvailableRoom_bookDescription
                                ),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                bookings.forEach { booking ->
                    var bookingStart = booking.from
                    if (bookingStart.isBefore(start)) bookingStart = start
                    val bookingEnd = booking.to
                    val offset = start.until(bookingStart, ChronoUnit.MINUTES) * scaling
                    val length = bookingStart.until(bookingEnd, ChronoUnit.MINUTES) * scaling

                    Box(
                        modifier = Modifier
                            .offset(x = offset.toInt().dp)
                            .width(length.toInt().dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (currentClassName == booking.`class`.name) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.error)
                            .clickable { onBookingClicked(booking) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = booking.`class`.name,
                            color = if (currentClassName == booking.`class`.name) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onError,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
                lessons.filterNotNull().forEach { lesson ->
                    var lessonStart = lesson.start
                    val lessonEnd = lesson.end
                    if (lessonStart.isBefore(start)) lessonStart = start
                    val offset = start.until(lessonStart, ChronoUnit.MINUTES) * scaling
                    val length = lessonStart.until(lessonEnd, ChronoUnit.MINUTES) * scaling
                    Box(
                        modifier = Modifier
                            .offset(x = offset.toInt().dp)
                            .width(length.toInt().dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (currentClassName == lesson.`class`.name) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.error)
                            .clickable { onLessonClicked(lesson) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = lesson.`class`.name,
                            color = if (currentClassName == lesson.`class`.name) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onError,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }
        val current = start.atBeginningOfTheWorld().until(
            ZonedDateTime.now().atBeginningOfTheWorld(),
            ChronoUnit.MINUTES
        ) * scaling
        if (current >= 0) Box(
            modifier = Modifier
                .height((44 * height.value).dp)
                .width(2.dp)
                .offset(x = current.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}

@Preview
@Composable
private fun RoomListRecordPreview() {
    RoomListRecord(
        start = ZonedDateTime.of(1970, 1, 1, 7, 30, 0, 0, ZoneId.of("Europe/Berlin")),
        lessons = Array(12) {
            if (Random.nextBoolean()) Lessons.generateLessons(1).first() else null
        }.toList(),
        bookings = emptyList(),
        true,
        width = 200.dp,
        currentClassName = "12a",
        lessonTimes = null,
    )
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}