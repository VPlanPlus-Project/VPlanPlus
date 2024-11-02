package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam

import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Exam
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp

@Composable
fun ExamSection(
    showSection: Boolean,
    includeTitle: Boolean = true,
    exams: List<Exam>,
    currentProfile: Profile,
    onOpenExamScreen: (examId: Int) -> Unit
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = showSection,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column {
            if (includeTitle) {
                Title()
                Spacer4Dp()
            }
            Column(
                modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                exams.forEach { exam ->
                    ExamItem(
                        currentProfile = currentProfile,
                        exam = exam,
                        onOpenExamScreen = onOpenExamScreen
                    )
                }
            }
            Spacer8Dp()
        }
    }
}



@Composable
private fun ColumnScope.Title() {
    Spacer8Dp()
    RowVerticalCenter(
        modifier = Modifier.align(CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.height(16.dp)
        )
        Text(
            text = stringResource(id = R.string.calendar_dayFilterExams),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
    Spacer4Dp()
}