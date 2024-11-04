package es.jvbabi.vplanplus.feature.main_home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DayType
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.DataType
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.model.SchoolDay
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.ExamSection
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.menu.Menu
import es.jvbabi.vplanplus.feature.main_home.ui.components.Head
import es.jvbabi.vplanplus.feature.main_home.ui.components.ImportantHeader
import es.jvbabi.vplanplus.feature.main_home.ui.components.NoData
import es.jvbabi.vplanplus.feature.main_home.ui.components.PagerSwitcher
import es.jvbabi.vplanplus.feature.main_home.ui.components.QuickActions
import es.jvbabi.vplanplus.feature.main_home.ui.components.VersionHintsInformation
import es.jvbabi.vplanplus.feature.main_home.ui.components.banners.BadCredentialsBanner
import es.jvbabi.vplanplus.feature.main_home.ui.components.cards.MissingVppIdLinkToProfileCard
import es.jvbabi.vplanplus.feature.main_home.ui.components.content.ExamList
import es.jvbabi.vplanplus.feature.main_home.ui.components.content.next.HomeworkSection
import es.jvbabi.vplanplus.feature.main_home.ui.components.content.next.Info
import es.jvbabi.vplanplus.feature.main_home.ui.components.content.next.Title
import es.jvbabi.vplanplus.feature.main_home.ui.components.content.today.AssessmentReminderTitle
import es.jvbabi.vplanplus.feature.main_home.ui.components.content.today.CurrentLesson
import es.jvbabi.vplanplus.feature.main_home.ui.components.content.today.CurrentOrNextTitle
import es.jvbabi.vplanplus.feature.main_home.ui.components.content.today.FurtherLessonsTitle
import es.jvbabi.vplanplus.feature.main_home.ui.components.content.today.LessonsForDayBlock
import es.jvbabi.vplanplus.feature.main_home.ui.preview.navBar
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheet
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkSheetInitialValues
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.onLogin
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer16Dp
import es.jvbabi.vplanplus.ui.common.Spacer4Dp
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
import es.jvbabi.vplanplus.ui.common.keyboardAsState
import es.jvbabi.vplanplus.ui.common.openLink
import es.jvbabi.vplanplus.ui.preview.GroupPreview
import es.jvbabi.vplanplus.ui.preview.PreviewFunction
import es.jvbabi.vplanplus.ui.preview.ProfilePreview
import es.jvbabi.vplanplus.ui.preview.ProfilePreview.toActiveVppId
import es.jvbabi.vplanplus.ui.preview.SchoolPreview
import es.jvbabi.vplanplus.ui.preview.VppIdPreview
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.util.runComposable
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
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
        onBookRoomClicked = remember { { navHostController.navigate(Screen.SearchAvailableRoomScreen.route) } },
        onOpenMenu = homeViewModel::onMenuOpenedChange,
        onVersionHintsClosed = homeViewModel::hideVersionHintsDialog,

        onSwitchProfile = homeViewModel::switchProfile,
        onManageProfiles = remember {
            {
                homeViewModel.onMenuOpenedChange(false)
                navHostController.navigate(Screen.SettingsProfileScreen.route)
            }
        },
        onManageProfile = remember {
            {
                homeViewModel.onMenuOpenedChange(false)
                navHostController.navigate("${Screen.SettingsProfileScreen.route}/${it.id}")
            }
        },
        onOpenNews = remember {
            {
                homeViewModel.onMenuOpenedChange(false); navHostController.navigate(
                Screen.NewsScreen.route
            )
            }
        },
        onOpenSettings = remember {
            {
                homeViewModel.onMenuOpenedChange(false); navHostController.navigate(
                Screen.SettingsScreen.route
            )
            }
        },
        onPrivacyPolicyClicked = remember {
            {
                openLink(
                    context,
                    "${state.server.uiHost}/privacy"
                )
            }
        },
        onRepositoryClicked = remember {
            {
                openLink(
                    context,
                    "https://github.com/VPlanPlus-Project/VPlanPlus"
                )
            }
        },
        onOpenSearch = remember { { navHostController.navigate(Screen.SearchScreen.route) } },
        onRefreshClicked = remember {
            {
                homeViewModel.onMenuOpenedChange(false); homeViewModel.onRefreshClicked(
                context
            )
            }
        },
        onFixVppIdSessionClicked = remember { { onLogin(context, state.server) } },
        onFixVppIdLinksClicked = remember { { navHostController.navigate(Screen.SettingsVppIdScreen.route) } },
        onIgnoreInvalidVppIdSessions = homeViewModel::ignoreInvalidVppIdSessions,
        onFixCredentialsClicked = remember { { navHostController.navigate("${Screen.SettingsProfileScreen.route}?task=update_credentials&schoolId=${state.currentProfile?.getSchool()?.id}") } },
        onSendFeedback = remember { { navHostController.navigate(Screen.SettingsHelpFeedbackScreen.route) } },
        onOpenHomework = remember { { homeworkId -> navHostController.navigate(Screen.HomeworkDetailScreen(homeworkId)) } },
        onOpenExam = remember { { examId -> navHostController.navigate(Screen.ExamDetailsScreen(examId)) } },
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
    onOpenExam: (examId: Int) -> Unit = {},

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
                currentTime = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")),
                isSyncing = state.isSyncRunning,
                showNotificationDot = state.hasUnreadNews,
                onProfileClicked = remember { { onOpenMenu(true) } },
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

            val todayHasData = state.today != null && (state.today.dataType != DataType.NO_DATA || state.today.type != DayType.NORMAL)
            val todayIsOver = state.today?.isDayOver() ?: false
            val nextDayHasData = state.nextSchoolDay != null

            Box(Modifier.fillMaxSize()) {
                if ((todayHasData && !nextDayHasData)) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)) {
                        TodayContent(
                            today = state.today ?: return@Column,
                            currentProfile = state.currentProfile,
                            onOpenExam = onOpenExam
                        )
                    }
                } else if (todayHasData) {
                    val pagerState =
                        rememberPagerState(initialPage = if (todayIsOver) 1 else 0) { 2 }
                    val scope = rememberCoroutineScope()
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) { page ->
                        when (page) {
                            0 -> TodayContent(
                                today = state.today ?: return@HorizontalPager,
                                currentProfile = state.currentProfile,
                                onOpenExam = onOpenExam
                            )
                            1 -> NextDayPreparation(
                                nextSchoolDay = state.nextSchoolDay ?: return@HorizontalPager,
                                currentProfile = state.currentProfile,
                                onOpenHomework = onOpenHomework,
                                onOpenExam = onOpenExam
                            )
                        }
                    }
                    PagerSwitcher(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .align(Alignment.BottomCenter),
                        swipeProgress = pagerState.currentPage + pagerState.currentPageOffsetFraction,
                        nextDate = state.nextSchoolDay?.date ?: LocalDate.now(),
                        onSelectPage = { scope.launch { pagerState.animateScrollToPage(it) } }
                    )
                } else if (nextDayHasData) {
                    NextDayPreparation(
                        nextSchoolDay = state.nextSchoolDay ?: return@Column,
                        currentProfile = state.currentProfile,
                        onOpenHomework = onOpenHomework,
                        onOpenExam = onOpenExam
                    )
                } else {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)) {
                        NoData(onRefreshClicked)
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

@Composable
private fun TodayContent(
    today: SchoolDay,
    currentProfile: Profile,
    onOpenExam: (examId: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 56.dp)
    ) {
        val currentOrNextLessons = today.getCurrentOrNextLesson()
        currentOrNextLessons?.let { currentOrNextLesson ->
            CurrentOrNextTitle(
                isCurrent = currentOrNextLesson.isCurrent,
                lessonNumber = currentOrNextLesson.lessons.first().lessonNumber
            )
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
                        today.homework.filter { it.homework.defaultLesson == (lesson as? Lesson.SubstitutionPlanLesson)?.defaultLesson && it.homework.defaultLesson != null },
                        today.exams.filter { it.subject?.vpId == (lesson as? Lesson.SubstitutionPlanLesson)?.defaultLesson?.vpId }
                    )
                }
            }
        }

        val followingLessons = today.lessons
            .filter {
                it.lessonNumber > (currentOrNextLessons?.lessons?.firstOrNull()?.lessonNumber
                    ?: -1)
            }
            .groupBy { it.lessonNumber }

        if (followingLessons.isNotEmpty()) {
            Spacer16Dp()
            FurtherLessonsTitle(followingLessons.filter { it.value.all { l -> l.displaySubject != "-" } }.size)
            Spacer8Dp()
            LessonsForDayBlock(followingLessons = followingLessons)
        }

        if (today.type != DayType.NORMAL) {
            NoLessonsContent(isHoliday = today.type == DayType.HOLIDAY)
        }

        if (today.examsToGetRemindedOf().isNotEmpty()) {
            Spacer16Dp()
            AssessmentReminderTitle()
            Spacer8Dp()
            Column(Modifier.padding(horizontal = 12.dp)) {
                ExamSection(
                    showSection = true,
                    includeTitle = false,
                    date = today.date,
                    currentProfile = currentProfile,
                    onOpenExamScreen = onOpenExam,
                    exams = today.examsToGetRemindedOf()
                )
            }
        }
    }
}

@Composable
private fun NextDayPreparation(
    nextSchoolDay: SchoolDay,
    currentProfile: Profile?,
    onOpenHomework: (homeworkId: Int) -> Unit,
    onOpenExam: (examId: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 52.dp)
    ) {
        Column(
            Modifier.padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            runComposable title@{
                val start = nextSchoolDay.actualLessons().minOfOrNull { it.start }?.toLocalTime()
                val end = nextSchoolDay.actualLessons().maxOfOrNull { it.end }?.toLocalTime()
                Title(nextSchoolDay.date, start, end)
            }
            HomeworkSection(
                homework = nextSchoolDay.homework,
                onOpenHomework = onOpenHomework,
                currentProfile = currentProfile,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Info(info = nextSchoolDay.info)
            ExamList(nextSchoolDay, onOpenExam)
            runComposable subjects@{
                LessonsForDayBlock(
                    modifier = Modifier.padding(vertical = 4.dp),
                    followingLessons = nextSchoolDay.lessons.groupBy { it.lessonNumber },
                    horizontalPadding = false
                )
            }
            if (nextSchoolDay.lessons.isEmpty()) NoData()
        }
        if (nextSchoolDay.type != DayType.NORMAL) {
            NoLessonsContent(isHoliday = nextSchoolDay.type == DayType.HOLIDAY)
        }
    }
}

@Composable
private fun NoLessonsContent(
    isHoliday: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .defaultMinSize(minHeight = 148.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = if (isHoliday) R.drawable.undraw_beach_day else R.drawable.undraw_fun_moments),
            contentDescription = null,
            modifier = Modifier.size(148.dp)
        )
        Spacer16Dp()
        Text(
            text = stringResource(id = if (isHoliday) R.string.calendar_dayTypeHoliday else R.string.calendar_dayTypeWeekend),
            style = MaterialTheme.typography.headlineMedium.copy(
                brush = Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
        )
    }
}

@Composable
private fun NoData(
    onRefresh: () -> Unit
) {
    var isLoading by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .defaultMinSize(minHeight = 148.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.undraw_schedule_re_2vro),
            contentDescription = null,
            modifier = Modifier.size(148.dp)
        )
        Spacer16Dp()
        Text(
            text = stringResource(id = R.string.home_noData),
            style = MaterialTheme.typography.headlineMedium.copy(
                brush = Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(id = R.string.home_noDataText),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer8Dp()

        OutlinedButton(
            onClick = { isLoading = true; onRefresh() },
            enabled = !isLoading
        ) {
            RowVerticalCenter {
                AnimatedVisibility(
                    visible = isLoading,
                    enter = expandHorizontally(),
                    exit = shrinkVertically()
                ) {
                    Row {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                        Spacer4Dp()
                    }
                }
                Text(text = stringResource(id = R.string.home_menuRefresh))
            }
        }
    }
}