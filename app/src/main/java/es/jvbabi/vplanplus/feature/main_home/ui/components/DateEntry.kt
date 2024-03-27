package es.jvbabi.vplanplus.feature.main_home.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateEntry(
    date: LocalDate,
    homework: Int,
    isActive: Boolean,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .height(100.dp)
            .width(90.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        val background = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 8.dp)
                .size(80.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(8.dp),
                )
                .clip(RoundedCornerShape(8.dp))
                .background(background)
                .clickable { onClick() }
                .padding(6.dp)
                .align(Alignment.TopCenter)
        ) content@{
            Text(
                text = date.format(DateTimeFormatter.ofPattern("d")),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = date.format(DateTimeFormatter.ofPattern("E")),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium
            )
            Row {
                if (homework > 0) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(16.dp)
                    )
                    Text(
                        text = homework.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        val modifier = animateFloatAsState(
            targetValue = if (isActive) 1f else 0f,
            label = "modifier active"
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(modifier.value)
                .height((4 * modifier.value).dp)
                .width(40.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DateEntryPreview() {
    DateEntry(date = LocalDate.now(), homework = 3, isActive = true)
}