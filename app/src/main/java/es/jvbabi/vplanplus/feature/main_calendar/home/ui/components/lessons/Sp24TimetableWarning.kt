package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.lessons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.Spacer8Dp

@Composable
fun Sp24TimetableWarning(
    onDismiss: () -> Unit,
    onTimetableInfoBannerClicked: () -> Unit,
    hasTimetableLessons: Boolean = false,
    canShowTimetableInfoBanner: Boolean = false,
) {
    if (hasTimetableLessons) AnimatedVisibility(
        visible = canShowTimetableInfoBanner,
        enter = fadeIn(tween(0)),
        exit = shrinkVertically()
    ) {
        Column {
            Spacer8Dp()
            Sp24TimetableWarningBanner(
                onDismiss = onDismiss,
                onTimetableInfoBannerClicked = onTimetableInfoBannerClicked
            )
            Spacer8Dp()
        }
    }
}

@Composable
private fun Sp24TimetableWarningBanner(
    onDismiss: () -> Unit,
    onTimetableInfoBannerClicked: () -> Unit,
) {
    InfoCard(
        imageVector = Icons.Default.WarningAmber,
        title = stringResource(id = R.string.calendar_timetableBannerTitle),
        text = stringResource(id = R.string.calendar_timetableBannerText),
        buttonText2 = stringResource(id = android.R.string.ok),
        buttonAction2 = onDismiss,
        buttonText1 = stringResource(id = R.string.learn_more),
        buttonAction1 = onTimetableInfoBannerClicked,
        backgroundColor = MaterialTheme.colorScheme.errorContainer,
        textColor = MaterialTheme.colorScheme.onErrorContainer
    )
}

@Composable
@Preview
private fun Sp24TimetableWarningPreview() {
    Sp24TimetableWarning(
        onDismiss = {},
        onTimetableInfoBannerClicked = {}
    )
}