package es.jvbabi.vplanplus.feature.main_grades.ui.view.components.grades

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_grades.data.source.example.ExampleInterval
import es.jvbabi.vplanplus.feature.main_grades.data.source.example.ExampleYear
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.domain.model.GradeModifier
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Interval
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Subject
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Teacher
import es.jvbabi.vplanplus.feature.main_grades.ui.view.SubjectGradeCollection
import es.jvbabi.vplanplus.ui.common.SubjectIcon
import es.jvbabi.vplanplus.ui.preview.ClassesPreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import java.math.RoundingMode
import java.time.LocalDate

@Composable
fun GradeSubjectGroup(
    grades: SubjectGradeCollection,
    onStartCalculator: () -> Unit = {},
    withIntervals: Set<Interval>
) {
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
                Column(
                    modifier = Modifier.weight(1f, true)
                ) {
                    Text(
                        text = grades.subject.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    val avg = grades.grades.filter { withIntervals.contains(it.interval) }.groupBy { it.type }.map { it.value.map { grade -> grade.value }.average() }.average()
                    Text(
                        text =
                        "Ø " +
                                (if (avg.isNaN()) "-" else avg.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)) +
                                " | " + grades.grades.map { "${it.givenBy.firstname} ${it.givenBy.lastname}" }.toSet().joinToString(", "),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                IconButton(
                    onClick = onStartCalculator,
                    modifier = Modifier
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "null"
                    )
                }
            }

            AnimatedVisibility(
                visible = grades.grades.none { withIntervals.contains(it.interval) },
                enter = expandVertically(tween(250)),
                exit = shrinkVertically(tween(250))
            ) {
                Text(
                    text = stringResource(id = R.string.grades_noGradesInSubject, grades.subject.name),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }
            grades.grades.groupBy { it.type }.entries.forEachIndexed { i, (_, grades) ->
                if (i != grades.groupBy { it.type }.size - 1) HorizontalDivider()
                grades.forEach { grade ->
                    AnimatedVisibility(
                        visible = withIntervals.contains(grade.interval),
                        enter = expandVertically(tween(250)),
                        exit = shrinkVertically(tween(250))
                    ) {
                        GradeRecord(grade)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GradeSubjectGroupPreview() {
    val subject = Subject(
        id = 1,
        name = "Mathematik",
        short = "MA"
    )
    val teacher = Teacher(
        id = 1,
        firstname = "Max",
        lastname = "Mustermann"
    )
    val classes = ClassesPreview.generateClass(null)
    val vppId = VppIdPreview.generateVppId(classes)
    GradeSubjectGroup(
        withIntervals = setOf(ExampleInterval.interval2(false)),
        grades = SubjectGradeCollection(
            subject = subject,
            grades = listOf(
                Grade(
                    id = 1,
                    subject = subject,
                    type = "KA",
                    modifier = GradeModifier.MINUS,
                    comment = "Geometrie",
                    givenAt = LocalDate.now(),
                    givenBy = teacher,
                    value = 2f,
                    vppId = vppId,
                    interval = ExampleInterval.interval2(false),
                    year = ExampleYear.exampleYear()
                ),
                Grade(
                    id = 2,
                    subject = subject,
                    type = "KA",
                    modifier = GradeModifier.NEUTRAL,
                    comment = "Lineare Funktionen",
                    givenAt = LocalDate.now(),
                    givenBy = teacher,
                    value = 1f,
                    vppId = vppId,
                    interval = ExampleInterval.interval2(false),
                    year = ExampleYear.exampleYear()
                ),
                Grade(
                    id = 2,
                    subject = subject,
                    type = "LK",
                    modifier = GradeModifier.PLUS,
                    comment = "Quadratische Funktionen",
                    givenAt = LocalDate.now(),
                    givenBy = teacher,
                    value = 3f,
                    vppId = vppId,
                    interval = ExampleInterval.interval2(false),
                    year = ExampleYear.exampleYear()
                )
            )
        )
    )
}