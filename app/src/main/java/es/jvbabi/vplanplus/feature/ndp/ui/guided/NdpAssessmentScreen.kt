package es.jvbabi.vplanplus.feature.ndp.ui.guided

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.ExamType
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.ui.common.Spacer2Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.stringResource
import es.jvbabi.vplanplus.util.DateUtils.atStartOfDay
import es.jvbabi.vplanplus.util.toDp
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * @param assessments Map of exams to reminder (false) or next day (true)
 */
@Composable
fun NdpAssessmentScreen(
    assessments: Map<Exam, Boolean>,
    enabled: Boolean,
    onContinue: () -> Unit,
    onOpenAssessment: (exam: Exam) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer16Dp()
            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                assessments.forEach { (exam, isNextDay) ->
                    AssessmentItem(exam, isNextDay) {
                        onOpenAssessment(exam)
                    }
                }
            }
        }

        Box(Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onContinue,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp),
                enabled = enabled
            ) {
                RowVerticalCenter {
                    Icon(Icons.Default.DoneAll, contentDescription = null)
                    Spacer4Dp()
                    Text(stringResource(R.string.ndp_guidedAssessmentDone))
                }
            }
        }
    }
}

@Composable
fun AssessmentItem(
    exam: Exam,
    isNextDay: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        RowVerticalCenter {
            Spacer8Dp()
            SubjectIcon(
                subject = exam.subject?.subject,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer8Dp()
            Text(
                text = buildString {
                    append(stringResource(exam.type.stringResource()))
                    if (exam.subject != null) append(": ${exam.subject.subject}")
                    append(" - " + exam.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)))
                }
            )
        }
        Row {
            Spacer16Dp()
            Spacer16Dp()
            Spacer8Dp()
            Column {
                Text(text = exam.title)
                if (exam.description != null) Text(text = exam.description)
                Spacer4Dp()
                if (isNextDay) Row {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.typography.bodySmall.lineHeight.toDp()),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer2Dp()
                    Text(
                        text = stringResource(R.string.ndp_guidedAssessmentNextDay),
                        style = MaterialTheme.typography.bodySmall
                    )
                } else Row {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.typography.bodySmall.lineHeight.toDp()),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer2Dp()
                    Text(
                        text = stringResource(R.string.ndp_guidedAssessmentReminder),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewAssessmentItem() {
    val school = SchoolPreview.generateRandomSchool()
    val group = GroupPreview.generateGroup(school)
    AssessmentItem(
        exam = Exam(
            title = "Test",
            date = LocalDate.now(),
            subject = null,
            id = -5,
            assessmentReminders = emptySet(),
            createdAt = ZonedDateTime.now().atStartOfDay().minusDays(2),
            type = ExamType.Oral,
            description = null,
            group = group,
            createdBy = null
        ),
        isNextDay = true,
        onClick = {}
    )
}