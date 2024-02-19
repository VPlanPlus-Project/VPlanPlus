package es.jvbabi.vplanplus.feature.grades.ui.components.grades

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.grades.domain.model.Grade

@Composable
fun LatestGrades(grades: List<Grade>) {
    Column {
        Text(text = stringResource(id = R.string.grades_latest), style = MaterialTheme.typography.headlineSmall)
        grades.forEach { grade ->
            GradeRecord(grade = grade, showSubject = true)
        }
    }

}