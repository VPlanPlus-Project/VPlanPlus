package es.jvbabi.vplanplus.feature.grades.ui.components.grades

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.feature.grades.ui.SubjectGradeCollection
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import java.math.RoundingMode

@Composable
fun GradeSubjectGroup(grades: SubjectGradeCollection) {
    if (grades.grades.isEmpty()) return

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SubjectIcon(
                    subject = grades.subject.short,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Column {
                    Text(
                        text = grades.subject.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Ø ${grades.avg.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)} | " + grades.grades.map { "${it.givenBy.firstname} ${it.givenBy.lastname}" }.toSet().joinToString(", "),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            grades.grades.groupBy { it.type }.entries.forEachIndexed { i, (_, grades) ->
                if (i != grades.groupBy { it.type }.size - 1) HorizontalDivider()
                grades.forEach { grade ->
                    GradeRecord(grade)
                }
            }
        }
    }
}