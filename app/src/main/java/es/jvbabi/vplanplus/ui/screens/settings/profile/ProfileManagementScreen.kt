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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.ui.common.YesNoDialog
import es.jvbabi.vplanplus.ui.screens.Screen
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ProfileManagementScreen(
    navController: NavHostController,
    onNewProfileClicked: (school: School) -> Unit = {},
    onNewSchoolClicked: () -> Unit,
    viewModel: ProfileManagementViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scope = rememberCoroutineScope()

    ProfileManagementScreenContent(
        onBackClicked = { navController.popBackStack() },
        state = state,
        onNewSchoolProfileClicked = {
            scope.launch {
                val school = viewModel.getSchoolByName(it)
                onNewProfileClicked(school)
                navController.navigate(Screen.OnboardingNewProfileScreen.route + "/${school.schoolId}") {
                    popUpTo(Screen.SettingsProfileScreen.route)
                }
            }
        },
        onProfileClicked = { navController.navigate(Screen.SettingsProfileScreen.route + it.id) },
        onNewSchoolClicked = {
            onNewSchoolClicked()
            navController.navigate(Screen.OnboardingSchoolIdScreen.route)
        },
        onDeleteSchoolOpenDialog = { viewModel.openDeleteSchoolDialog(it) },
        onDeleteSchoolConfirm = { viewModel.deleteSchool() },
        onDeleteSchoolDismiss = { viewModel.closeDeleteSchoolDialog() }
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
    onProfileClicked: (profile: ProfileManagementProfile) -> Unit = {},
    onDeleteSchoolOpenDialog: (school: ProfileManagementSchool) -> Unit = {},
    onDeleteSchoolConfirm: (school: ProfileManagementSchool) -> Unit = {},
    onDeleteSchoolDismiss: () -> Unit = {}
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
                    Text(text = stringResource(id = R.string.profileManagement_addNewSchool))
                },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
            )
        }
    ) { pv ->

        if (state.deletingSchool != null) {
            YesNoDialog(
                icon = Icons.Default.Delete,
                title = stringResource(id = R.string.profileManagement_deleteSchoolTitle),
                message = stringResource(id = R.string.profileManagement_deleteSchoolText, state.deletingSchool.name),
                onYes = { onDeleteSchoolConfirm(state.deletingSchool) },
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = school.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                        .weight(1f, false),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                if (state.schools.size > 1) IconButton(
                                    onClick = { onDeleteSchoolOpenDialog(school) },
                                    modifier = Modifier.padding(start = 16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null
                                    )
                                }
                            }
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
                                        modifier = Modifier.clickable { onProfileClicked(it) }
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
                            ProfileType.STUDENT -> stringResource(id = R.string.classStr)
                            ProfileType.TEACHER -> stringResource(id = R.string.teacher)
                            ProfileType.ROOM -> stringResource(id = R.string.room)
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
                            id = UUID.randomUUID(),
                            name = "7a",
                            type = ProfileType.STUDENT
                        ),
                        ProfileManagementProfile(
                            id = UUID.randomUUID(),
                            name = "207",
                            type = ProfileType.ROOM
                        ),
                        ProfileManagementProfile(
                            id = UUID.randomUUID(),
                            name = "Mul",
                            type = ProfileType.TEACHER
                        )
                    )
                ),
                ProfileManagementSchool(
                    "200.009. Oberschule Dresden",
                    listOf(
                        ProfileManagementProfile(
                            id = UUID.randomUUID(),
                            name = "Mul",
                            type = ProfileType.TEACHER
                        ),
                    )
                )
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