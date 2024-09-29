package es.jvbabi.vplanplus.feature.main_home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.lessons.LessonBlock
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.menu.Menu
import es.jvbabi.vplanplus.feature.main_home.ui.components.Head
import es.jvbabi.vplanplus.feature.main_home.ui.components.ImportantHeader
import es.jvbabi.vplanplus.feature.main_home.ui.components.NoData
import es.jvbabi.vplanplus.feature.main_home.ui.components.QuickActions
import es.jvbabi.vplanplus.feature.main_home.ui.components.VersionHintsInformation
import es.jvbabi.vplanplus.feature.main_home.ui.components.banners.BadCredentialsBanner
import es.jvbabi.vplanplus.feature.main_home.ui.components.cards.MissingVppIdLinkToProfileCard
import es.jvbabi.vplanplus.feature.main_home.ui.components.content.today.CurrentLesson
import es.jvbabi.vplanplus.feature.main_home.ui.components.next.HomeworkSection
import es.jvbabi.vplanplus.feature.main_home.ui.components.next.Info
import es.jvbabi.vplanplus.feature.main_home.ui.components.next.Subjects
import es.jvbabi.vplanplus.feature.main_home.ui.components.next.Title
import es.jvbabi.vplanplus.feature.main_home.ui.preview.navBar
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheet
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheetInitialValues
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.onLogin
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.keyboardAsState
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.common.toLocalizedString
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.PreviewFunction
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview.toActiveVppId
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.runComposable
import java.time.ZonedDateTime

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    navBar: @Composable (expanded: Boolean) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val state = homeViewModel.state
    val context = LocalContext.current

    HomeScreenContent(
        navBar = navBar,
        state = state,
        onBookRoomClicked = { navHostController.navigate(Screen.SearchAvailableRoomScreen.route) },
        onOpenMenu = homeViewModel::onMenuOpenedChange,
        onVersionHintsClosed = homeViewModel::hideVersionHintsDialog,

        onSwitchProfile = homeViewModel::switchProfile,
        onManageProfiles = {
            homeViewModel.onMenuOpenedChange(false)
            navHostController.navigate(Screen.SettingsProfileScreen.route)
        },
        onManageProfile = {
            homeViewModel.onMenuOpenedChange(false)
            navHostController.navigate("${Screen.SettingsProfileScreen.route}/${it.id}")
        },
        onOpenNews = { homeViewModel.onMenuOpenedChange(false); navHostController.navigate(Screen.NewsScreen.route) },
        onOpenSettings = {
            homeViewModel.onMenuOpenedChange(false); navHostController.navigate(
            Screen.SettingsScreen.route
        )
        },
        onPrivacyPolicyClicked = {
            openLink(
                context,
                "${state.server.uiHost}/privacy"
            )
        },
        onRepositoryClicked = {
            openLink(
                context,
                "https://github.com/VPlanPlus-Project/VPlanPlus"
            )
        },
        onOpenSearch = { navHostController.navigate(Screen.SearchScreen.route) },
        onRefreshClicked = {
            homeViewModel.onMenuOpenedChange(false); homeViewModel.onRefreshClicked(
            context
        )
        },
        onFixVppIdSessionClicked = { onLogin(context, state.server) },
        onFixVppIdLinksClicked = { navHostController.navigate(Screen.SettingsVppIdScreen.route) },
        onIgnoreInvalidVppIdSessions = homeViewModel::ignoreInvalidVppIdSessions,
        onFixCredentialsClicked = { navHostController.navigate("${Screen.SettingsProfileScreen.route}?task=update_credentials&schoolId=${state.currentProfile?.getSchool()?.id}") },
        onSendFeedback = { navHostController.navigate(Screen.SettingsHelpFeedbackScreen.route) },
        onOpenHomework = { homeworkId -> navHostController.navigate("${Screen.HomeworkDetailScreen.route}/$homeworkId") },
    )
}

@Composable
fun HomeScreenContent(
    navBar: @Composable (expanded: Boolean) -> Unit,
    state: HomeState,
    onOpenMenu: (state: Boolean) -> Unit = {},
    onBookRoomClicked: () -> Unit,
    onOpenSearch: () -> Unit = {},

    onSwitchProfile: (to: Profile) -> Unit,
    onManageProfiles: () -> Unit = {},
    onManageProfile: (profile: Profile) -> Unit = {},
    onOpenNews: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onPrivacyPolicyClicked: () -> Unit = {},
    onRepositoryClicked: () -> Unit = {},
    onRefreshClicked: () -> Unit = {},

    onFixVppIdSessionClicked: () -> Unit = {},
    onIgnoreInvalidVppIdSessions: () -> Unit = {},
    onFixVppIdLinksClicked: () -> Unit = {},

    onFixCredentialsClicked: () -> Unit = {},

    onOpenHomework: (homeworkId: Int) -> Unit = {},

    onSendFeedback: () -> Unit = {},

    onVersionHintsClosed: (untilNextVersion: Boolean) -> Unit = {}
) {
    if (state.currentProfile == null) return

    if (state.isVersionHintsDialogOpen && state.versionHint != null) VersionHintsInformation(
        currentVersion = state.currentVersion,
        hint = state.versionHint,
        onCloseUntilNextTime = { onVersionHintsClosed(false) },
        onCloseUntilNextVersion = { onVersionHintsClosed(true) }
    )

    var addHomeworkSheetInitialValues by rememberSaveable<MutableState<AddHomeworkSheetInitialValues?>> {
        mutableStateOf(
            null
        )
    }
    if (addHomeworkSheetInitialValues != null) {
        AddHomeworkSheet(
            onClose = { addHomeworkSheetInitialValues = null },
            initialValues = addHomeworkSheetInitialValues ?: AddHomeworkSheetInitialValues()
        )
    }

    Scaffold(
        bottomBar = { navBar(!keyboardAsState().value) },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            Spacer4Dp()
            Head(
                profile = state.currentProfile,
                currentTime = state.currentTime,
                isSyncing = state.isSyncRunning,
                showNotificationDot = state.hasUnreadNews,
                onProfileClicked = { onOpenMenu(true) },
                onSearchClicked = onOpenSearch,
            )
            Spacer8Dp()
            Collapsable(
                expand = state.hasMissingVppIdToProfileLinks || state.hasInvalidVppIdSession
            ) { ImportantHeader(Modifier.padding(horizontal = 8.dp)) }
            Collapsable(expand = state.hasInvalidVppIdSession) {
                InfoCard(
                    imageVector = Icons.Default.NoAccounts,
                    title = stringResource(id = R.string.home_invalidVppIdSessionTitle),
                    text = stringResource(id = R.string.home_invalidVppIdSessionText),
                    buttonText1 = stringResource(id = R.string.ignore),
                    buttonAction1 = onIgnoreInvalidVppIdSessions,
                    buttonText2 = stringResource(id = R.string.fix),
                    buttonAction2 = onFixVppIdSessionClicked,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }
            Collapsable(expand = state.hasMissingVppIdToProfileLinks) {
                MissingVppIdLinkToProfileCard(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    onFixClicked = onFixVppIdLinksClicked
                )
            }
            BadCredentialsBanner(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                expand = state.currentProfile.getSchool().credentialsValid == false,
                onFixCredentialsClicked = onFixCredentialsClicked
            )

            QuickActions(
                modifier = Modifier.padding(bottom = 8.dp),
                onNewHomeworkClicked = {
                    addHomeworkSheetInitialValues = AddHomeworkSheetInitialValues()
                },
                onFindAvailableRoomClicked = onBookRoomClicked,
                onSendFeedback = onSendFeedback,
                allowHomeworkQuickAction = (state.currentProfile as? ClassProfile)?.isHomeworkEnabled
                    ?: false
            )

            Spacer16Dp()

            AnimatedContent(
                targetState = state.today != null,
                transitionSpec = {
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut())
                },
                label = "Today",
                modifier = Modifier.fillMaxSize()
            ) { isTodayVisible ->
                if (!isTodayVisible || state.today == null) return@AnimatedContent
                if (!state.today.isDayOver()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        val currentOrNextLessons = state.today.getCurrentOrNextLesson()
                        currentOrNextLessons?.let { currentOrNextLesson ->
                            Row(verticalAlignment = Alignment.CenterVertically) title@{
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 16.dp, end = 8.dp)
                                        .size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = if (currentOrNextLesson.isCurrent) "Aktuell" else "Als nächstes",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                    )
                                    Text(
                                        text = currentOrNextLesson.lessons.first().lessonNumber.toLocalizedString() + " Stunde",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer8Dp()

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surface),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                currentOrNextLesson.lessons.forEach { lesson ->
                                    CurrentLesson(
                                        lesson,
                                        state.today.homework.filter { it.homework.defaultLesson == (lesson as? Lesson.SubstitutionPlanLesson)?.defaultLesson && it.homework.defaultLesson != null })
                                }
                            }
                        }

                        val followingLessons = state.today.lessons
                            .filter {
                                it.lessonNumber > (currentOrNextLessons?.lessons?.firstOrNull()?.lessonNumber
                                    ?: -1)
                            }
                            .groupBy { it.lessonNumber }

                        if (followingLessons.isNotEmpty()) {
                            Spacer16Dp()

                            Row(verticalAlignment = Alignment.CenterVertically) title@{
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(start = 16.dp, end = 8.dp)
                                        .size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Weitere Stunden",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                    )
                                    Text(
                                        text = "${followingLessons.filter { it.value.all { l -> l.displaySubject != "-" } }.size} Stunden verbleibend",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer8Dp()

                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                followingLessons
                                    .forEach { (lessonNumber, lessons) ->
                                        LessonBlock(
                                            lessonNumber,
                                            lessons,
                                            MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                            }
                        }
                    }
                    if (state.nextSchoolDay != null) {
                        Text(
                            text = state.nextSchoolDay.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                        )
                    }
                }
                if (state.nextSchoolDay != null) NextDayPreparation(
                    nextSchoolDay = state.nextSchoolDay,
                    currentProfile = state.currentProfile,
                    onOpenHomework = onOpenHomework
                )
            }
        }
    }

    Menu(
        isVisible = state.menuOpened,
        isSyncing = state.isSyncRunning,
        profiles = state.profiles,
        hasUnreadNews = state.hasUnreadNews,
        selectedProfile = state.currentProfile,
        onCloseMenu = { onOpenMenu(false) },
        onProfileClicked = onSwitchProfile,
        onManageProfilesClicked = onManageProfiles,
        onProfileLongClicked = onManageProfile,
        onNewsClicked = onOpenNews,
        onSettingsClicked = onOpenSettings,
        onPrivacyPolicyClicked = onPrivacyPolicyClicked,
        onRepositoryClicked = onRepositoryClicked,
        onRefreshClicked = onRefreshClicked
    )
}

@OptIn(PreviewFunction::class)
@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val school = SchoolPreview.generateRandomSchools(1).first()
    val group = GroupPreview.generateGroup(school)
    val profile = ProfilePreview.generateClassProfile(
        group,
        VppIdPreview.generateVppId(group).toActiveVppId()
    )
    HomeScreenContent(
        navBar = navBar,
        state = HomeState(
            currentProfile = profile,
            menuOpened = false,
            hasUnreadNews = true,
            profiles = listOf(profile),
            hasMissingVppIdToProfileLinks = true,
            lastSync = ZonedDateTime.now().minusDays(1L)
        ),
        onBookRoomClicked = {},
        onOpenMenu = {},
        onSwitchProfile = {},
    )
}

@Composable
fun Collapsable(modifier: Modifier = Modifier, expand: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        modifier = modifier,
        visible = expand,
        enter = expandVertically(tween(250)),
        exit = shrinkVertically(tween(250))
    ) {
        content()
    }
}

@Composable
private fun NextDayPreparation(
    nextSchoolDay: SchoolDay,
    currentProfile: Profile?,
    onOpenHomework: (homeworkId: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(Modifier.padding(horizontal = 12.dp)) {
            runComposable title@{
                val start = nextSchoolDay.actualLessons().minOfOrNull { it.start }?.toLocalTime()
                val end = nextSchoolDay.actualLessons().maxOfOrNull { it.end }?.toLocalTime()
                Title(nextSchoolDay.date, start, end)
            }
            Info(info = nextSchoolDay.info)
            runComposable subjects@{
                val subjects = nextSchoolDay.lessons
                    .map { it.displaySubject }
                    .filter { it != "-" }
                    .toSet()
                    .associateWith { subject ->
                        val homework =
                            nextSchoolDay.homework.filter { it.homework.defaultLesson?.subject == subject }
                        (homework.count { it.allDone() } to homework.size)
                    }
                Subjects(subjects)
            }
            if (nextSchoolDay.lessons.isEmpty()) NoData()
            HomeworkSection(
                homework = nextSchoolDay.homework,
                onOpenHomework = onOpenHomework,
                currentProfile = currentProfile
            )
        }
    }
}