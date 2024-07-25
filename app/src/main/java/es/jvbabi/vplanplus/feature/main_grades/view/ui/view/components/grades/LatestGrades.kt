package es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components.grades

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Grade
import java.time.format.DateTimeFormatter

const val STEPS = 3

@Composable
fun LatestGrades(grades: List<Grade>) {
    if (grades.isEmpty()) return

    var limit by rememberSaveable { mutableIntStateOf(STEPS) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(32.dp)
                        .clickable { limit = STEPS },
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Column(
                    modifier = Modifier.weight(1f, true)
                ) {
                    Text(
                        text = stringResource(id = R.string.grades_latest),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    Text(
                        text = buildString {
                            append(grades.take(limit).minByOrNull { it.givenAt }!!.givenAt.format(formatter))
                            append(" - ")
                            append(grades.take(limit).maxByOrNull { it.givenAt }!!.givenAt.format(formatter))
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            grades.forEachIndexed { i, grade ->
                AnimatedVisibility(
                    visible = i < limit,
                    enter = expandVertically(tween(200)),
                ) {
                    GradeRecord(grade, true)
                }
            }
            TextButton(
                onClick = { limit += STEPS },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(imageVector = Icons.Default.MoreHoriz, contentDescription = null)
                Text(text = stringResource(id = R.string.grades_showMore))
            }
        }
    }
}