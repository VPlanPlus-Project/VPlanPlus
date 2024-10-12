package es.jvbabi.vplanplus.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.util.toBlackAndWhite

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    text: String,
    buttonText1: String? = null,
    buttonAction1: () -> Unit = {},
    buttonText2: String? = null,
    buttonAction2: () -> Unit = {},
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Column(
        modifier = modifier
            .shadow(5.dp, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(start = 4.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = textColor
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = textColor
                )
                Text(text = text, color = textColor)
            }
        }

        if (buttonText1 == null && buttonText2 == null) Spacer(modifier = Modifier.size(16.dp))

        if (buttonText1 != null) Row(
            modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.End),
        ) {
            TextButton(
                onClick = { buttonAction1() },
                colors = ButtonColors(
                    contentColor = textColor,
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = textColor.toBlackAndWhite()
                )
            ) {
                Text(text = buttonText1)
            }
            if (buttonText2 != null) {
                Spacer(modifier = Modifier.size(8.dp))
                TextButton(
                    onClick = { buttonAction2() },
                    colors = ButtonColors(
                        contentColor = textColor,
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = textColor.toBlackAndWhite()
                    )
                ) {
                    Text(text = buttonText2)
                }
            }
        }
    }
}

@Composable
fun CollapsableInfoCard(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    title: String,
    text: String,
    buttonText1: String? = null,
    buttonAction1: () -> Unit = {},
    buttonText2: String? = null,
    buttonAction2: () -> Unit = {},
    isExpanded: Boolean,
    onChangeState: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .shadow(5.dp, shape = RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .drawWithContent {
                drawRect(
                    color = colorScheme.primary,
                    topLeft = Offset(0f, 0f),
                    size = Size(32f, size.height)
                )
                drawContent()
            }
            .padding(start = 4.dp)
            .clickable {
                onChangeState(!isExpanded)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = imageVector,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    val rotation = animateFloatAsState(
                        targetValue = if (isExpanded) -180f else 0f,
                        label = "Expand Card"
                    )
                    IconButton(onClick = { onChangeState(!isExpanded) }) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotation.value)
                        )
                    }
                }
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(tween(250)),
                    exit = shrinkVertically(tween(250)),
                ) {
                    Text(
                        text = text,
                        modifier = Modifier.padding(start = 38.dp)
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(tween(250)),
            exit = shrinkVertically(tween(250)),
            modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.End)
        ) {
            if (buttonText1 != null) Row {
                TextButton(onClick = { buttonAction1() }) {
                    Text(text = buttonText1)
                }
                if (buttonText2 != null) {
                    Spacer(modifier = Modifier.size(8.dp))
                    TextButton(onClick = { buttonAction2() }) {
                        Text(text = buttonText2)
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoCardPreview() {
    InfoCard(
        imageVector = Icons.Default.Info,
        title = "Title",
        text = "Text\nA very big one",
    )
}

@Preview(showBackground = true)
@Composable
private fun CollapsableInfoCardPreview() {
    CollapsableInfoCard(
        imageVector = Icons.Default.Info,
        title = stringResource(id = R.string.home_activeDaySchoolInformation),
        text = "There is an info",
        isExpanded = false,
        onChangeState = {},
        buttonText1 = "Ok"
    )
}