package es.jvbabi.vplanplus.feature.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.screens.Screen

@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview() {
    SettingsScreen(rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(id = R.string.home_menuSettings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(
                                id = R.string.back
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSetting(
                icon = Icons.Outlined.AccountCircle,
                title = stringResource(id = R.string.settings_vppIdTitle),
                subtitle = stringResource(id = R.string.settings_vppIdSubtitle),
                type = SettingsType.FUNCTION,
                doAction = {
                    navController.navigate(Screen.SettingsVppIdScreen.route)
                }
            )
            SettingsSetting(
                icon = Icons.Outlined.Build,
                title = stringResource(id = R.string.settings_generalSettingsTitle),
                subtitle = stringResource(id = R.string.settings_generalSettingsSubtitle),
                type = SettingsType.FUNCTION,
                doAction = {
                    navController.navigate(Screen.SettingsGeneralSettingsScreen.route)
                }
            )
            SettingsSetting(
                icon = Icons.Outlined.Person,
                title = stringResource(id = R.string.settings_profileTitle),
                subtitle = stringResource(id = R.string.settings_profileSubtitle),
                type = SettingsType.FUNCTION,
                doAction = {
                    navController.navigate(Screen.SettingsProfileScreen.route)
                }
            )
            SettingsSetting(
                painter = painterResource(id = R.drawable.edit_document),
                title = stringResource(id = R.string.settings_homeworkTitle),
                subtitle = stringResource(id = R.string.settings_subjectsSubtitle),
                type = SettingsType.FUNCTION,
                doAction = {
                    navController.navigate(Screen.SettingsHomeworkScreen.route)
                }
            )
            SettingsSetting(
                icon = Icons.Outlined.DataObject,
                title = stringResource(id = R.string.settings_advancedTitle),
                subtitle = stringResource(id = R.string.settings_advancedSubtitle),
                type = SettingsType.FUNCTION,
                doAction = {
                    navController.navigate(Screen.SettingsAdvancedScreen.route)
                }
            )
            SettingsSetting(
                icon = Icons.Outlined.Info,
                title = stringResource(id = R.string.settings_aboutTitle),
                subtitle = stringResource(id = R.string.settings_aboutSubtitle),
                doAction = {
                    navController.navigate(Screen.SettingsAboutScreen.route)
                },
                type = SettingsType.FUNCTION
            )
        }
    }
}