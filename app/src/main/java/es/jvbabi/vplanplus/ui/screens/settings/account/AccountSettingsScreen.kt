package es.jvbabi.vplanplus.ui.screens.settings.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import java.net.URLEncoder
import es.jvbabi.vplanplus.ui.preview.Classes as PreviewClasses
import es.jvbabi.vplanplus.ui.preview.School as PreviewSchool

@Composable
fun AccountSettingsScreen(
    navHostController: NavHostController,
    viewModel: AccountSettingsViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    AccountSettingsScreenContent(
        onBack = { navHostController.popBackStack() },
        onLogin = {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://id.vpp.jvbabi.es/link/?name=VPlanPlus%20on%20Android%20" + URLEncoder.encode(Build.VERSION.RELEASE, "UTF-8"))
            )
            ContextCompat.startActivity(context, browserIntent, null)
            (context as Activity).finish()
        },
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountSettingsScreenContent(
    onBack: () -> Unit,
    onLogin: () -> Unit = {},
    state: AccountSettingsState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.vppidSettings_title)) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        BackIcon()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            if (state.accounts == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.accounts.entries.toList()) { (account, enabled) ->
                        SettingsSetting(
                            icon = if (enabled == true) Icons.Outlined.Check else Icons.Default.ErrorOutline,
                            iconTint = if (enabled == true) null else MaterialTheme.colorScheme.error,
                            title = account.name,
                            isLoading = enabled == null,
                            subtitle = account.schoolId.toString() + " / " + account.className,
                            type = SettingsType.FUNCTION,
                            doAction = {},
                        )
                    }
                    if (state.accounts.isNotEmpty()) item { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp)) }
                    item {
                        SettingsSetting(
                            icon = Icons.Default.Add,
                            title = stringResource(id = R.string.vppidSettings_add),
                            type = SettingsType.FUNCTION,
                            doAction = { onLogin() },
                        )
                    }
                    item { HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp)) }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier
                                    .size(16.dp)
                            )
                            Text(text = stringResource(id = R.string.vppidSettings_info), modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun AccountSettingsPreviewNoAccounts() {
    AccountSettingsScreenContent(
        onBack = {},
        state = AccountSettingsState(
            accounts = emptyMap(),
        )
    )
}

@Preview
@Composable
private fun AccountSettingsPreview() {
    val school = PreviewSchool.generateRandomSchools(1).first()
    val classes = PreviewClasses.generateClass(school)
    AccountSettingsScreenContent(
        onBack = {},
        state = AccountSettingsState(
            accounts = mapOf(
                VppId(
                    id = 654,
                    name = "Max Mustermann",
                    schoolId = school.schoolId,
                    school = school,
                    className = classes.name,
                    classes = classes
                ) to false
            )
        )
    )
}