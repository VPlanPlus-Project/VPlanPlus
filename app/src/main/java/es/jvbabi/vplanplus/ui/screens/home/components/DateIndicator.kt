package es.jvbabi.vplanplus.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate

@Composable
fun DateIndicator(
    displayDate: LocalDate,
    alpha: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(10.dp, shape = RoundedCornerShape(20.dp))
            .height(40.dp)
            .width(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = DateUtils.localizedRelativeDate(LocalContext.current, displayDate),
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.alpha(alpha)
        )
    }
}