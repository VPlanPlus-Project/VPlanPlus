package es.jvbabi.vplanplus.feature.settings.vpp_id.ui

import android.content.Context
import android.os.Build
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.settings.advanced.ui.components.VppIdServer
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.screens.Screen
import io.ktor.http.Parameters
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import es.jvbabi.vplanplus.ui.preview.GroupPreview as PreviewClasses
import es.jvbabi.vplanplus.ui.preview.SchoolPreview as PreviewSchool

fun onLogin(context: Context, server: VppIdServer) {
    val host = URLBuilder(server.uiHost).host
    val clientId = BuildConfig.VPP_CLIENT_ID
    val clientSecret = BuildConfig.VPP_CLIENT_SECRET
    val redirectUri = BuildConfig.VPP_REDIRECT_URI
    val url = Url(
        URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = host,
            port = 443,
            pathSegments = listOf("id", "login", "link"),
            parameters = Parameters.build {
                append("version", BuildConfig.VERSION_CODE.toString())
                append("name", Build.BRAND + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")")
                append("client_id", clientId)
                append("client_secret", clientSecret)
                append("redirect_uri", redirectUri)
            }
        )
    )
    val intent = CustomTabsIntent.Builder().build()
    intent.launchUrl(context, url.toString().toUri())
}


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
            onLogin(context, state.server)
        },
        onOpenVppIdManagement = { vppIdId ->
            navHostController.navigate(Screen.SettingsVppIdManageScreen.route + "/$vppIdId")
        },
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountSettingsScreenContent(
    onBack: () -> Unit,
    onLogin: () -> Unit = {},
    onOpenVppIdManagement: (Int) -> Unit = {},
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
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.accounts) { (account, enabled, profiles) ->

                    val subtitle = listOfNotNull(
                        if (account.school != null) account.school.name.take(24) + (if (account.school.name.length > 24) "..." else "") else account.schoolId.toString(),
                        if (profiles.isNotEmpty()) account.groupName else null,
                        if (profiles.isEmpty()) stringResource(id = R.string.vppIdSettings_noProfilesConnected) else pluralStringResource(
                            id = R.plurals.vppIdSettings_withNProfilesConnected,
                            count = profiles.size,
                            profiles.size
                        )
                    )

                    SettingsSetting(
                        icon = if (enabled == true) Icons.Outlined.Check else Icons.Default.ErrorOutline,
                        iconTint = if (enabled == true) null else MaterialTheme.colorScheme.error,
                        title = account.name,
                        isLoading = enabled == null,
                        subtitle =
                            if (enabled == false) stringResource(id = R.string.vppIdSettings_sessionExpired)
                            else subtitle.joinToString(" $DOT "),
                        type = SettingsType.FUNCTION,
                        doAction = { onOpenVppIdManagement(account.id) },
                    )
                }
                if (state.accounts.isNotEmpty()) item {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        )
                    )
                }
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
                        Text(
                            text = stringResource(id = R.string.vppidSettings_info),
                            modifier = Modifier.padding(start = 8.dp)
                        )
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
            accounts = emptyList(),
        )
    )
}

@Preview
@Composable
private fun AccountSettingsPreview() {
    val school = PreviewSchool.generateRandomSchools(1).first()
    val classes = PreviewClasses.generateGroup(school)
    AccountSettingsScreenContent(
        onBack = {},
        state = AccountSettingsState(
            accounts = listOf(
                VppIdSettingsRecord(
                    VppId(
                        id = 654,
                        name = "Max Mustermann",
                        schoolId = school.id,
                        school = school,
                        groupName = classes.name,
                        group = classes,
                        email = "max.mustermann@email.com"
                    ),
                    false,
                    emptyList()
                )
            )
        )
    )
}