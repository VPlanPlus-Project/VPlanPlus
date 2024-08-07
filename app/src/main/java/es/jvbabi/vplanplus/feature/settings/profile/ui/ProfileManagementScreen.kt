package es.jvbabi.vplanplus.feature.settings.profile.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.lightspark.composeqr.DotShape
import com.lightspark.composeqr.QrCodeColors
import com.lightspark.composeqr.QrCodeView
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.feature.settings.profile.ui.components.SchoolCard
import es.jvbabi.vplanplus.feature.settings.profile.ui.components.UpdateCredentialsDialog
import es.jvbabi.vplanplus.ui.common.ComposableDialog
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.screens.Screen
import kotlinx.coroutines.launch

@Composable
fun ProfileManagementScreen(
    navController: NavHostController,
    onNewProfileClicked: (school: School) -> Unit = {},
    onNewSchoolClicked: () -> Unit,
    viewModel: ProfileManagementViewModel = hiltViewModel(),
    task: ProfileManagementTask?
) {
    val state = viewModel.state.value
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(key1 = task) {
        if (state.taskCompleted) return@LaunchedEffect
        if (task is UpdateCredentialsTask) viewModel.openUpdateCredentialsDialog(task.schoolId)
    }

    ProfileManagementScreenContent(
        onBackClicked = { navController.navigateUp() },
        state = state,
        onNewSchoolProfileClicked = { school ->
            scope.launch {
                onNewProfileClicked(school)
                navController.navigate(Screen.OnboardingNewProfileScreen(school.id)) {
                    popUpTo(Screen.SettingsProfileScreen.route)
                }
            }
        },
        onProfileClicked = { navController.navigate("${Screen.SettingsProfileScreen.route}/${it.id}") },
        onNewSchoolClicked = {
            onNewSchoolClicked()
            navController.navigate(Screen.OnboardingSchoolIdScreen.route)
        },
        onDeleteSchoolOpenDialog = { viewModel.openDeleteSchoolDialog(it) },
        onDeleteSchoolConfirm = { viewModel.deleteSchool() },
        onDeleteSchoolDismiss = { viewModel.closeDeleteSchoolDialog() },
        onShareSchool = { viewModel.share(it) },
        onCloseShareDialog = { viewModel.closeShareDialog() },
        onUpdateCredentialsRequest = viewModel::openUpdateCredentialsDialog,
        onResetUpdateCredentialsValidity = viewModel::resetUpdateCredentialsValidity,
        onCheckCredentialsValidity = viewModel::checkCredentialsValidity,
        onUpdateCredentials = viewModel::confirmNewCredentials,
        onCancelUpdateCredentialsRequest = viewModel::closeUpdateCredentialsDialog
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileManagementScreenContent(
    onBackClicked: () -> Unit = {},
    state: ProfileManagementState,
    onNewSchoolProfileClicked: (school: School) -> Unit = {},
    onNewSchoolClicked: () -> Unit = {},
    onProfileClicked: (profile: Profile) -> Unit = {},
    onDeleteSchoolOpenDialog: (school: School) -> Unit = {},
    onDeleteSchoolConfirm: () -> Unit = {},
    onDeleteSchoolDismiss: () -> Unit = {},
    onCloseShareDialog: () -> Unit = {},
    onShareSchool: (school: School) -> Unit = {},
    onUpdateCredentialsRequest: (schoolId: Int) -> Unit = {},
    onResetUpdateCredentialsValidity: () -> Unit = {},
    onCheckCredentialsValidity: (username: String, password: String) -> Unit = { _, _ -> },
    onUpdateCredentials: (username: String, password: String) -> Unit = { _, _ -> },
    onCancelUpdateCredentialsRequest: () -> Unit = {}
) {

    if (state.changeCredentials != null) {
        UpdateCredentialsDialog(
            schoolName = state.changeCredentials.school.name,
            username = state.changeCredentials.school.username,
            password = state.changeCredentials.school.password,
            isLoading = state.changeCredentials.isLoading,
            isValid = state.changeCredentials.isValid,
            hasError = state.changeCredentials.hasError,
            onResetValidity = onResetUpdateCredentialsValidity,
            onCheckValidity = onCheckCredentialsValidity,
            onConfirm = onUpdateCredentials,
            onCancel = onCancelUpdateCredentialsRequest
        )
    }

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
                    Text(text = stringResource(id = R.string.profileManagement_addNewSchool))
                },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
        }
    ) { pv ->
        if (state.shareSchool != null) {
            ComposableDialog(
                icon = Icons.Default.Share,
                title = "Share",
                content = {
                    Column {
                        QrCodeView(
                            data = Gson().toJson(state.shareSchool),
                            modifier = Modifier.size(300.dp),
                            colors = QrCodeColors(
                                background = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                                foreground = MaterialTheme.colorScheme.onSurface
                            ),
                            dotShape = DotShape.Circle
                        ) {}
                        Row {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = 2.dp, end = 8.dp)
                                    .size(16.dp)
                            )
                            Text(text = stringResource(id = R.string.settings_profileShareNote))
                        }
                    }
                },
                onOk = { onCloseShareDialog() }
            )
        }

        if (state.deletingSchool != null) {
            YesNoDialog(
                icon = Icons.Default.Delete,
                title = stringResource(id = R.string.profileManagement_deleteSchoolTitle),
                message = stringResource(id = R.string.profileManagement_deleteSchoolText, state.deletingSchool.name),
                onYes = { onDeleteSchoolConfirm() },
                onNo = { onDeleteSchoolDismiss() }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn {
                items(state.profiles.toList().sortedBy { it.first.name }) { (school, profiles) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        SchoolCard(
                            school = school,
                            profiles = profiles,
                            onAddProfileClicked = { onNewSchoolProfileClicked(school) },
                            onProfileClicked = onProfileClicked,
                            onDeleteRequest = { onDeleteSchoolOpenDialog(school) },
                            onShareRequest = { onShareSchool(school) },
                            onUpdateCredentialsRequest = { onUpdateCredentialsRequest(school.id) }
                        )
                    }
                }
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
fun ProfileManagementScreenPreview() {
    val school1 = SchoolPreview.generateRandomSchools(1).first()
    val school2 = SchoolPreview.generateRandomSchools(1).first()
    val group1 = GroupPreview.generateGroup(school1)
    val group2 = GroupPreview.generateGroup(school2)
    ProfileManagementScreenContent(
        state = ProfileManagementState(
            profiles = mapOf(
                school1 to listOf(ProfilePreview.generateClassProfile(group1)),
                school2 to listOf(ProfilePreview.generateClassProfile(group2))
            )
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
                Modifier.drawWithContent {
                    drawContent()
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
            )
        }
    )
}

sealed class ProfileManagementTask(val name: String)

class UpdateCredentialsTask(val schoolId: Int) : ProfileManagementTask(NAME) {
    companion object {
        const val NAME = "update_credentials"
    }
}