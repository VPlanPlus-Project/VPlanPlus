package es.jvbabi.vplanplus.ui.screens.id_link

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.id_link.components.AutoConnectToProfile
import es.jvbabi.vplanplus.ui.screens.id_link.components.LinkNoProfiles
import io.ktor.http.HttpStatusCode
import es.jvbabi.vplanplus.ui.preview.ClassesPreview as PreviewClasses
import es.jvbabi.vplanplus.ui.preview.School as PreviewSchool

@Composable
fun VppIdLinkScreen(
    navHostController: NavHostController,
    token: String?,
    vppIdLinkViewModel: VppIdLinkViewModel = hiltViewModel()
) {
    val state = vppIdLinkViewModel.state.value

    LaunchedEffect(token) {
        vppIdLinkViewModel.init(token)
    }

    VppIdLinkScreenContent(
        state = state,
        onOk = {
            vppIdLinkViewModel.onProceed()
            navHostController.navigate(Screen.HomeScreen.route) {
                popUpTo(0)
            }
        },
        onRetry = {
            vppIdLinkViewModel.init(token)
        },
        onContactUs = { userId, className ->
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = android.net.Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("julvin.babies@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "VPP-ID Link Fehler")
            intent.putExtra(
                Intent.EXTRA_TEXT,
                """
                    Bitte fülle die Daten aus (vorausgefüllte Werte bitte lassen):
                    vpp.ID Nutzer-ID, beste.schule E-Mail oder Nutzername: ${userId ?: "eingeben"}
                    Klasse: ${className ?: "eingeben"}
                    
                    Wir melden uns, sobald du dich anmelden kannst.
                    Alternativ kannst du uns auch auf Instagram (@vplanplus) kontaktieren.
                """.trimIndent()
            )
            navHostController.context.startActivity(intent)
        }
    )
}

@Composable
private fun VppIdLinkScreenContent(
    onOk: () -> Unit = {},
    onRetry: () -> Unit = {},
    onContactUs: (userId: Int?, className: String?) -> Unit = { _, _ -> },
    state: VppIdLinkState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) CircularProgressIndicator()
        else if (state.classNotFound) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .size(48.dp)
                )
                Text(
                    text = stringResource(id = R.string.vppIdLink_invalidClassTitle),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.vppIdLink_invalidClassText, state.vppId?.className ?: "unknown"),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    OutlinedButton(
                        onClick = onOk,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) { Text(text = stringResource(id = R.string.back)) }
                    Button(
                        onClick = { onContactUs(state.vppId?.id, state.vppId?.className) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.vppIdLink_contactUs))
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        } else if (state.error || state.vppId!!.classes == null) {
            Column {
                Text(text = "Error")
                Button(onClick = onRetry) {
                    Text(text = "Retry/Erneut versuchen")
                }
            }
        } else if (state.response == HttpStatusCode.OK) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = stringResource(id = R.string.vppidlink_welcome, state.vppId.name),
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = stringResource(
                        id = R.string.vppidlink_message,
                        state.vppId.classes!!.school.name,
                        state.vppId.classes.name
                    ),
                    textAlign = TextAlign.Center
                )
                if (state.selectedProfileFoundAtStart == true) {
                    AutoConnectToProfile(
                        modifier = Modifier.padding(vertical = 8.dp),
                        profileName = state.selectedProfiles.toList().first { it.second }.first.displayName
                    )
                } else if (state.selectedProfiles.isEmpty()) {
                    LinkNoProfiles(
                        modifier = Modifier.padding(vertical = 8.dp),
                        className = state.vppId.classes.name
                    )
                }
                Button(enabled = state.selectedProfiles.any { it.value } || state.selectedProfiles.isEmpty(), onClick = { onOk() }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        } else {
            Text(text = "Something bad happened: ${state.response}")
        }
    }
}

@Preview
@Composable
private fun VppIdLinkScreenInvalidClassPreview() {
    VppIdLinkScreenContent(
        state = VppIdLinkState(
            isLoading = false,
            error = true,
            classNotFound = true
        )
    )
}

@Preview
@Composable
private fun VppIdLinkScreenAutoLinkPreview() {
    val school = PreviewSchool.generateRandomSchools(1).first()
    val classes = PreviewClasses.generateClass(school)
    val vppId = VppId(
        id = 1,
        name = "Maria Musterfrau",
        schoolId = school.schoolId,
        school = school,
        className = classes.name,
        classes = classes,
        email = "maria.musterfrau@email.com"
    )
    VppIdLinkScreenContent(
        state = VppIdLinkState(
            isLoading = false,
            response = HttpStatusCode.OK,
            selectedProfileFoundAtStart = true,
            selectedProfiles = mapOf(
                ProfilePreview.generateClassProfile(vppId) to true
            ),
            vppId = vppId,
        )
    )
}

@Preview
@Composable
private fun VppIdLinkScreenPreview() {
    val school = PreviewSchool.generateRandomSchools(1).first()
    val classes = PreviewClasses.generateClass(school)
    val vppId = VppId(
        id = 1,
        name = "Maria Musterfrau",
        schoolId = school.schoolId,
        school = school,
        className = classes.name,
        classes = classes,
        email = "maria.musterfrau@email.com"
    )
    VppIdLinkScreenContent(
        state = VppIdLinkState(
            isLoading = false,
            response = HttpStatusCode.OK,
            selectedProfileFoundAtStart = false,
            selectedProfiles = mapOf(
                ProfilePreview.generateClassProfile(vppId) to true,
                ProfilePreview.generateClassProfile() to false
            ),
            vppId = vppId,
        )
    )
}