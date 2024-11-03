package es.jvbabi.vplanplus.feature.main_home.ui.components.content.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.lessons.LessonBlock

@Composable
fun LessonsForDayBlock(
    modifier: Modifier = Modifier,
    followingLessons: Map<Int, List<Lesson>>,
    horizontalPadding: Boolean = true
) {
    Column(
        modifier = modifier
            .padding(horizontal = if (horizontalPadding) 12.dp else 0.dp)
            .clip(RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        followingLessons
            .forEach { (lessonNumber, lessons) ->
                LessonBlock(
                    lessonNumber = lessonNumber,
                    lessons = lessons,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer
                )
            }
    }
}
