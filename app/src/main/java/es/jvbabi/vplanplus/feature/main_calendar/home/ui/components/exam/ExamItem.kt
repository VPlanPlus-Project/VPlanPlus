package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.util.formatDayDuration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ExamItem(
    currentProfile: Profile,
    exam: Exam,
    isReminder: Boolean = false,
    onOpenExamScreen: (examId: Int) -> Unit
) {
    RowVerticalCenter(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onOpenExamScreen(exam.id) }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(exam, isReminder)
        Column {
            Title(exam, isReminder, currentProfile)
            Text(
                text = exam.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun Title(
    exam: Exam,
    isReminder: Boolean,
    currentProfile: Profile,
) {
    RowVerticalCenter(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = (exam.subject?.subject ?: stringResource(id = R.string.homework_noSubject)).let { subject ->
                if (isReminder) stringResource(id = R.string.calendar_dayExamReminder, subject)
                else subject
            },
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = buildString {
                append(
                    when (exam.getAuthor()) {
                        null -> stringResource(id = R.string.homework_thisDevice)
                        (currentProfile as? ClassProfile)?.vppId -> stringResource(id = R.string.homework_you)
                        else -> exam.createdBy.name
                    }
                )
                if (isReminder) {
                    append(" $DOT ")
                    append(exam.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                    append(", ")
                    append(LocalDate.now().formatDayDuration(exam.date))
                }
            },
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun Icon(
    exam: Exam,
    isReminder: Boolean
) {
    Box(
        modifier = Modifier
            .size(32.dp),
        contentAlignment = Alignment.Center
    ) subjectIcon@{
        if (isReminder) androidx.compose.material3.Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        ) else SubjectIcon(
            subject = exam.subject?.subject,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
    }
}