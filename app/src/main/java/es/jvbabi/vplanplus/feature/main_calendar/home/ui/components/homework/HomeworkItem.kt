package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.homework

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun HomeworkItem(
    onOpenHomeworkScreen: (homeworkId: Int) -> Unit,
    currentProfile: Profile,
    hw: PersonalizedHomework,
    contextDate: LocalDate,
    showUntil: Boolean = true
) {
    RowVerticalCenter(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable { onOpenHomeworkScreen(hw.homework.id) }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProgressRing(hw, contextDate)
        Column {
            Title(hw, currentProfile, contextDate, showUntil)
            Tasks(hw)
        }
    }
}

@Composable
private fun Tasks(hw: PersonalizedHomework) {
    if (hw.tasks.isEmpty()) return
    Text(
        text = buildAnnotatedString {
            val style =
                MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ).toSpanStyle()
            hw.tasks.sortedBy { it.isDone }
                .forEachIndexed { i, task ->
                    withStyle(
                        if (task.isDone) style.copy(
                            textDecoration = TextDecoration.LineThrough
                        ) else style
                    ) {
                        append(task.content)
                        append(if (i != hw.tasks.size - 1) ", " else "")
                    }
                }
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun Title(
    hw: PersonalizedHomework,
    currentProfile: Profile,
    contextDate: LocalDate,
    showUntil: Boolean = true
) {
    RowVerticalCenter(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Subject
        Text(
            text = hw.homework.defaultLesson?.subject
                ?: stringResource(id = R.string.homework_noSubject),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                textDecoration = if (hw.allDone()) TextDecoration.LineThrough else null
            ),
            color = if (!hw.allDone() && hw.homework.until.toLocalDate().isBefore(contextDate)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = when (hw) {
                is PersonalizedHomework.LocalHomework -> stringResource(
                    id = R.string.homework_thisDevice
                )

                is PersonalizedHomework.CloudHomework -> {
                    if (hw.homework.createdBy.id == (currentProfile as? ClassProfile)?.vppId?.id) stringResource(
                        id = R.string.homework_you
                    )
                    else hw.homework.createdBy.name
                }
            },
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        if (showUntil) {
            Text(
                text = stringResource(
                    id = R.string.homework_dueTo,
                    DateUtils.localizedRelativeDate(LocalContext.current, hw.homework.until.toLocalDate(), true) ?: ""
                ),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun ProgressRing(
    hw: PersonalizedHomework,
    contextDate: LocalDate
) {
    Box(
        modifier = Modifier
            .size(32.dp),
        contentAlignment = Alignment.Center
    ) subjectIcon@{
        val circularProgressIndicatorPadding = 3
        SubjectIcon(
            subject = hw.homework.defaultLesson?.subject,
            tint = if (!hw.allDone() && hw.homework.until.toLocalDate().isBefore(contextDate)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(
                    sqrt(
                        ((32 - circularProgressIndicatorPadding) / 2f).pow(
                            2
                        ) * 2
                    ).dp
                ),
        )
        CircularProgressIndicator(
            progress = {
                hw.tasks.count { it.isDone }
                    .toFloat() / hw.tasks.size.coerceAtLeast(
                    1
                )
            },
            modifier = Modifier.size(32.dp),
            trackColor = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.2f
            ),
            color = if (!hw.allDone() && hw.homework.until.toLocalDate().isBefore(contextDate)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            strokeWidth = circularProgressIndicatorPadding.dp
        )
    }
}

@Composable
@Preview(showBackground = true)
fun HomeworkItemPreview() {
    val group = GroupPreview.generateGroup()
    val classProfile = ProfilePreview.generateClassProfile(group)
    HomeworkItem(
        onOpenHomeworkScreen = {},
        hw = PersonalizedHomework.LocalHomework(
            homework = HomeworkCore.LocalHomework(
                id = -1,
                tasks = emptyList(),
                createdAt = ZonedDateTime.now().minusDays(1L),
                until = ZonedDateTime.now().plusDays(1L),
                documents = emptyList(),
                profile = classProfile,
                defaultLesson = null
            ),
            profile = classProfile,
            tasks = emptyList()
        ),
        currentProfile = classProfile,
        contextDate = LocalDate.now().plusDays(5)
    )
}