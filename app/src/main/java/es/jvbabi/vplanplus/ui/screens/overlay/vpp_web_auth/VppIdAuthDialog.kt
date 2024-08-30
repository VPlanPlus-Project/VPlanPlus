package es.jvbabi.vplanplus.ui.screens.overlay.vpp_web_auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.vpp_id.WebAuthTask
import es.jvbabi.vplanplus.ui.common.ComposableDialog
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.VerticalExpandVisibility
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import kotlinx.coroutines.delay
import java.time.ZonedDateTime

@Composable
fun VppIdAuthWrapper(
    task: WebAuthTask?,
    viewModel: VppIdAuthViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {
    LaunchedEffect(key1 = task) {
        if (task != null) viewModel.init(task)
    }

    val state = viewModel.state
    if (state != null) VppIdAuthDialog(
        state = state,
        onSelectEmoji = viewModel::selectEmoji,
        onCancel = onFinished
    )

    LaunchedEffect(key1 = state?.result) {
        if (state?.result == null) return@LaunchedEffect
        delay(1000)
        onFinished()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VppIdAuthDialog(
    state: VppIdAuthState,
    onSelectEmoji: (String) -> Unit,
    onCancel: () -> Unit,
) {
    ComposableDialog(
        icon = Icons.AutoMirrored.Default.Login,
        title = stringResource(id = R.string.webAuth_title),
        cancelString = stringResource(id = android.R.string.cancel),
        onCancel = onCancel,
        content = {
            Column {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.task.emojis.forEach forEachEmoji@{ emoji ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable(!state.isLoading) {
                                    onSelectEmoji(emoji)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.loadingEmoji == emoji && state.result == null) CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                            )
                            val alpha by animateFloatAsState(
                                targetValue = if (state.isLoading && state.loadingEmoji != emoji) 0.5f else 1f,
                                label = "Emoji Alpha"
                            )

                            val overlayAlpha by animateFloatAsState(targetValue = if (state.result != null && state.loadingEmoji == emoji) 1f else 0f, label = "Overlay Alpha")
                            Text(
                                text = emoji,
                                fontSize = 24.sp,
                                modifier = Modifier.alpha(alpha * (1 - overlayAlpha))
                            )

                            Icon(
                                imageVector = if (state.result == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(overlayAlpha),
                                tint = if (state.result == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }

                VerticalExpandVisibility(visible = state.error) {
                    Column {
                        Spacer8Dp()
                        InfoCard(
                            imageVector = Icons.Default.Error,
                            title = stringResource(id = R.string.something_went_wrong),
                            text = stringResource(id = R.string.webAuth_error)
                        )
                    }
                }
            }
        }
    )
}

@Composable
@Preview
private fun VppIdAuthDialogPreview() {
    VppIdAuthDialog(
        state = VppIdAuthState(
            task = WebAuthTask(
                taskId = 0,
                emojis = listOf(
                    "🛷",
                    "🫗",
                    "🐄",
                    "🐛",
                    "🐰",
                    "🎵",
                    "🎗️",
                    "🎃",
                    "😃",
                    "🪳"
                ),
                validUntil = ZonedDateTime.now().plusMinutes(10),
                vppId = VppIdPreview.generateActiveVppId(null)
            ),
            loadingEmoji = "🐰",
            error = true,
            result = false
        ),
        onSelectEmoji = {},
        onCancel = {}
    )
}