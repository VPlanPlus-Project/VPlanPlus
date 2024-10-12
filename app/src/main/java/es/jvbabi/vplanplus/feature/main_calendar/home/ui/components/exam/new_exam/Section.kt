package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import es.jvbabi.vplanplus.ui.common.Spacer8Dp

@Composable
fun Section(
    title: @Composable () -> Unit,
    isVisible: Boolean = true,
    isContentExpanded: Boolean = true,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        isVisible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Column {
            title()
            AnimatedVisibility(
                isContentExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Spacer8Dp()
                content()
            }
        }
    }
}