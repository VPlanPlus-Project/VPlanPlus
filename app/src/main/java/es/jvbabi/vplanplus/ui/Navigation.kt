package es.jvbabi.vplanplus.ui

import android.content.Intent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.google.gson.Gson
import es.jvbabi.vplanplus.feature.exams.ui.details.ExamDetailsScreen
import es.jvbabi.vplanplus.feature.logs.ui.LogsScreen
import es.jvbabi.vplanplus.feature.main_calendar.home.ui.CalendarScreen
import es.jvbabi.vplanplus.feature.main_grades.view.ui.calculator.GradeCalculatorScreen
import es.jvbabi.vplanplus.feature.main_grades.view.ui.calculator.GradeCollection
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.GradesScreen
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.SearchView
import es.jvbabi.vplanplus.feature.main_home.ui.HomeScreen
import es.jvbabi.vplanplus.feature.main_homework.list.ui.HomeworkListScreen
import es.jvbabi.vplanplus.feature.main_homework.view.ui.HomeworkDetailScreen
import es.jvbabi.vplanplus.feature.news.ui.NewsScreen
import es.jvbabi.vplanplus.feature.news.ui.detail.NewsDetailScreen
import es.jvbabi.vplanplus.feature.onboarding.stages.a_welcome.ui.OnboardingWelcomeScreen
import es.jvbabi.vplanplus.feature.onboarding.stages.b0_schoolid.ui.OnboardingSchoolIdScreen
import es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.ui.OnboardingQrScreen
import es.jvbabi.vplanplus.feature.onboarding.stages.c_credentials.ui.OnboardingLoginScreen
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.ui.OnboardingAddProfileScreen
import es.jvbabi.vplanplus.feature.onboarding.stages.e_profile.ui.OnboardingProfileSelectScreen
import es.jvbabi.vplanplus.feature.onboarding.stages.f_defaultlessons.ui.OnboardingDefaultLessonScreen
import es.jvbabi.vplanplus.feature.onboarding.stages.g_permissions.ui.OnboardingPermissionScreen
import es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.ui.OnboardingSetupScreen
import es.jvbabi.vplanplus.feature.room_search.ui.RoomSearch
import es.jvbabi.vplanplus.feature.settings.about.ui.AboutScreen
import es.jvbabi.vplanplus.feature.settings.advanced.ui.AdvancedSettingsScreen
import es.jvbabi.vplanplus.feature.settings.general.ui.GeneralSettingsScreen
import es.jvbabi.vplanplus.feature.settings.notifications.NotificationSettingsScreen
import es.jvbabi.vplanplus.feature.settings.profile.ui.ProfileManagementScreen
import es.jvbabi.vplanplus.feature.settings.profile.ui.ProfileManagementTask
import es.jvbabi.vplanplus.feature.settings.profile.ui.UpdateCredentialsTask
import es.jvbabi.vplanplus.feature.settings.profile.ui.settings.ProfileSettingsDefaultLessonScreen
import es.jvbabi.vplanplus.feature.settings.profile.ui.settings.ProfileSettingsScreen
import es.jvbabi.vplanplus.feature.settings.support.ui.SupportScreen
import es.jvbabi.vplanplus.feature.settings.ui.SettingsScreen
import es.jvbabi.vplanplus.feature.settings.vpp_id.manage.VppIdManagementScreen
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.AccountSettingsScreen
import es.jvbabi.vplanplus.ui.common.Transition.enterSlideTransition
import es.jvbabi.vplanplus.ui.common.Transition.enterSlideTransitionLeft
import es.jvbabi.vplanplus.ui.common.Transition.enterSlideTransitionRight
import es.jvbabi.vplanplus.ui.common.Transition.exitSlideTransition
import es.jvbabi.vplanplus.ui.common.Transition.exitSlideTransitionRight
import es.jvbabi.vplanplus.ui.common.Transition.slideInFromBottom
import es.jvbabi.vplanplus.ui.common.Transition.slideOutFromBottom
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.id_link.VppIdLinkScreen
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun NavigationGraph(
    navController: NavHostController,
    goToOnboarding: Boolean,
    navBar: @Composable (expanded: Boolean) -> Unit,
    navRail: @Composable (expanded: Boolean, fab: @Composable () -> Unit) -> Unit,
    onNavigationChanged: (String?) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (goToOnboarding) Screen.Onboarding.route else Screen.HomeScreen.route
    ) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            onNavigationChanged(destination.route)
        }

        deepLinks(navController)
        onboarding(navController)
        mainScreens(navController, navBar, navRail)
        newsScreens(navController)
        settingsScreens(navController)
        gradesScreens(navController)

        examScreens(navController)

        composable(route = Screen.SearchAvailableRoomScreen.route) {
            RoomSearch(navHostController = navController)
        }
    }
}

private fun NavGraphBuilder.examScreens(navController: NavHostController) {
    composable<Screen.ExamDetailsScreen> { backStackEntry ->
        val args = backStackEntry.toRoute<Screen.ExamDetailsScreen>()
        ExamDetailsScreen(
            navHostController = navController,
            examId = args.examId
        )
    }
}


private fun NavGraphBuilder.deepLinks(navController: NavHostController) {
    composable(
        route = Screen.AccountAddedScreen.route + "/{token}",
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://vppid-development.test.jvbabi.es/android/link_success/{token}"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "https://vplan.plus/android/link_success/{token}"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "vplanplus://android/link_success/{token}"
                action = Intent.ACTION_VIEW
            }
        ),
        arguments = listOf(
            navArgument("token") {
                type = NavType.StringType
            }
        ),
        content = {
            VppIdLinkScreen(
                navHostController = navController,
                token = it.arguments?.getString("token")
            )
        }
    )
}

private fun NavGraphBuilder.onboarding(
    navController: NavHostController
) {
    navigation(
        route = Screen.Onboarding.route,
        startDestination = Screen.OnboardingWelcomeScreen.route
    ) {

        composable(
            route = Screen.OnboardingWelcomeScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingWelcomeScreen(navController)
        }

        composable(
            route = Screen.OnboardingQrScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingQrScreen(navController)
        }

        composable(
            route = Screen.OnboardingSchoolIdScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingSchoolIdScreen(navController)
        }

        composable(
            route = Screen.OnboardingLoginScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingLoginScreen(navController)
        }

        composable<Screen.OnboardingNewProfileScreen>(
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            val args = it.toRoute<Screen.OnboardingNewProfileScreen>()
            OnboardingAddProfileScreen(navController, schoolId = if (args.schoolId == -1) null else args.schoolId)
        }

        composable(
            route = Screen.OnboardingProfileSelectScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingProfileSelectScreen(navController)
        }

        composable(
            route = Screen.OnboardingDefaultLessonScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingDefaultLessonScreen(navController)
        }

        composable(
            route = Screen.OnboardingPermissionsScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingPermissionScreen(navController)
        }

        composable(
            route = Screen.OnboardingSetupScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingSetupScreen(navController)
        }
    }
}

private fun NavGraphBuilder.mainScreens(
    navController: NavHostController,
    navBar: @Composable (expanded: Boolean) -> Unit,
    navRail: @Composable (expanded: Boolean, fab: @Composable () -> Unit) -> Unit
) {
    composable(
        route = Screen.HomeScreen.route,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        HomeScreen(
            navHostController = navController,
            navBar = navBar
        )
    }

    composable(
        route = Screen.SearchScreen.route,
        enterTransition = slideInFromBottom,
        exitTransition = slideOutFromBottom,
        popEnterTransition = slideInFromBottom,
        popExitTransition = slideOutFromBottom
    ) {
        SearchView(navHostController = navController)
    }

    composable<Screen.CalendarScreen>(
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) { navStackEntry ->
        val args = navStackEntry.toRoute<Screen.CalendarScreen>()
        CalendarScreen(
            navHostController = navController,
            startDate = LocalDate.parse(args.dateString),
            navBar = navBar,
            navRail = navRail
        )
    }

    composable(
        route = Screen.HomeworkScreen.route,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        HomeworkListScreen(
            navHostController = navController,
            navBar = navBar
        )
    }

    composable<Screen.HomeworkDetailScreen> { backStackEntry ->
        val args = backStackEntry.toRoute<Screen.HomeworkDetailScreen>()
        HomeworkDetailScreen(navHostController = navController, homeworkId = args.homeworkId)
    }

    composable(
        Screen.GradesScreen.route,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        GradesScreen(navHostController = navController, navBar = navBar)
    }
}

private fun NavGraphBuilder.newsScreens(navController: NavHostController) {
    composable(route = Screen.NewsScreen.route) {
        NewsScreen(navController)
    }

    composable(route = Screen.NewsDetailScreen.route + "/{messageId}",
        arguments = listOf(
            navArgument("messageId") {
                type = NavType.StringType
            }
        )
    ) {
        NewsDetailScreen(navController, it.arguments?.getString("messageId")!!)
    }
}

private fun NavGraphBuilder.settingsScreens(
    navController: NavHostController
) {
    composable(
        route = Screen.SettingsScreen.route,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        SettingsScreen(navController)
    }

    composable(
        route = Screen.SettingsVppIdScreen.route,
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight
    ) {
        AccountSettingsScreen(navHostController = navController)
    }

    composable(
        route = Screen.SettingsVppIdManageScreen.route + "/{vppIdId}",
        arguments = listOf(
            navArgument("vppIdId") {
                type = NavType.IntType
            }
        ),
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight
    ) {
        VppIdManagementScreen(
            navHostController = navController,
            vppId = it.arguments?.getInt("vppIdId")!!
        )
    }

    composable(
        route = Screen.SettingsProfileScreen.route + "/{profileId}",
        arguments = listOf(
            navArgument("profileId") {
                type = NavType.StringType
            }
        ),
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight
    ) {
        ProfileSettingsScreen(
            navController = navController,
            profileId = UUID.fromString(it.arguments?.getString("profileId")!!)
        )
    }

    composable(
        route = Screen.SettingsProfileDefaultLessonsScreen.route,
        arguments = listOf(
            navArgument("profileId") {
                type = NavType.StringType
            }
        ),
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        ProfileSettingsDefaultLessonScreen(
            profileId = UUID.fromString(it.arguments?.getString("profileId")!!),
            navController = navController
        )
    }

    composable(
        route = Screen.SettingsProfileScreen.route + "?task={task}&schoolId={schoolId}",
        arguments = listOf(
            navArgument("task") {
                type = NavType.StringType
                nullable = true
                defaultValue = "display"
            },
            navArgument("schoolId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        ),
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        val taskName = it.arguments?.getString("task") ?: "display"
        val task: ProfileManagementTask? = when (taskName) {
            UpdateCredentialsTask.NAME -> /*UpdateCredentialsTask(it.arguments?.getString("schoolId")!!.toLong())*/ TODO()
            else -> null
        }

        ProfileManagementScreen(
            navController = navController,
            onNewProfileClicked = {
//                onboardingViewModel.reset()
//                onboardingViewModel.setTask(Task.CREATE_PROFILE)
//                onboardingViewModel.setOnboardingCause(OnboardingCause.NEW_PROFILE)
//                onboardingViewModel.onAutomaticSchoolIdInput(it.schoolId)
            },
            onNewSchoolClicked = {
//                onboardingViewModel.reset()
//                onboardingViewModel.setOnboardingCause(OnboardingCause.NEW_PROFILE)
//                onboardingViewModel.setTask(Task.CREATE_SCHOOL)
            },
            task = task
        )
    }

    composable(
        route = Screen.SettingsAdvancedScreen.route,
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight
    ) {
        AdvancedSettingsScreen(navHostController = navController)
    }

    composable(
        route = Screen.SettingsAdvancedLogScreen.route,
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight
    ) {
        LogsScreen(navController)
    }

    composable(
        route = Screen.SettingsGeneralSettingsScreen.route,
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight
    ) {
        GeneralSettingsScreen(navController)
    }

    composable<Screen.SettingsNotificationsScreen>(
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight
    ) {
        NotificationSettingsScreen(navController)
    }

    composable(
        route = Screen.SettingsHelpFeedbackScreen.route,
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight
    ) {
        SupportScreen(navController)
    }

    composable(
        route = Screen.SettingsAboutScreen.route,
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight
    ) {
        AboutScreen(navController)
    }
}

@OptIn(ExperimentalEncodingApi::class)
private fun NavGraphBuilder.gradesScreens(navController: NavHostController) {
    composable(
        route = Screen.GradesCalculatorScreen.route + "/?grades={grades}&isSek2={isSek2}",
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight,
        arguments = listOf(
            navArgument("grades") {
                type = NavType.StringType
            },
            navArgument("isSek2") {
                type = NavType.BoolType
            }
        ),
    ) {
        val decodedString = String(Base64.decode(it.arguments?.getString("grades")!!))
        val grades = Gson().fromJson(decodedString, Array<GradeCollection>::class.java)
        GradeCalculatorScreen(navHostController = navController, grades = grades.toList(), isSek2 = it.arguments?.getBoolean("isSek2")?: false)
    }
}

@Serializable
data class NotificationDestination(
    @SerialName("profile_id") val profileId: String? = null,
    @SerialName("screen") val screen: String,
    @SerialName("payload") val payload: String? = null
)