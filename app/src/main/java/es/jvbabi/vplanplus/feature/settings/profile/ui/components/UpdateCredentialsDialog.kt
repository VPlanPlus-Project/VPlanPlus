package es.jvbabi.vplanplus.feature.settings.profile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.ComposableDialog
import es.jvbabi.vplanplus.ui.preview.School
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UpdateCredentialsDialog(
    schoolName: String,
    username: String,
    password: String,
    isLoading: Boolean,
    isValid: Boolean?,
    hasError: Boolean,
    onResetValidity: () -> Unit,
    onCheckValidity: (username: String, password: String) -> Unit,
    onConfirm: (username: String, password: String) -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var inputUsername by rememberSaveable { mutableStateOf(username) }
    var inputPassword by rememberSaveable { mutableStateOf(password) }
    var checkValidityJob: Job? by rememberSaveable { mutableStateOf(null) }
    val onInputChanges by remember {
        mutableStateOf({
            onResetValidity()
            checkValidityJob?.cancel()
            checkValidityJob = scope.launch {
                delay(500)
                onCheckValidity(inputUsername, inputPassword)
            }
        })
    }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    ComposableDialog(
        icon = Icons.Default.Key,
        title = stringResource(id = R.string.settings_profileUpdateSchoolCredentialsTitle),
        okEnabled = !isLoading && isValid == true,
        onOk = { onConfirm(inputUsername, inputPassword) },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = schoolName,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                TextField(
                    value = inputUsername,
                    onValueChange = { inputUsername = it; onInputChanges() },
                    label = { Text(text = stringResource(id = R.string.username)) },
                    trailingIcon = {
                        IconButton(onClick = {
                            inputUsername = if (inputUsername == "schueler") "lehrer"
                            else "schueler"
                            onResetValidity()
                        }) {
                            Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = null)
                        }
                    }
                )
                TextField(
                    value = inputPassword,
                    onValueChange = { inputPassword = it; onInputChanges() },
                    label = { Text(text = stringResource(id = R.string.password)) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, null)
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var content: (@Composable () -> Unit) by remember {
                        mutableStateOf({Spacer(modifier = Modifier.size(24.dp))})
                    }
                    LaunchedEffect(key1 = isLoading, key2 = hasError, key3 = isValid) {
                        if (isLoading || hasError || isValid != null) content = {
                            if (isLoading) {
                                CircularProgressIndicator(Modifier.size(24.dp))
                                Text(text = stringResource(id = R.string.settings_profileUpdateSchoolCredentialsCheckingCredentials))
                            } else if (hasError) {
                                Icon(Icons.Default.Error, contentDescription = null, modifier = Modifier.size(24.dp))
                                Text(text = stringResource(id = R.string.settings_profileUpdateSchoolCredentialsCredentialsError))
                            } else if (isValid == true) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(24.dp))
                                Text(text = stringResource(id = R.string.settings_profileUpdateSchoolCredentialsCredentialsValid))
                            } else if (isValid == false) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(24.dp))
                                Text(text = stringResource(id = R.string.settings_profileUpdateSchoolCredentialsCredentialsInvalid))
                            }
                        }
                    }
                    content()
                }
            }
        },
        onCancel = onCancel
    )
}

@Preview
@Composable
private fun UpdateCredentialsDialogPreview() {
    val school = School.generateRandomSchools(1).first()
    UpdateCredentialsDialog(
        schoolName = school.name,
        username = school.username,
        password = school.password,
        isLoading = false,
        isValid = false,
        hasError = true,
        onResetValidity = {},
        onCheckValidity = { _, _ -> },
        onConfirm = { _, _ -> },
        onCancel = {}
    )
}