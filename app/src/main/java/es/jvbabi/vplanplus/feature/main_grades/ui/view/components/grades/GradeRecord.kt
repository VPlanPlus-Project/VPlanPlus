package es.jvbabi.vplanplus.feature.main_grades.ui.view.components.grades

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.main_grades.data.source.example.GradesExample
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.domain.model.GradeModifier
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.util.toBlackAndWhite
import java.time.format.DateTimeFormatter

@Composable
fun GradeRecord(grade: Grade, showSubject: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        val colorScheme = MaterialTheme.colorScheme
        Text(
            text =
            if (grade.actualValue == null) "-"
            else grade.value.toInt().toString() + when (grade.modifier) {
                GradeModifier.MINUS -> "-"
                GradeModifier.PLUS -> "+"
                else -> ""
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .padding(4.dp)
                .width(48.dp)
                .clip(RoundedCornerShape(4.dp))
                .drawWithContent {
                    if (grade.actualValue == null) drawRect(color = colorScheme.primary.toBlackAndWhite(), size = size, topLeft = Offset(0f, 0f))
                    else drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                colorScheme.primary,
                                colorScheme.tertiary
                            )
                        ),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height)
                    )
                    drawContent()
                }
                .padding(4.dp)
        )
        Column(
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val title = buildAnnotatedString {
                    withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                        if (showSubject) {
                            append(grade.subject.short)
                            append(" ")
                            append(DOT)
                            append(" ")
                        }
                        append(grade.comment)
                    }
                    withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                        append(" ")
                        append(DOT)
                        append(" ")
                        append(grade.type)
                    }
                }
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = grade.givenBy.firstname + " " + grade.givenBy.lastname + " $DOT " + grade.givenAt.format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy")
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun GradeRecordPreview() {
    GradeRecord(
        grade = GradesExample(
            VppId(
                id = 1,
                name = "Maria M.",
                schoolId = 1,
                school = null,
                className = "9b",
                classes = null,
                email = "maria.muster@mail.com"
            )
        ).grades().random()
    )
}