package es.jvbabi.vplanplus.ui.screens.settings.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.ui.common.Badge
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.screens.Screen
import kotlinx.coroutines.launch

@Composable
fun ProfileManagementScreen(
    navController: NavHostController,
    onNewProfileClicked: (school: School) -> Unit = {},
    onNewSchoolClicked: () -> Unit,
    viewModel: ProfileManagementViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scope = rememberCoroutineScope()
    LaunchedEffect("Init") {
        if (state.schools.isEmpty() && !state.isLoading) viewModel.init()
    }

    ProfileManagementScreenContent(
        onBackClicked = { navController.popBackStack() },
        state = state,
        onNewSchoolProfileClicked = {
            scope.launch {
                val school = viewModel.getSchoolByName(it)
                onNewProfileClicked(school)
                navController.navigate(Screen.OnboardingNewProfileScreen.route + "/${school.id!!}")
            }
        },
        onProfileDeleteDialogOpen = { viewModel.onProfileDeleteDialogOpen(it) },
        onProfileDeleteDialogClose = { viewModel.onProfileDeleteDialogClose() },
        onProfileDeleteDialogYes = { viewModel.deleteProfile(it) },
        onSnackbarDone = { viewModel.setDeleteProfileResult(null) },
        onNewSchoolClicked = {
            onNewSchoolClicked()
            navController.navigate(Screen.OnboardingSchoolIdScreen.route)
        }
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileManagementScreenContent(
    onBackClicked: () -> Unit = {},
    state: ProfileManagementState,
    onNewSchoolProfileClicked: (schoolName: String) -> Unit = {},
    onNewSchoolClicked: () -> Unit = {},
    onProfileDeleteDialogOpen: (profile: ProfileManagementProfile) -> Unit = {},
    onProfileDeleteDialogClose: () -> Unit = {},
    onProfileDeleteDialogYes: (profile: ProfileManagementProfile) -> Unit = {},
    onSnackbarDone: () -> Unit = {}
) {
    val snackbarState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarState)
        },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.profileManagement_title)) },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNewSchoolClicked() },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(id = R.string.profileManagement_addNewSchool))
                        Badge(color = Color.Magenta, text = "Untested")
                    }
                },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
        }
    ) { pv ->
        val lastProfileMessage = stringResource(id = R.string.profileManagement_lastProfileError)
        val ok = stringResource(id = R.string.ok)

        if (state.deleteProfileResult == ProfileManagementDeletionResult.LAST_PROFILE) {
            rememberCoroutineScope().launch {
                snackbarState.showSnackbar(
                    message = lastProfileMessage,
                    actionLabel = ok,
                    duration = SnackbarDuration.Short
                )
                onSnackbarDone()
            }

        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 16.dp)
        ) {
            if (state.deleteProfileDialogProfile != null) {
                YesNoDialog(
                    icon = Icons.Default.Delete,
                    title = stringResource(id = R.string.profileManagement_deleteProfileDialogTitle),
                    message = stringResource(
                        id = R.string.profileManagement_deleteProfileDialogText,
                        state.deleteProfileDialogProfile.name
                    ),
                    onYes = {
                        onProfileDeleteDialogYes(state.deleteProfileDialogProfile)
                        onProfileDeleteDialogClose()
                    },
                    onNo = {
                        onProfileDeleteDialogClose()
                    }
                )
            }
            LazyColumn {
                items(state.schools.sortedBy { it.name }) { school ->
                    Card(
                        colors = CardDefaults.cardColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = school.name,
                                modifier = Modifier
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(start = 16.dp, bottom = 16.dp),
                            ) {
                                school.profiles.forEach {
                                    ProfileCard(
                                        type = it.type,
                                        name = it.name,
                                        modifier = Modifier.clickable { onProfileDeleteDialogOpen(it) }
                                    )
                                }
                                ProfileCard(
                                    type = null,
                                    name = "+",
                                    modifier = Modifier.clickable { onNewSchoolProfileClicked(school.name) }
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun ProfileCard(type: ProfileType?, name: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(),
        border = if (type != null) BorderStroke(1.dp, Color.Black) else null,
        modifier = Modifier
            .padding(end = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .then(modifier)
            .size(width = 80.dp, height = 80.dp)
            .dashedBorder(if (type == null) 2.dp else 0.dp, Color.Black, 8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                if (type != null) {
                    Text(text = name, style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = when (type) {
                            ProfileType.STUDENT -> "Klasse" // TODO stringResource
                            ProfileType.TEACHER -> "Lehrer"
                            ProfileType.ROOM -> "Raum"
                        }, style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(text = "+", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }

}

@Preview
@Composable
fun ProfileCardPreview() {
    ProfileCard(ProfileType.STUDENT, "7a")
}

@Composable
@Preview(showBackground = true)
fun ProfileManagementScreenPreview() {
    ProfileManagementScreenContent(
        state = ProfileManagementState(
            listOf(
                ProfileManagementSchool(
                    "Waldschule",
                    listOf(
                        ProfileManagementProfile(
                            id = 0,
                            name = "7a",
                            type = ProfileType.STUDENT
                        ),
                        ProfileManagementProfile(
                            id = 1,
                            name = "207",
                            type = ProfileType.ROOM
                        ),
                        ProfileManagementProfile(
                            id = 3,
                            name = "Mul",
                            type = ProfileType.TEACHER
                        )
                    )
                ),
                ProfileManagementSchool(
                    "200.009. Oberschule Dresden",
                    listOf(
                        ProfileManagementProfile(
                            id = 4,
                            name = "Mul",
                            type = ProfileType.TEACHER
                        ),
                    )
                )
            ),
            deleteProfileDialogProfile = null,
            deleteProfileResult = ProfileManagementDeletionResult.LAST_PROFILE
        )
    )
}

// https://stackoverflow.com/a/67039676/16682019
fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp): Modifier {
    if (strokeWidth == 0.dp) return this
    return composed(
        factory = {
            val density = LocalDensity.current
            val strokeWidthPx = density.run { strokeWidth.toPx() }
            val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

            this.then(
                Modifier.drawWithCache {
                    onDrawBehind {
                        val stroke = Stroke(
                            width = strokeWidthPx,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )

                        drawRoundRect(
                            color = color,
                            style = stroke,
                            cornerRadius = CornerRadius(cornerRadiusPx)
                        )
                    }
                }
            )
        }
    )
}