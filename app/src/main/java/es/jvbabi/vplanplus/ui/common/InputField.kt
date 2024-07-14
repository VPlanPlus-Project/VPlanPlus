package es.jvbabi.vplanplus.ui.common

import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color

@Composable
fun BasicInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    focusRequester: FocusRequester? = null
) {
    TextField(
        modifier = modifier.then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        )
    )
}