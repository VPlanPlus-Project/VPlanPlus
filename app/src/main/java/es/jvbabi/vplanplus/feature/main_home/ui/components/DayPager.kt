package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate

private const val PAGER_SIZE = 200
private const val FLING_ITEMS = 10

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DayPager(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    today: LocalDate,
    onDateSelected: (date: LocalDate) -> Unit = {}
) {
    val pagerState = rememberPagerState(initialPage = PAGER_SIZE / 2) { PAGER_SIZE }
    val fling = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(FLING_ITEMS),
    )

    LaunchedEffect(key1 = selectedDate) {
        pagerState.animateScrollToPage(maxOf( selectedDate.toPagerIndex(today) - 2, 0))
    }

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSize = PageSize.Fixed(68.dp),
        verticalAlignment = Alignment.Top,
        beyondBoundsPageCount = FLING_ITEMS,
        flingBehavior = fling
    ) {
        val date = it.toPagerDate(today)
        DateCard(date = date, isSelected = selectedDate.isEqual(date), onClick = onDateSelected, modifier = Modifier.padding(horizontal = 4.dp))
    }
}

private fun LocalDate.toPagerIndex(today: LocalDate): Int {
    return today.until(this).days + PAGER_SIZE / 2
}

private fun Int.toPagerDate(today: LocalDate): LocalDate {
    return today.plusDays((this - PAGER_SIZE / 2).toLong())
}

@Preview
@Composable
fun DayPagerPreview() {
    DayPager(
        selectedDate = LocalDate.now(),
        today = LocalDate.now()
    )
}