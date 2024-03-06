package es.jvbabi.vplanplus.feature.grades.ui.calculator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.grades.domain.model.GradeModifier
import es.jvbabi.vplanplus.feature.grades.ui.calculator.GradeCollection
import es.jvbabi.vplanplus.ui.common.ComposableDialog
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.Grid
import java.math.RoundingMode
import kotlin.random.Random

@Composable
fun CollectionGroup(
    group: String,
    grades: List<Pair<Float, GradeModifier>>,
    avg: Double,
    onAddGrade: (Float) -> Unit,
    onRemoveGrade: (Int) -> Unit
) {
    var dialogOpenForCategory by rememberSaveable { mutableStateOf("") }

    val colorScheme = MaterialTheme.colorScheme

    if (dialogOpenForCategory.isNotEmpty()) {
        ComposableDialog(
            icon = Icons.Default.Grade,
            title = stringResource(id = R.string.gradesCalculator_addGradeTitle),
            okEnabled = true,
            onCancel = { dialogOpenForCategory = "" },
            onOk = null,
            content = {
                Column {
                    Text(
                        text = stringResource(
                            id = R.string.gradesCalculator_addGradeContent,
                            dialogOpenForCategory
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Grid(
                        columns = 3,
                        content = List(6) {
                            @Composable { _, _, _ ->
                                val backgroundColor = Color(
                                    ColorUtils.blendARGB(
                                        Color(0xFF25CC25).toArgb(),
                                        Color.Red.toArgb(),
                                        (it + 1) / 6f
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(backgroundColor)
                                        .clickable { onAddGrade(it + 1f); dialogOpenForCategory = "" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (it + 1).toString(),
                                        modifier = Modifier.padding(8.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    )
                }
            }
        )
    }
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        Text(
            text = "$group $DOT Ø ${if (avg.isNaN()) "-" else avg.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN)}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Text(
                    text = "+",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(4.dp)
                        .width(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorScheme.primary)
                        .padding(4.dp)
                        .clickable { dialogOpenForCategory = group }
                )
            }
            itemsIndexed(grades) { i, (grade, modifier) ->
                Text(
                    text = grade.toInt().toString() + when (modifier) {
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
                            drawRect(
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
                        .clickable {
                            onRemoveGrade(i)
                        }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CollectionGroupPreview() {
    CollectionGroup(
        group = "KA",
        grades = GradeCollection(
            name = "KA",
            grades = List(5) {
                Random.nextFloat()*6 to GradeModifier.entries.toTypedArray().random()
            }
        ).grades,
        avg = 2.0,
        onAddGrade = {},
        onRemoveGrade = {}
    )
}