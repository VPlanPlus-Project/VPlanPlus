package es.jvbabi.vplanplus.feature.ndp.ui.guided

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import es.jvbabi.vplanplus.domain.model.Exam

/**
 * @param assessments Map of exams to reminder (false) or next day (true)
 */
@Composable
fun NdpAssessmentScreen(
    assessments: Map<Exam, Boolean>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        assessments.forEach { (exam, isNextDay) ->
            Text(exam.title)
            Text(if (isNextDay) "Next day" else "Reminder")
        }
    }
}