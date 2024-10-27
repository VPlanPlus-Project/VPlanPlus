package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.Option
import es.jvbabi.vplanplus.ui.common.OptionTextTitle
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.getSubjectIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExamSubjectSection(
    subjects: Set<DefaultLesson>,
    currentSubjects: Set<DefaultLesson>,
    selectedSubject: DefaultLesson?,
    isDeveloperModeEnabled: Boolean,
    onSubjectClicked: (subject: DefaultLesson) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isSubjectModalOpen by rememberSaveable { mutableStateOf(false) }
    if (isSubjectModalOpen) {
        val subjectSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { isSubjectModalOpen = false },
            sheetState = subjectSheetState,
        ) {
            Column(Modifier.padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())) {
                Text(
                    text = stringResource(id = R.string.examsNew_subject),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .verticalScroll(rememberScrollState())
                ) {
                    remember { subjects.sortedBy { it.subject } }.forEach { subject ->
                        Option(
                            title = OptionTextTitle(buildString {
                                append(subject.subject)
                                if (subject.courseGroup != null) append (" (${subject.courseGroup})")
                                if (isDeveloperModeEnabled) append(" $DOT VP-ID: ${subject.vpId}")
                            }),
                            subtitle = subject.teacher?.acronym ?: stringResource(id = R.string.addHomework_lessonSubtitleNoTeacher),
                            icon = subject.subject.getSubjectIcon(),
                            state = subject.vpId == selectedSubject?.vpId,
                            enabled = true,
                            modifier = Modifier.border(width = .25.dp, color = MaterialTheme.colorScheme.outline),
                        ) {
                            onSubjectClicked(subject)
                            scope.launch { subjectSheetState.hide(); isSubjectModalOpen = false }
                        }
                    }
                }
            }
        }
    }
    AddExamItem(
        icon = {
            AnimatedContent(
                targetState = selectedSubject,
                label = "subject"
            ) { subject ->
                Icon(
                    imageVector = subject?.subject?.getSubjectIcon() ?: Icons.Outlined.School,
                    contentDescription = null,
                    tint = if (subject == null) Color.Gray else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { isSubjectModalOpen = true }
                    .padding(start = 8.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                AnimatedContent(
                    targetState = selectedSubject,
                    label = "subject"
                ) { displaySubject ->
                    Text(
                        text = buildString {
                            if (displaySubject == null) {
                                append(stringResource(R.string.examsNew_subject))
                                return@buildString
                            }
                            val subject = buildString {
                                append(displaySubject.subject)
                                if (displaySubject.courseGroup != null) append (" (${displaySubject.courseGroup})")
                            }
                            if (displaySubject.teacher == null) append(stringResource(R.string.examsNew_subjectLabelWithoutTeacher, subject))
                            else append(stringResource(R.string.examsNew_subjectLabelWithTeacher, subject, displaySubject.teacher.acronym))
                            if (isDeveloperModeEnabled) append(" $DOT VP-ID: ${displaySubject.vpId}")
                        },
                        color = if (displaySubject == null) Color.Gray else MaterialTheme.colorScheme.onSurface,
                        style = if (displaySubject == null) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            if (currentSubjects.isNotEmpty()) RowVerticalCenter(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                remember { currentSubjects.sortedBy { it.subject } }.forEach { currentSubject ->
                    AssistChip(
                        onClick = { onSubjectClicked(currentSubject) },
                        leadingIcon = {Icon(imageVector = currentSubject.getSubjectIcon(), contentDescription = null)},
                        label = { Text(currentSubject.subject) }
                    )
                }
            }
        }
    }
}