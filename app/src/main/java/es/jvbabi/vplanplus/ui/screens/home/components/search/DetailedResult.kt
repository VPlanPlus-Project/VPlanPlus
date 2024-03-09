package es.jvbabi.vplanplus.ui.screens.home.components.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import es.jvbabi.vplanplus.domain.usecase.home.search.SearchResult
import es.jvbabi.vplanplus.ui.preview.Lessons
import es.jvbabi.vplanplus.util.DateUtils.progress
import java.time.ZonedDateTime
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailedResult(
    result: SearchResult,
    time: ZonedDateTime,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column {
            Text(
                text = result.name,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                if (result.lessons.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(65.dp + 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.search_noLessons),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                result.lessons.forEach { lesson ->
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
                                    text = when (result.type) {
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
}

@Preview
@Composable
private fun DetailedSearchPreview() {
    DetailedResult(
        result = SearchResult(
            UUID.randomUUID(),
            Lessons.randomRoom().first().name,
            SchoolEntityType.ROOM,
            Lessons.generateLessons(5, true)
        ),
        time = ZonedDateTime.now()
    )
}