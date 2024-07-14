package es.jvbabi.vplanplus.feature.main_homework.view.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.getSubjectIcon

@Composable
fun RowScope.DefaultLessonCard(defaultLesson: DefaultLesson?) {
    BigCustomCard(
        modifier = Modifier.weight(1f, true),
        icon = defaultLesson.getSubjectIcon(),
        title = stringResource(id = R.string.homework_detailViewSubject),
        content = {
            RowVerticalCenter(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = defaultLesson?.subject ?: stringResource(id = R.string.homework_detailViewNoSubject),
                    style = MaterialTheme.typography.bodyMedium
                )
                if (defaultLesson?.subject != null) Text(
                    text = defaultLesson.teacher?.acronym ?: stringResource(id = R.string.addHomework_lessonSubtitleNoTeacher),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DefaultLessonCardPreview() {
    Row {
        DefaultLessonCard(null)
    }
}