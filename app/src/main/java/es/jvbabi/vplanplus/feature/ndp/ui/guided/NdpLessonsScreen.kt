package es.jvbabi.vplanplus.feature.ndp.ui.guided

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.common.toLocalizedString
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun NdpLessonsScreen(
    lessons: List<Lesson>,
    enabled: Boolean,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) content@{

            Spacer16Dp()
            RowVerticalCenter {
                Spacer8Dp()
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = null
                )
                Spacer8Dp()
                Text(
                    text = stringResource(R.string.ndp_guidedLessonStart,
                        lessons.minOf { it.start }
                            .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
                    ),
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Spacer8Dp()

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                lessons.groupBy { it.subject + (it as? Lesson.SubstitutionPlanLesson)?.defaultLesson?.courseGroup }.forEach { (_, lessons) ->
                    RowVerticalCenter(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp)
                    ) {
                        SubjectIcon(
                            subject = lessons.first().subject,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer8Dp()
                        Column {
                            Text(
                                text = buildString {
                                    append(lessons.first().subject)
                                    if ((lessons.first() as? Lesson.SubstitutionPlanLesson)?.defaultLesson?.courseGroup != null) {
                                        append(" $DOT ${(lessons.first() as? Lesson.SubstitutionPlanLesson)?.defaultLesson?.courseGroup}")
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = buildString {
                                    append(
                                        stringResource(R.string.ndp_guidedLessonNumber, lessons.map { it.lessonNumber }.distinct().sorted().joinToString(", ") {
                                            it.toLocalizedString()
                                        }
                                        )
                                    )
                                },
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer8Dp()

            RowVerticalCenter {
                Spacer8Dp()
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
                Spacer8Dp()
                Text(
                    text = stringResource(R.string.ndp_guidedLessonEnd,
                        lessons.maxOf { it.end }
                            .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
                    ),
                    style = MaterialTheme.typography.titleSmall
                )
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
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer4Dp()
                    Text(stringResource(R.string.ndp_guidedHomeworkLessonsAcknowledge))
                }
            }
        }
    }
}