package es.jvbabi.vplanplus.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.common.toLocalizedString

@Composable
fun NextDaySubjectCard(
    modifier: Modifier = Modifier,
    subject: String,
    lessonNumbers: List<Int>,
    homework: Int
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubjectIcon(
            subject = subject,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(40.dp)
        )
        Column {
            Text(text = subject, style = MaterialTheme.typography.titleMedium)
            var subtext = stringResource(
                id = R.string.home_nextDayLessonDescription,
                lessonNumbers.joinToString(", ") { it.toLocalizedString() })
            if (homework > 0) {
                subtext += "\n" + pluralStringResource(
                    id = R.plurals.home_nextDayHomework,
                    homework,
                    homework
                )
            }
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}