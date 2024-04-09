package es.jvbabi.vplanplus.feature.main_home.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_home.ui.Collapsable
import es.jvbabi.vplanplus.util.blendColor
import es.jvbabi.vplanplus.util.toBlackAndWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("NewApi")
@Composable
fun DateCard(
    date: LocalDate,
    isSelected: Boolean,
    modifier: Float,
    expand: Boolean,
    onClick: (date: LocalDate) -> Unit
) {

    val drawable by rememberSaveable {
        mutableStateOf(
            if (isEaster(date)) listOf(R.drawable.easter0, R.drawable.easter1).random()
            else null
        )
    }

    val selectedModifier by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        label = "selectedModifier",
        animationSpec = tween(250)
    )

    val background = blendColor(
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.tertiary,
        selectedModifier
    )

    val foreground = blendColor(
        MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.onTertiary,
        selectedModifier
    )

    val secondaryForeground = blendColor(
        MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.onTertiary.toBlackAndWhite(),
        selectedModifier
    )

    Column {
        val cardShape = RoundedCornerShape(12.dp)
        Box(
            Modifier
                .shadow(8.dp, cardShape)
                .then(
                    if (date.isEqual(LocalDate.now()) && !isSelected) Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        cardShape
                    )
                    else Modifier
                )
                .background(background)
                .size(60.dp)
                .clip(cardShape)
                .clickable { onClick(date) }
        ) {
            if (drawable != null) Image(
                painter = painterResource(drawable!!),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height((50 + 30 * modifier).dp)
                    .width(60.dp)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            Brush.verticalGradient(
                                0f to background,
                                0.5f to background,
                                1f to background.copy(alpha = 0f)
                            ),
                            size = size
                        )
                    }
            )
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Collapsable(expand = expand) {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())).take(2),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Light,
                            color = secondaryForeground
                        ),
                    )
                }
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("d", Locale.getDefault())),
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    fontSize = (26 + 4 * modifier).sp,
                    lineHeight = (26 + 4 * modifier).sp,
                    color = foreground
                )
            }
        }

        val texts = mutableListOf<String>()
        if (date.dayOfMonth == 1) texts.add(date.format(DateTimeFormatter.ofPattern("MMMM")))
        if (date.dayOfWeek.value == 1) texts.add("KW ${date.format(DateTimeFormatter.ofPattern("w"))}")

        Collapsable(expand = expand) {
            if (texts.isNotEmpty()) Text(
                text = texts.joinToString("\n"),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Light,
                    color = Color.Gray
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
            else Box(Modifier.height((MaterialTheme.typography.labelSmall.lineHeight.value * 2).dp + 4.dp))
        }
        Collapsable(expand = !expand) { Box(Modifier.height(8.dp)) }
    }
}

fun isEaster(date: LocalDate): Boolean {
    return date.isAfter(LocalDate.of(2024, 3, 27)) && date.isBefore(LocalDate.of(2024, 4, 8))
}