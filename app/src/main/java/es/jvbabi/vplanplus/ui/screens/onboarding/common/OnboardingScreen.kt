package es.jvbabi.vplanplus.ui.screens.onboarding.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    // content composables
    title: String,
    text: @Composable () -> Unit,
    buttonText: String,
    isLoading: Boolean,
    enabled: Boolean,
    onButtonClick: () -> Unit,
    content: @Composable () -> Unit,
    footer: @Composable () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, false)
                    .padding(PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp))
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge
                )
                text()
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    content()
                }
            }
            Column(
                modifier = Modifier
                    .padding(PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp))
                    .fillMaxWidth()
            ) {
                footer()
                Button(onClick = { onButtonClick() }, modifier = Modifier
                    .fillMaxWidth(),
                    enabled = enabled && !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .width(24.dp)
                            .height(24.dp)
                            .padding(6.dp)
                    )
                    else Text(text = buttonText)
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun OnboardingScreenPreview() {
    OnboardingScreen(
        title = "Title",
        text = { Text(text = "Text") },
        buttonText = "Button",
        isLoading = true,
        enabled = true,
        onButtonClick = {},
        content = {
            val lines = 200
            Column {
                repeat(lines) {
                    Text(text = "Very long content (line ${it + 1}/$lines)")
                }
            }
        }
    )
}