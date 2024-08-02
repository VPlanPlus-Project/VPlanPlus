package es.jvbabi.vplanplus.feature.main_homework.add.ui.components.default_lesson_dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.ui.common.Option
import es.jvbabi.vplanplus.ui.common.getSubjectIcon
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

            if (defaultLessons.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nothing_found),
                        contentDescription = null,
                        modifier = Modifier.size(92.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.addHomework_noDefaultLessonsFound),
                        textAlign = TextAlign.Center
                    )
                }
            }

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
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .verticalScroll(rememberScrollState())
            ) {
                defaultLessons.sortedBy { it.subject }.forEach defaultLessonOptions@{ defaultLesson ->
                    Option(
                        title = buildString {
                            append(defaultLesson.subject)
                            if (defaultLesson.courseGroup != null) append (" (${defaultLesson.courseGroup})")
                        },
                        subtitle = defaultLesson.teacher?.acronym ?: stringResource(id = R.string.addHomework_lessonSubtitleNoTeacher),
                        icon = defaultLesson.getSubjectIcon(), state = defaultLesson == selectedDefaultLesson, enabled = true,
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