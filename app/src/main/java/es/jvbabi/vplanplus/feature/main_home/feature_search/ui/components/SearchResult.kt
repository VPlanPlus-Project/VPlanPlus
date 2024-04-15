package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.SearchResult
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.util.DateUtils.progress
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchResult(
    searchResult: SearchResult,
    time: ZonedDateTime
) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(text = searchResult.name, style = MaterialTheme.typography.headlineSmall)
            if (searchResult.lessons == null) return@Row
            Text(
                text = DOT,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            if (searchResult.lessons.isEmpty()) Text(
                text = stringResource(id = R.string.search_noLessons),
                style = MaterialTheme.typography.bodyMedium
            )
            else Text(
                text = stringResource(
                    id = R.string.search_lessons,
                    searchResult.lessons.size
                ), style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = searchResult.school,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
        LazyRow(modifier = Modifier.padding(top = 8.dp)) {
            item {
                Spacer(modifier = Modifier.width(8.dp))
            }
            items(searchResult.lessons ?: emptyList()) { lesson ->
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 8.dp)
                        .size(65.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardDefaults.cardColors().containerColor),
                ) {
                    val progress = time.progress(lesson.start, lesson.end)
                    if (progress > 0) Box(modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .fillMaxWidth(minOf(progress, 1f))
                        .fillMaxHeight()
                    ) // Progress bar
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .basicMarquee(
                                    iterations = Int.MAX_VALUE,
                                    velocity = 80.dp,
                                    spacing = MarqueeSpacing(12.dp)
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        )  {
                            Text(
                                text = lesson.lessonNumber.toString() + ". " + lesson.displaySubject,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = when (searchResult.type) {
                                    SchoolEntityType.TEACHER -> lesson.`class`.name + " • " + lesson.rooms.joinToString(
                                        ", "
                                    )

                                    SchoolEntityType.ROOM -> lesson.`class`.name + " • " + lesson.teachers.joinToString(
                                        ", "
                                    )

                                    SchoolEntityType.CLASS -> lesson.teachers.joinToString(
                                        ", "
                                    ) + " • " + lesson.rooms.joinToString(", ")
                                },
                                style = MaterialTheme.typography.labelSmall
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
fun SearchResultPreview() {
    SearchResult(
        SearchResult(
            name = "10c",
            type = SchoolEntityType.CLASS,
            school = "Einstein-School",
            lessons = emptyList(),
            bookings = emptyList()
        ),
        ZonedDateTime.now()
    )
}