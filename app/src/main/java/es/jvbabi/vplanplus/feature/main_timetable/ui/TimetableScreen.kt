@file:OptIn(ExperimentalFoundationApi::class)

package es.jvbabi.vplanplus.feature.main_timetable.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.DayDataState
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.feature.main_home.ui.components.LessonCard
import es.jvbabi.vplanplus.feature.main_timetable.ui.components.Holiday
import es.jvbabi.vplanplus.feature.main_timetable.ui.components.NoData
import es.jvbabi.vplanplus.feature.main_timetable.ui.components.Weekend
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.DOT
import java.time.LocalDate
import java.time.Period
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.floor

const val PAGES = 201

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimetableScreen(
    navHostController: NavHostController,
    startDate: LocalDate = LocalDate.now(),
    viewModel: TimetableViewModel = hiltViewModel(),
    navBar: @Composable (expanded: Boolean) -> Unit
) {
    val state = viewModel.state.value

    val pagerState = rememberPagerState(
        (floor(PAGES / 2.0) + LocalDate.now().until(startDate, ChronoUnit.DAYS)).toInt()
    ) { 200 }

    LaunchedEffect(key1 = state.date, block = {
        pagerState.animateScrollToPage(PAGES /2+ Period.between(LocalDate.now(), state.date).days)
    })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val date = LocalDate.now().plusDays(page - PAGES / 2L)
            viewModel.init(date, true)
            viewModel.setDate(date)
        }
    }

    Scaffold(
        bottomBar = { navBar(true) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            TimetableContent(
                state = state,
                pagerState = pagerState,
                onDateChanged = { viewModel.setDate(it) },
                onBack = { navHostController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun TimetableContent(
    state: TimetableState,
    pagerState: PagerState,
    onDateChanged: (LocalDate) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.timetable_title))
                        Text(
                            text = LocalDate.now().plusDays(pagerState.currentPage - PAGES / 2L).format(
                                DateTimeFormatter.ofPattern("dd.MM.yyyy")
                            ) + " $DOT ${state.activeProfile?.displayName}",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        BackIcon()
                    }
                },
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            HorizontalPager(state = pagerState) {
                val pageDate = LocalDate.now().plusDays((it - floor(PAGES / 2.0)).toLong())
                if (state.days[pageDate] == null || state.days[pageDate]?.state == DayDataState.NO_DATA) {
                    NoData(pageDate)
                    return@HorizontalPager
                }
                if (state.days[pageDate]?.type == DayType.WEEKEND) {
                    Weekend(pageDate)
                    return@HorizontalPager
                }
                if (state.days[pageDate]?.type == DayType.HOLIDAY) {
                    Holiday()
                    return@HorizontalPager
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.days[pageDate]!!.lessons.groupBy { l -> l.lessonNumber }
                        .toList()) { (_, lessons) ->
                        Box(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            LessonCard(
                                lessons = lessons,
                                bookings = emptyList(),
                                homework = emptyList(),
                                time = ZonedDateTime.now(),
                                allowActions = false,
                                profileType = state.activeProfile?.type ?: ProfileType.STUDENT
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(64.dp))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onDateChanged(state.date.minusDays(1)) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                    )
                }
                VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))
                TextButton(onClick = { onDateChanged(LocalDate.now()) }) {
                    Text(text = stringResource(id = R.string.timetable_toToday))
                }
                VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))
                IconButton(onClick = { onDateChanged(state.date.plusDays(1)) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TimetablePreview() {
    TimetableContent(
        state = TimetableState(isLoading = false),
        pagerState = rememberPagerState(0) { 0 },
        onBack = {}
    )
}