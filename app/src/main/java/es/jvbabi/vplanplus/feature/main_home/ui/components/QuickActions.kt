package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NextWeek
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.util.DateUtils.getRelativeStringResource
import es.jvbabi.vplanplus.util.VersionTools
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun QuickActions(
    modifier: Modifier = Modifier,
    nextSchoolDayWithData: LocalDate?,
    selectedDate: LocalDate,
    onNewHomeworkClicked: () -> Unit = {},
    onFindAvailableRoomClicked: () -> Unit = {},
    onPrepareNextDayClicked: () -> Unit = {},
    onSendFeedback: () -> Unit = {},
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) title@{
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 16.dp, end = 4.dp)
                    .size(20.dp)
            )
            Text(
                text = stringResource(id = R.string.home_quickActionsTitle),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
        }
        LazyRow(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
        ) {
            item { Spacer(Modifier.width(8.dp)) }
            item {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    text = stringResource(id = R.string.home_quickActionsNewHomework),
                    onClick = onNewHomeworkClicked,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            item {
                AnimatedVisibility(
                    visible = nextSchoolDayWithData != null && !selectedDate.isEqual(nextSchoolDayWithData),
                    enter = expandHorizontally(tween(200)),
                    exit = shrinkHorizontally(tween(200))
                ) {
                    val resource = nextSchoolDayWithData?.getRelativeStringResource(LocalDate.now())
                    val dayText =
                        if (nextSchoolDayWithData == null) stringResource(id = R.string.tomorrow)
                        else if (resource != null) stringResource(id = resource)
                        else nextSchoolDayWithData.format(DateTimeFormatter.ofPattern("EEEE"))
                    QuickActionButton(
                        icon = Icons.AutoMirrored.Outlined.NextWeek,
                        text = stringResource(id = R.string.home_quickActionsNextDayPreparation, dayText),
                        onClick = onPrepareNextDayClicked,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
            item {
                QuickActionButton(
                    icon = Icons.Outlined.MeetingRoom,
                    text = stringResource(id = R.string.home_quickActionsFindAvailableRoom),
                    onClick = onFindAvailableRoomClicked,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            if (VersionTools.isDevelopmentVersion()) item {
                QuickActionButton(
                    icon = Icons.Outlined.Feedback,
                    text = stringResource(id = R.string.home_sendFeedback),
                    onClick = onSendFeedback,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
            item { Spacer(Modifier.width(8.dp)) }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun QuickActionsPreview() {
    QuickActions(nextSchoolDayWithData = LocalDate.now().plusDays(1L), selectedDate = LocalDate.now())
}

@Composable
private fun QuickActionButton(modifier: Modifier = Modifier, icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(end = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
@Preview
private fun QuickActionButtonPreview() {
    QuickActionButton(
        icon = Icons.Default.Add,
        text = "New task",
        onClick = {}
    )
}