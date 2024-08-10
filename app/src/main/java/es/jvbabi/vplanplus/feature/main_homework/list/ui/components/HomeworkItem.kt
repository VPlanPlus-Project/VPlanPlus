package es.jvbabi.vplanplus.feature.main_homework.list.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.skydoves.balloon.compose.Balloon
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkCore
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PersonalizedHomework
import es.jvbabi.vplanplus.ui.common.DefaultBalloonDescription
import es.jvbabi.vplanplus.ui.common.DefaultBalloonTitle
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.common.getSubjectIcon
import es.jvbabi.vplanplus.ui.common.rememberDefaultBalloon
import es.jvbabi.vplanplus.util.DateUtils.localizedRelativeDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkCardItem(
    personalizedHomework: PersonalizedHomework,
    isVisible: Boolean,
    onClick: () -> Unit,
    onCheckSwiped: () -> Unit,
    onVisibilityOrDeleteSwiped: () -> Unit,
    resetKey1: Any? = null,
    resetKey2: Any? = null,
    showDemo: Boolean = false,
    onDemoEnd: () -> Unit = {},
    allowProgressBar: Boolean = true
) {
    val scope = rememberCoroutineScope()

    var isMarkedToDelete by remember { mutableStateOf(false) }
    var isDeleteDialogOpen by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) { // Dismissed to the left (mark as done)
                onCheckSwiped()
            } else if (it == SwipeToDismissBoxValue.StartToEnd) { // Dismissed to the right (delete)
                val homework = personalizedHomework.homework
                if ((homework is HomeworkCore.CloudHomework && homework.createdBy.id == personalizedHomework.profile.vppId?.id) || homework is HomeworkCore.LocalHomework) isDeleteDialogOpen = true
                else onVisibilityOrDeleteSwiped()
            }
            true
        }
    )

    LaunchedEffect(key1 = resetKey1, key2 = resetKey2, key3 = personalizedHomework) { dismissState.reset() }

    if (isDeleteDialogOpen) {
        YesNoDialog(
            icon = Icons.Default.DeleteForever,
            title = stringResource(id = R.string.homework_deleteHomeworkTitle),
            message =
            when (personalizedHomework) {
                is PersonalizedHomework.CloudHomework -> if (personalizedHomework.homework.isPublic) stringResource(id = R.string.homework_deleteHomeworkTextPublic) else stringResource(id = R.string.homework_deleteHomeworkTextPrivate)
                is PersonalizedHomework.LocalHomework -> stringResource(id = R.string.homework_deleteHomeworkTextLocal)
            },
            onYes = {
                isMarkedToDelete = true
                isDeleteDialogOpen = false
                scope.launch {
                    delay(AnimationConstants.DefaultDurationMillis.toLong())
                    onVisibilityOrDeleteSwiped()
                }
            },
            onNo = { isDeleteDialogOpen = false; scope.launch { dismissState.reset() } },
        )
    }
    var demoDisplayDirection by remember<MutableState<SwipeToDismissBoxValue?>> { mutableStateOf(null) }
    var demoDisplayOffset by remember { mutableFloatStateOf(0f) }
    var isDemoRunning by remember { mutableStateOf(showDemo) }

    if (showDemo) LaunchedEffect(key1 = Unit) {
        isDemoRunning = true
        while (isDemoRunning) {
            delay(1000)
            demoDisplayDirection = SwipeToDismissBoxValue.EndToStart
            demoDisplayOffset = -64f
            delay(1000)
            demoDisplayOffset = 64f
            delay(500)
            demoDisplayDirection = SwipeToDismissBoxValue.StartToEnd
            delay(500)
            demoDisplayOffset = 0f
            delay(500)
            demoDisplayDirection = SwipeToDismissBoxValue.Settled
        }
    }

    AnimatedVisibility(
        visible = !isMarkedToDelete && isVisible,
        enter = expandVertically(),
        exit = shrinkVertically(tween()),
    ) {
        Balloon(
            builder = rememberDefaultBalloon(),
            balloonContent = {
                Column {
                    DefaultBalloonTitle(text = stringResource(id = R.string.homework_swipeDemoTitle))
                    DefaultBalloonDescription(text = stringResource(id = R.string.homework_swipeDemoDescription))
                }
            },
        ) { balloon ->
            LaunchedEffect(key1 = showDemo) {
                if (!showDemo) return@LaunchedEffect
                delay(500)
                balloon.showAlignBottom()
                balloon.setOnBalloonDismissListener {
                    onDemoEnd()
                    demoDisplayDirection = null
                    demoDisplayOffset = 0f
                    isDemoRunning = false
                }
            }
            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromEndToStart = !isDemoRunning,
                enableDismissFromStartToEnd = !isDemoRunning,
                backgroundContent = { SwipeBackground(demoDisplayDirection ?: dismissState.dismissDirection, personalizedHomework.homework is HomeworkCore.LocalHomework || (personalizedHomework is PersonalizedHomework.CloudHomework && personalizedHomework.homework.createdBy.id == personalizedHomework.profile.vppId?.id), personalizedHomework is PersonalizedHomework.CloudHomework && personalizedHomework.isHidden) }
            ) {
                val demoOffset = animateFloatAsState(
                    targetValue = demoDisplayOffset,
                    label = "demoOffset",
                    animationSpec = tween(1000)
                )
                val homework = personalizedHomework.homework
                HomeworkCard(
                    modifier = Modifier.offset { IntOffset(demoOffset.value.dp.roundToPx(), 0) },
                    subject = homework.defaultLesson?.subject,
                    tasks = homework.tasks.map { it.content },
                    documentCount = homework.documents.size,
                    tasksDone = personalizedHomework.tasks.count { it.isDone },
                    creator = when (homework) {
                        is HomeworkCore.CloudHomework -> HomeworkCreator.VppIdCreator(homework.createdBy.name, homework.createdBy.id == personalizedHomework.profile.vppId?.id)
                        else -> HomeworkCreator.DeviceCreator
                    },
                    createdAt = homework.createdAt,
                    swipingProgress = dismissState.progress,
                    isHidden = personalizedHomework is PersonalizedHomework.CloudHomework && personalizedHomework.isHidden,
                    isPublic = homework is HomeworkCore.CloudHomework && homework.isPublic,
                    onClick = onClick,
                    allowProgressBar = allowProgressBar
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(
    direction: SwipeToDismissBoxValue,
    canDelete: Boolean,
    isHidden: Boolean = false,
) {
    val color =
        when (direction) {
            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primaryContainer
            SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.errorContainer
            else -> Color.Transparent
        }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = if (direction == SwipeToDismissBoxValue.EndToStart) CenterEnd else CenterStart
    ) {
        Icon(
            imageVector = if (direction == SwipeToDismissBoxValue.EndToStart) {
                Icons.Default.CheckBox
            } else if (canDelete) {
                Icons.Default.DeleteForever
            } else if (isHidden) {
                Icons.Default.VisibilityOff
            } else {
                Icons.Default.Visibility
            },
            contentDescription = null,
            tint = if (direction == SwipeToDismissBoxValue.EndToStart) {
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
        @Composable
        override fun getName(): String = if (isCurrentUser) stringResource(id = R.string.homework_you) else username
    }

    data object DeviceCreator : HomeworkCreator {
        @Composable
        override fun getName(): String = stringResource(id = R.string.homework_thisDevice)
    }
}

@Composable
private fun HomeworkCard(
    modifier: Modifier = Modifier,
    subject: String?,
    tasks: List<String>,
    documentCount: Int,
    tasksDone: Int,
    creator: HomeworkCreator,
    createdAt: ZonedDateTime,
    isHidden: Boolean,
    isPublic: Boolean,
    swipingProgress: Float = 1f,
    onClick: () -> Unit = {},
    allowProgressBar: Boolean = true
) {
    val context = LocalContext.current
    val isSwipingModifierValue by animateFloatAsState(targetValue = if (swipingProgress == 1f) 0f else 1f, label = "isSwipingModifierValue")
    val rounding = ((abs(swipingProgress.run { if (this == 1f) return@run 0f else return@run this }).coerceAtMost(0.08f) / 0.08f) * 16).dp
    RowVerticalCenter(
        modifier = modifier
            .zIndex(if (swipingProgress != 1f) 0f else 1f)
            .fillMaxWidth()
            .shadow((isSwipingModifierValue * 8).dp, RoundedCornerShape(rounding))
            .clip(RoundedCornerShape(rounding))
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(if (rounding == 0.dp) 16.dp else rounding))
            .clickable { onClick() }
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
                            withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
                                append(subject ?: stringResource(id = R.string.homework_noSubject))
                                append(" ")
                            }
                        },
                    )
                    if (isHidden) Icon(imageVector = Icons.Default.VisibilityOff, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
                    if (isPublic) Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.outline)
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
                        text = tasks.size.toString(),
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
            Text(
                text = tasks.joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = allowProgressBar,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                HomeworkProgressBar(tasks = tasks.size, tasksDone = tasksDone, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeworkCardPreview() {
    HomeworkCard(
        subject = "MA",
        tasks = listOf("Pythagoras Theorem", "Trigonometry"),
        documentCount = 1,
        tasksDone = 1,
        creator = HomeworkCreator.DeviceCreator,
        createdAt = ZonedDateTime.now().minusDays(2),
        isHidden = true,
        isPublic = false
    )
}

