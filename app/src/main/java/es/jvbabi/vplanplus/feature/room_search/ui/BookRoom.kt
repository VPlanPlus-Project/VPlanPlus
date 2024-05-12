package es.jvbabi.vplanplus.feature.room_search.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.Setting
import es.jvbabi.vplanplus.ui.common.SettingsCategory
import es.jvbabi.vplanplus.ui.common.SettingsState
import es.jvbabi.vplanplus.ui.common.SettingsType
import es.jvbabi.vplanplus.ui.common.toLocalizedString

@Composable
fun BookRoom(
    navHostController: NavHostController,
    roomName: String?,
    viewModel: BookRoomViewModel = hiltViewModel()
) {
    if (roomName == null) {
        navHostController.navigateUp()
        return
    }
    LaunchedEffect(key1 = roomName) { viewModel.init(roomName) }
    val state = viewModel.state.value

    LaunchedEffect(key1 = state.allSuccessful) {
        if (state.allSuccessful == true) navHostController.navigateUp()
    }


    BookRoomContent(
        onBack = { navHostController.navigateUp() },
        onUpdateLessonTime = viewModel::toggleLessonTime,
        onHideDisclaimerBanner = viewModel::hideDisclaimerBanner,
        onBookingConfirm = viewModel::confirmBooking,
        state = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookRoomContent(
    onBack: () -> Unit = {},
    onUpdateLessonTime: (Int) -> Unit = {},
    onHideDisclaimerBanner: () -> Unit = {},
    onBookingConfirm: () -> Unit = {},
    state: BookRoomState
) {
    var showAll by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.bookRoom_title))
                        Text(text = if (state.room == null) stringResource(id = R.string.loadingData) else stringResource(id = R.string.bookRoom_subtitle, state.room.name), style = MaterialTheme.typography.labelSmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = stringResource(id = R.string.close))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            SettingsCategory(
                title = stringResource(id = R.string.bookRoom_lessonTimesTitle),
            ) {
                state.lessons.toList().forEachIndexed { i, (lessonTime, lessonTimeState) ->
                    AnimatedVisibility(
                        visible = i < 3 || showAll,
                        enter = expandVertically(tween()),
                        exit = shrinkVertically(tween())
                    ) {
                        Setting(
                            SettingsState(
                                title = stringResource(id = R.string.bookRoom_lessonTimesItemTitle, lessonTime.lessonNumber.toLocalizedString()),
                                subtitle = lessonTime.toTimeString() + if (lessonTimeState == BookTimeState.CONFLICT) " $DOT " + stringResource(id = R.string.bookRoom_conflict) else "",
                                type = SettingsType.CHECKBOX,
                                doAction = { onUpdateLessonTime(lessonTime.lessonNumber) },
                                checked = lessonTimeState == BookTimeState.ACTIVE,
                                enabled = lessonTimeState != BookTimeState.CONFLICT
                            )
                        )
                    }
                }
                AnimatedVisibility(
                    visible = !showAll,
                    enter = expandVertically(tween()),
                    exit = shrinkVertically(tween())
                ) {
                    TextButton(
                        onClick = { showAll = true },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = stringResource(id = R.string.bookRoom_showAll))
                    }
                }
                AnimatedVisibility(
                    visible = state.showDisclaimerBanner,
                    enter = expandVertically(tween()),
                    exit = shrinkVertically(tween()),
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    InfoCard(
                        imageVector = Icons.Default.Warning,
                        title = stringResource(id = R.string.disclaimer),
                        text = stringResource(id = R.string.searchAvailableRoom_disclaimerText),
                        buttonAction1 = onHideDisclaimerBanner,
                        buttonText1 = stringResource(id = android.R.string.ok)
                    )
                }
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .weight(1f, true)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        Text(text = stringResource(id = R.string.back))
                    }
                    Button(
                        onClick = onBookingConfirm,
                        enabled = !state.isBookingLoading && state.lessons.any { it.value == BookTimeState.ACTIVE },
                        modifier = Modifier
                            .weight(1f, true)
                    ) {
                        Box {
                            androidx.compose.animation.AnimatedVisibility(visible = state.isBookingLoading) {
                                CircularProgressIndicator(Modifier.size(24.dp))
                            }
                            androidx.compose.animation.AnimatedVisibility(visible = !state.isBookingLoading) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                    Text(text = stringResource(id = R.string.bookRoom_confirm))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun BookRoomPreview() {
    BookRoomContent(
        state = BookRoomState(isBookingLoading = false)
    )
}