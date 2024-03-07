package es.jvbabi.vplanplus.ui.screens.id_link

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import es.jvbabi.vplanplus.ui.screens.Screen
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
        onBack = {
            navHostController.navigate(Screen.HomeScreen.route) {
                popUpTo(0)
            }
        },
        onRetry = {
            vppIdLinkViewModel.init(token)
        }
    )
}

@Composable
private fun VppIdLinkScreenContent(
    onBack: () -> Unit = {},
    onRetry: () -> Unit = {},
    state: VppIdLinkState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) CircularProgressIndicator()
        else if (state.error || state.vppId!!.classes == null) {
            Column {
                Text(text = "Error")
                Button(onClick = onRetry) {
                    Text(text = "Retry/Erneut versuchen")
                }
            }
        }
        else if (state.response == HttpStatusCode.OK) {
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
                Button(onClick = { onBack() }) {
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
private fun VppIdLinkScreenPreview() {
    val school = PreviewSchool.generateRandomSchools(1).first()
    val classes = PreviewClasses.generateClass(school)
    VppIdLinkScreenContent(
        state = VppIdLinkState(
            isLoading = false,
            response = HttpStatusCode.OK,
            vppId = VppId(
                id = 1,
                name = "Maria Musterfrau",
                schoolId = school.schoolId,
                school = school,
                className = classes.name,
                classes = classes,
                email = "maria.musterfrau@email.com"
            ),
        )
    )
}