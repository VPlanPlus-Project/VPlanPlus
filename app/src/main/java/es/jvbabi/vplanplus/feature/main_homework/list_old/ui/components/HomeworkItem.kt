package es.jvbabi.vplanplus.feature.main_homework.list_old.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.main_homework.list_old.ui.components.homeworkcard.HomeworkProgressBar
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.getSubjectIcon
import es.jvbabi.vplanplus.util.DateUtils.getRelativeStringResource
import es.jvbabi.vplanplus.util.DateUtils.localizedRelativeDate
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkCardItem(
    homework: Homework,
    currentVppId: VppId?
) {
    var isMarkedToDelete by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) { // Dismissed to the left (mark as done)
                isMarkedToDelete = true
            } else if (it == SwipeToDismissBoxValue.StartToEnd) { // Dismissed to the right (delete)
                TODO()
            }
            true
        }
    )

    AnimatedVisibility(
        visible = !isMarkedToDelete,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = { SwipeBackground(dismissState) }
        ) {
            HomeworkCard(
                subject = homework.defaultLesson?.subject,
                dueTo = homework.until.toLocalDate(),
                taskCount = homework.tasks.size,
                documentCount = homework.documents.size,
                tasksDone = homework.tasks.count { it.isDone },
                creator = when (homework) {
                    is Homework.CloudHomework -> HomeworkCreator.VppIdCreator(homework.createdBy.name, homework.createdBy.id == currentVppId?.id)
                    is Homework.LocalHomework -> HomeworkCreator.DeviceCreator
                },
                createdAt = homework.createdAt,
                isSwiping = dismissState.progress != 1f
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(
    swipeDismissState: SwipeToDismissBoxState
) {
    val color =
        when (swipeDismissState.dismissDirection) {
            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primaryContainer
            SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.errorContainer
            else -> Color.Transparent
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = CenterEnd
    ) {
        Icon(
            imageVector = if (swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                Icons.Default.CheckBox
            } else {
                Icons.Default.DeleteForever
            },
            contentDescription = null,
            tint = if (swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}

sealed interface HomeworkCreator {
    @Composable
    fun getName(): String

    data class VppIdCreator(private val username: String, private val isCurrentUser: Boolean) : HomeworkCreator {
        @Composable override fun getName(): String = if (isCurrentUser) stringResource(id = R.string.homework_you) else username
    }

    data object DeviceCreator : HomeworkCreator {
        @Composable override fun getName(): String = stringResource(id = R.string.homework_thisDevice)
    }
}

@Composable
fun HomeworkCard(
    subject: String?,
    dueTo: LocalDate,
    taskCount: Int,
    documentCount: Int,
    tasksDone: Int,
    creator: HomeworkCreator,
    createdAt: ZonedDateTime,
    isSwiping: Boolean = false
) {
    val context = LocalContext.current
    val isSwipingModifierValue by animateFloatAsState(targetValue = if (isSwiping) 0f else 1f, label = "isSwipingModifierValue")
    RowVerticalCenter(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(((1-isSwipingModifierValue)*8).dp, RoundedCornerShape(((1-isSwipingModifierValue)*16).dp))
            .clip(RoundedCornerShape(((1-isSwipingModifierValue) * 16).dp))
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(16.dp))
            .clickable {}
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Center
        ) icon@{
            Icon(imageVector = subject.getSubjectIcon(), contentDescription = subject, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Column(Modifier.fillMaxWidth()) {
            RowVerticalCenterSpaceBetweenFill title@{
                RowVerticalCenter(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = buildAnnotatedString {
                            if (subject != null) withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                                append(subject)
                                append(" ")
                            }

                            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                                if (subject != null) append("$DOT ")
                                append(stringResource(id = R.string.homework_dueTo, dueTo.getRelativeStringResource(LocalDate.now()).run {
                                    return@run if (this@run == null) dueTo.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                                    else stringResource(id = this@run)
                                }))
                            }
                        },
                    )
                }
                RowVerticalCenter taskAndDocumentCount@{
                    Icon(
                        imageVector = Icons.Default.TaskAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .padding(end = 2.dp)
                            .size(12.dp)
                    )
                    Text(
                        text = taskCount.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (documentCount > 0) {
                        Spacer4Dp()
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .padding(end = 2.dp)
                                .size(12.dp)
                        )
                        Text(
                            text = documentCount.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            Text(
                text = stringResource(id = R.string.homework_authorAndTime, creator.getName(), localizedRelativeDate(context, createdAt.toLocalDate(), true)!!),
                style = MaterialTheme.typography.bodySmall,
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = tasksDone > 0,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                HomeworkProgressBar(tasks = taskCount, tasksDone = tasksDone, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeworkCardPreview() {
    HomeworkCard(
        subject = "MA",
        dueTo = LocalDate.now(),
        taskCount = 3,
        documentCount = 1,
        tasksDone = 2,
        creator = HomeworkCreator.DeviceCreator,
        createdAt = ZonedDateTime.now().minusDays(2)
    )
}

