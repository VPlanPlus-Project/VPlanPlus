package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.result.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.util.DateUtils.progress
import java.time.ZonedDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchResultLesson(
    lesson: Lesson,
    resultType: SchoolEntityType,
    currentTime: ZonedDateTime
) {
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
        val progress = currentTime.progress(lesson.start, lesson.end)
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
                    text = when (resultType) {
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