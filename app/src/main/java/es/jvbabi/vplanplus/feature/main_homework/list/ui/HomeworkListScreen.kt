package es.jvbabi.vplanplus.feature.main_homework.list.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_homework.list.ui.components.BadProfileType
import es.jvbabi.vplanplus.feature.main_homework.list_old.ui.components.HomeworkCardItem
import es.jvbabi.vplanplus.ui.common.Spacer4Dp

@Composable
fun HomeworkListScreen(
    navHostController: NavHostController,
    viewModel: HomeworkListViewModel = hiltViewModel(),
    navBar: @Composable (visible: Boolean) -> Unit
) {
    val state = viewModel.state
    HomeworkListContent(state, { navBar(true) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeworkListContent(
    state: HomeworkListState,
    navBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.homework_title)) })
        },
        floatingActionButton = {
            if (state.userUsesFalseProfileType) return@Scaffold
            ExtendedFloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer4Dp()
                Text(text = stringResource(id = R.string.home_addHomeworkLabel))
            }
        },
        bottomBar = navBar
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)) content@{
            if (state.userUsesFalseProfileType) {
                BadProfileType()
                return@content
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {}
                state.filters.forEach { filter ->
                    item {
                        AssistChip(
                            onClick = {
                                when (filter) {
                                    is HomeworkFilter.VisibilityFilter -> {
                                        TODO()
                                    }
                                    is HomeworkFilter.CompletionFilter -> {
                                        TODO()
                                    }
                                }
                            },
                            label = { Text(text = filter.buildLabel()) },
                            leadingIcon = filter.buildLeadingIcon(),
                            trailingIcon = filter.buildTrailingIcon()
                        )
                    }
                }
            }

            LazyColumn {
                items(state.homework) { homework ->
                    HomeworkCardItem(homework = homework, currentVppId = state.profile?.vppId)
                }

            }
        }
    }
}

@Composable
@Preview
private fun HomeworkListContentPreview() {
    HomeworkListContent(
        HomeworkListState()
    )
}

@Composable
@Preview
private fun BadProfileTypePreview() {
    HomeworkListContent(
        HomeworkListState(userUsesFalseProfileType = true)
    )
}