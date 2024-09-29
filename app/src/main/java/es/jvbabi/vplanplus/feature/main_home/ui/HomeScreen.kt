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
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.NextWeek
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
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
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.homework.HomeworkSection
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
import es.jvbabi.vplanplus.feature.main_home.ui.preview.navBar
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheet
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheetInitialValues
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.onLogin
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.Grid
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.SubjectIcon
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
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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
                if (state.nextSchoolDay != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) title@{
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.NextWeek,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .size(20.dp)
                            )
                            Column {
                                Text(
                                    text = run {
                                        val localizedRelativeDate = DateUtils.localizedRelativeDate(LocalContext.current, state.nextSchoolDay.date)
                                        if (localizedRelativeDate == null) stringResource(R.string.home_nextDayTitle)
                                        else stringResource(R.string.home_nextDayTitleWithDate, localizedRelativeDate)
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                )
                                Text(
                                    text = buildString {
                                        append(state.nextSchoolDay.date.format(DateTimeFormatter.ofPattern("EEE, dd. MMM yyyy")))
                                        val start = state.nextSchoolDay.actualLessons().minOfOrNull { it.start }
                                        val end = state.nextSchoolDay.actualLessons().maxOfOrNull { it.end }
                                        if (start == null || end == null) return@buildString
                                        append(" $DOT ")
                                        append(state.nextSchoolDay.lessons.minOf { it.start }.format(DateTimeFormatter.ofPattern("HH:mm")))
                                        append(" - ")
                                        append(state.nextSchoolDay.lessons.maxOf { it.end }.format(DateTimeFormatter.ofPattern("HH:mm")))
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (!state.nextSchoolDay.info.isNullOrBlank()) {
                            Spacer8Dp()
                            RowVerticalCenter(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = state.nextSchoolDay.info,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (state.nextSchoolDay.lessons.isNotEmpty()) {
                            Spacer8Dp()
                            Text(
                                text = stringResource(R.string.home_subjects),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .fillMaxWidth()
                            )
                            Spacer4Dp()
                            val subjects = state.nextSchoolDay.lessons
                                .map { it.displaySubject }
                                .filter { it != "-" }
                                .toSet()
                            Grid(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                columns = 2,
                                padding = 2.dp,
                                content = List(subjects.size) {
                                    @Composable { _, _, index ->
                                        RowVerticalCenter(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                                .fillMaxSize()
                                                .padding(4.dp),
                                        ) {
                                            SubjectIcon(
                                                subject = subjects.elementAt(index),
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .size(24.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Column {
                                                val homeworkForSubject = state.nextSchoolDay.homework.filter { it.homework.defaultLesson?.subject == subjects.elementAt(index) }
                                                Text(
                                                    text = subjects.elementAt(index),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (homeworkForSubject.isNotEmpty()) {
                                                    Text(
                                                        text = stringResource(R.string.home_subejctsHomework, homeworkForSubject.count { it.allDone() }, homeworkForSubject.size),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 1
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        else NoData()


                        if (state.nextSchoolDay.homework.isNotEmpty()) {
                            Spacer8Dp()
                            Text(
                                text = stringResource(R.string.home_homeworkTitle),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .fillMaxWidth()
                            )
                            Spacer4Dp()
                            Box(Modifier.padding(horizontal = 4.dp)) {
                                HomeworkSection(
                                    contextDate = LocalDate.now(),
                                    homework = state.nextSchoolDay.homework,
                                    onOpenHomeworkScreen = onOpenHomework,
                                    currentProfile = state.currentProfile,
                                    showSection = true,
                                    includeTitle = false,
                                    includeUntil = true
                                )
                            }
                        }
                    }
                }
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