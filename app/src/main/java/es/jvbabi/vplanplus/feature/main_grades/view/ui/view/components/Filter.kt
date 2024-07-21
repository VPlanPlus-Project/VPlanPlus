package es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Interval
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Subject
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.GradeEvent
import es.jvbabi.vplanplus.ui.common.SubjectIcon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Filter(
    visible: Boolean,
    subjects: Map<Subject, Boolean>,
    intervals: Map<Interval, Boolean>,
    onEvent: (GradeEvent) -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(tween(200), Alignment.CenterVertically),
        exit = shrinkVertically(tween(200), Alignment.CenterVertically)
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.grades_filterTitle),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                subjects.forEach { (subject, isSelected) ->
                    FilterChip(
                        selected = isSelected,
                        onClick = { onEvent(GradeEvent.ToggleSubject(subject)) },
                        label = { Text(text = subject.short) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        leadingIcon = {
                            SubjectIcon(
                                subject = subject.name,
                                modifier = Modifier,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.grades_filterIntervalsTitle),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
            FlowRow(Modifier.fillMaxWidth()) {
                intervals.forEach { (interval, isSelected) ->
                    FilterChip(
                        selected = isSelected,
                        onClick = { onEvent(GradeEvent.ToggleInterval(interval)) },
                        label = { Text(text = interval.name) },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}