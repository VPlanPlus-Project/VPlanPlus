package es.jvbabi.vplanplus.feature.homework.view.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import java.time.format.DateTimeFormatter

@Composable
fun HomeworkCard(
    currentUser: VppId?,
    homework: Homework,
    allDone: (Boolean) -> Unit,
    singleDone: (HomeworkTask, Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = homework.tasks.all { it.done }, onCheckedChange = { allDone(it) })
                Column {
                    Text(
                        text = stringResource(
                            id = R.string.homework_homeworkHead,
                            homework.defaultLesson.subject,
                            homework.until.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy")
                            )
                        ),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = createSubtext(homework, currentUser), style = MaterialTheme.typography.labelMedium)
                }
            }

            HorizontalDivider()
            homework.tasks.sortedBy { it.done.toString() + it.content }.forEach { task ->
                Row(
                    modifier = Modifier.padding(start = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = task.done, onCheckedChange = { singleDone(task, it) })
                    Text(
                        text = task.content,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
            }
            Row(
                modifier = Modifier.padding(start = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = "Neu",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun createSubtext(homework: Homework, currentUser: VppId?): String {
    val builder = StringBuilder()
    if (homework.createdBy != null) {
        if (currentUser == homework.createdBy) builder.append(
            stringResource(
                id = R.string.homework_homeworkSubtitleCreatedByYou,
                homework.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
            )
        ) else builder.append(
            stringResource(
                id = R.string.homework_homeworkSubtitleCreatedBy,
                homework.createdBy.name,
                homework.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
            )
        )
    } else {
        builder.append(
            stringResource(
                id = R.string.homework_homeworkSubtitleLocally,
                homework.createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")
                )
            )
        )
    }
    return builder.toString()
}