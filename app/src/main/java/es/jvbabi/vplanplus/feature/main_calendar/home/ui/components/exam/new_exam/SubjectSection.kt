package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.Option
import es.jvbabi.vplanplus.ui.common.getSubjectIcon

@Composable
fun SubjectSection(
    subjects: List<DefaultLesson>,
    isExpanded: Boolean,
    selectedSubject: DefaultLesson?,
    onHeaderClicked: () -> Unit,
    onSubjectClicked: (subject: DefaultLesson) -> Unit
) {
    Section(
        title = {
            TitleRow(
                title = stringResource(R.string.examsNew_subject),
                subtitle = selectedSubject?.let { subject ->
                    buildString {
                        append(subject.subject)
                        if (subject.teacher != null) append(" $DOT ${subject.teacher.acronym}")
                        if (BuildConfig.DEBUG) append(" $DOT ${subject.vpId}")
                    }
                } ?: stringResource(R.string.examsNew_subject_noSubject),
                icon = Icons.Default.School,
                onClick = onHeaderClicked
            )
        },
        isVisible = true,
        isContentExpanded = isExpanded,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .verticalScroll(rememberScrollState())
        ) {
            subjects.forEach { subject ->
                Option(
                    title = subject.subject,
                    subtitle = subject.teacher?.acronym,
                    icon = subject.subject.getSubjectIcon(),
                    state = subject == selectedSubject,
                    enabled = true,
                    onClick = { onSubjectClicked(subject) }
                )
                HorizontalDivider()
            }
        }
    }
}