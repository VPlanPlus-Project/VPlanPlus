package es.jvbabi.vplanplus.feature.settings.about.ui

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.BackIcon
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsSetting
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.common.openLink

@Composable
fun AboutScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current

    AboutContent(
        onBack = { navHostController.popBackStack() },
        onOpenWebsite = {
            openLink(context, "https://vplan.plus")
        },
        onOpenPlayStore = {
            openLink(context, "https://play.google.com/store/apps/details?id=es.jvbabi.vplanplus")
        },
        onOpenRepository = {
            openLink(context, "https://github.com/VPlanPlus-Project/VPlanPlus")
        },
        onOpenPrivacyPolicy = {
            openLink(context, "https://vplan.plus/privacy")
        },
        onOpenInstagram = {
            openLink(context, "https://instagram.com/vplanplus")
        },
        onOpenThreads = {
            openLink(context, "https://www.threads.net/@vplanplus")
        },
        onOpenMastodon = {
            openLink(context, "https://mastodon.social/@vpp_app")
        },
        onOpenWhatsApp = {
            openLink(context, "https://whatsapp.com/channel/0029Vagcelf5q08Vjjc7Of1o")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
private fun AboutContent(
    onBack: () -> Unit = {},
    onOpenWebsite: () -> Unit = {},
    onOpenPlayStore: () -> Unit = {},
    onOpenRepository: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {},
    onOpenInstagram: () -> Unit = {},
    onOpenThreads: () -> Unit = {},
    onOpenMastodon: () -> Unit = {},
    onOpenWhatsApp: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settingsAbout_title)) },
                navigationIcon = { IconButton(onClick = onBack) { BackIcon() } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.padding(16.dp))
            val drawable = AnimatedImageVector.animatedVectorResource(
                if (MainActivity.isAppInDarkMode.value) R.drawable.avd_anim_dark
                else R.drawable.avd_anim
            )
            var atEnd by remember { mutableStateOf(false) }
            LaunchedEffect(drawable) { atEnd = true }
            Image(
                painter = rememberAnimatedVectorPainter(animatedImageVector = drawable, atEnd = atEnd),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = stringResource(id = R.string.app_name_full),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(
                    id = R.string.settingsAbout_version,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.labelMedium
            )
            SettingsCategory(title = stringResource(id = R.string.settingsAbout_links)) {
                SettingsSetting(
                    icon = Icons.Default.Language,
                    title = stringResource(id = R.string.settingsAbout_websiteTitle),
                    subtitle = "vplan.plus",
                    doAction = onOpenWebsite,
                    type = SettingsType.FUNCTION
                )
                SettingsSetting(
                    painter = painterResource(id = R.drawable.play_store),
                    title = stringResource(id = R.string.settingsAbout_playStoreTitle),
                    subtitle = stringResource(id = R.string.settingsAbout_playStoreSubtitle),
                    doAction = onOpenPlayStore,
                    type = SettingsType.FUNCTION
                )
                SettingsSetting(
                    painter = painterResource(id = R.drawable.github_logo),
                    title = stringResource(id = R.string.settingsAbout_repositoryTitle),
                    subtitle = "VPlanPlus-Project/VPlanPlus",
                    doAction = onOpenRepository,
                    type = SettingsType.FUNCTION
                )
                SettingsSetting(
                    icon = Icons.Default.PrivacyTip,
                    title = stringResource(id = R.string.settingsAbout_privacyPolicyTitle),
                    subtitle = stringResource(id = R.string.settingsAbout_privacyPolicySubtitle),
                    type = SettingsType.FUNCTION,
                    doAction = onOpenPrivacyPolicy,
                )
            }
            SettingsCategory(title = stringResource(id = R.string.settingsAbout_socialMedia)) {
                SettingsSetting(
                    painter = painterResource(id = R.drawable.instagram_logo),
                    title = stringResource(id = R.string.settingsAbout_socialMediaInstagramTitle),
                    subtitle = "@vplanplus",
                    type = SettingsType.FUNCTION,
                    doAction = onOpenInstagram
                )
                SettingsSetting(
                    painter = painterResource(id = R.drawable.threads_logo),
                    title = stringResource(id = R.string.settingsAbout_socialMediaThreadsTitle),
                    subtitle = "@vplanplus",
                    type = SettingsType.FUNCTION,
                    doAction = onOpenThreads
                )
                SettingsSetting(
                    painter = painterResource(id = R.drawable.mastodon_logo),
                    title = stringResource(id = R.string.settingsAbout_socialMediaMastodonTitle),
                    subtitle = "@vpp_app@mastodon.social",
                    type = SettingsType.FUNCTION,
                    doAction = onOpenMastodon
                )
                SettingsSetting(
                    painter = painterResource(id = R.drawable.whatsapp_logo),
                    title = stringResource(id = R.string.settingsAbout_socialMediaWhatsAppTitle),
                    subtitle = stringResource(id = R.string.settingsAbout_socialMediaWhatsAppSubtitle),
                    type = SettingsType.FUNCTION,
                    doAction = onOpenWhatsApp
                )
            }

            Text(
                text = stringResource(id = R.string.settingsAbout_subtitle),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun AboutScreenPreview() {
    AboutContent()
}