package es.jvbabi.vplanplus.feature.main_homework.add.ui.components.default_lesson_dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.ui.common.Option
import es.jvbabi.vplanplus.ui.common.getIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDefaultLessonSheet(
    defaultLessons: List<DefaultLesson>,
    selectedDefaultLesson: DefaultLesson?,
    hasDefaultLessonsFiltered: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSelectDefaultLesson: (DefaultLesson?) -> Unit
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RectangleShape
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            DefaultLessonSheetTitle(hasDefaultLessonsFiltered)

            AnimatedVisibility(
                visible = selectedDefaultLesson != null,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                ) {
                    Option(
                        title = stringResource(id = R.string.addHomework_removeSubjectTitle),
                        icon = Icons.Default.Close,
                        state = false,
                        enabled = true,
                        modifier = Modifier.border(width = .25.dp, color = MaterialTheme.colorScheme.outline),
                    ) {
                        onSelectDefaultLesson(null)
                        scope.launch { sheetState.hide(); onDismiss() }
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .verticalScroll(rememberScrollState())
            ) {
                defaultLessons.sortedBy { it.subject }.forEach defaultLessonOptions@{ defaultLesson ->
                    Option(
                        title = defaultLesson.subject,
                        subtitle = defaultLesson.teacher?.acronym ?: stringResource(id = R.string.addHomework_lessonSubtitleNoTeacher),
                        icon = defaultLesson.getIcon(), state = defaultLesson == selectedDefaultLesson, enabled = true,
                        modifier = Modifier.border(width = .25.dp, color = MaterialTheme.colorScheme.outline),
                    ) {
                        onSelectDefaultLesson(defaultLesson)
                        scope.launch { sheetState.hide(); onDismiss() }
                    }
                }
            }
        }
    }
}