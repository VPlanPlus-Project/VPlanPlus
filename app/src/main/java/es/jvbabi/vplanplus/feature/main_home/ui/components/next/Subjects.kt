package es.jvbabi.vplanplus.feature.main_home.ui.components.next

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.Grid
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.SubjectIcon

/**
 * @param subjects Map subject name to Pair(homework done, homework tasks)
 */
@Composable
fun Subjects(
    subjects: Map<String, Pair<Int, Int>>,
) {
    if (subjects.isEmpty()) return
    Spacer8Dp()
    Text(
        text = stringResource(R.string.home_subjects),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
    )
    Spacer4Dp()
    Grid(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp)),
        columns = 2,
        padding = 2.dp,
        content = subjects.map { (subject, homeworkTasks) ->
            @Composable { _, _, index ->
                RowVerticalCenter(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .fillMaxSize()
                        .padding(4.dp),
                ) {
                    SubjectIcon(
                        subject = subject,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = subject,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (homeworkTasks.second > 0) {
                            Text(
                                text = stringResource(R.string.home_subejctsHomework, homeworkTasks.first, homeworkTasks.second),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    )
}