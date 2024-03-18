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
import com.google.gson.Gson
import es.jvbabi.vplanplus.feature.main_grades.ui.calculator.GradeCalculatorScreen
import es.jvbabi.vplanplus.feature.main_grades.ui.calculator.GradeCollection
import es.jvbabi.vplanplus.feature.main_grades.ui.view.GradesScreen
import es.jvbabi.vplanplus.feature.main_home.ui.HomeScreen
import es.jvbabi.vplanplus.feature.main_homework.add.ui.AddHomeworkScreen
import es.jvbabi.vplanplus.feature.main_homework.view.ui.HomeworkScreen
import es.jvbabi.vplanplus.feature.logs.ui.LogsScreen
import es.jvbabi.vplanplus.feature.news.ui.NewsScreen
import es.jvbabi.vplanplus.feature.news.ui.detail.NewsDetailScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingAddProfileScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingCause
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingDefaultLessonScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingLoginScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingPermissionScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingProfileOptionListScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingQrScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingSchoolIdScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingSetupScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingViewModel
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingWelcomeScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.Task
import es.jvbabi.vplanplus.feature.settings.about.ui.AboutScreen
import es.jvbabi.vplanplus.feature.settings.homework.ui.HomeworkSettingsScreen
import es.jvbabi.vplanplus.feature.settings.support.ui.SupportScreen
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.AccountSettingsScreen
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.manage.VppIdManagementScreen
import es.jvbabi.vplanplus.ui.common.Transition.enterSlideTransition
import es.jvbabi.vplanplus.ui.common.Transition.enterSlideTransitionLeft
import es.jvbabi.vplanplus.ui.common.Transition.enterSlideTransitionRight
import es.jvbabi.vplanplus.ui.common.Transition.exitSlideTransition
import es.jvbabi.vplanplus.ui.common.Transition.exitSlideTransitionRight
import es.jvbabi.vplanplus.ui.common.Transition.slideInFromBottom
import es.jvbabi.vplanplus.ui.common.Transition.slideOutFromBottom
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.home.search.room.FindAvailableRoomScreen
import es.jvbabi.vplanplus.ui.screens.id_link.VppIdLinkScreen
import es.jvbabi.vplanplus.feature.settings.ui.SettingsScreen
import es.jvbabi.vplanplus.ui.screens.settings.advanced.AdvancedSettingsScreen
import es.jvbabi.vplanplus.ui.screens.settings.general.GeneralSettingsScreen
import es.jvbabi.vplanplus.ui.screens.settings.profile.ProfileManagementScreen
import es.jvbabi.vplanplus.ui.screens.settings.profile.settings.ProfileSettingsDefaultLessonScreen
import es.jvbabi.vplanplus.ui.screens.settings.profile.settings.ProfileSettingsScreen
import es.jvbabi.vplanplus.feature.main_timetable.ui.TimetableScreen
import java.time.LocalDate
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onboardingViewModel: OnboardingViewModel,
    goToOnboarding: Boolean,
    navBar: @Composable () -> Unit,
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
        onboarding(navController, onboardingViewModel)
        mainScreens(navController, navBar)
        newsScreens(navController)
        settingsScreens(navController, onboardingViewModel)
        gradesScreens(navController)

        composable(route = Screen.SearchAvailableRoomScreen.route) {
            FindAvailableRoomScreen(navController)
        }
    }
}


private fun NavGraphBuilder.deepLinks(navController: NavHostController) {
    composable(
        route = Screen.AccountAddedScreen.route + "/{token}",
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "https://id.vpp.jvbabi.es/android/link_success/{token}"
                action = Intent.ACTION_VIEW
            },
            navDeepLink {
                uriPattern = "https://vppid-development.test.jvbabi.es/android/link_success/{token}"
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
    navController: NavHostController,
    viewModel: OnboardingViewModel
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
            OnboardingWelcomeScreen(navController, viewModel)
        }

        composable(
            route = Screen.OnboardingQrScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingQrScreen(navController, viewModel)
        }

        composable(
            route = Screen.OnboardingSchoolIdScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingSchoolIdScreen(navController, viewModel)
        }

        composable(
            route = Screen.OnboardingLoginScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingLoginScreen(navController, viewModel)
        }

        composable(
            route = Screen.OnboardingFirstProfileScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingAddProfileScreen(navController, viewModel)
        }

        composable(
            route = Screen.OnboardingNewProfileScreen.route + "/{schoolId}",
            arguments = listOf(
                navArgument("schoolId") {
                    type = NavType.LongType
                }
            ),
        ) {
            OnboardingAddProfileScreen(navController, viewModel)
        }

        composable(
            route = Screen.OnboardingProfileSelectScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingProfileOptionListScreen(navController, viewModel)
        }

        composable(
            route = Screen.OnboardingDefaultLessonScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingDefaultLessonScreen(navController, viewModel)
        }

        composable(
            route = Screen.OnboardingPermissionsScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingPermissionScreen(
                navController = navController,
                viewModel = viewModel,
            )
        }

        composable(
            route = Screen.OnboardingSetupScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition,
            popEnterTransition = enterSlideTransitionRight,
            popExitTransition = exitSlideTransitionRight
        ) {
            OnboardingSetupScreen(navController, viewModel)
        }
    }
}

private fun NavGraphBuilder.mainScreens(
    navController: NavHostController,
    navBar: @Composable () -> Unit
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
        route = Screen.AddHomeworkScreen.route + "?vpId={vpId}",
        enterTransition = slideInFromBottom,
        exitTransition = slideOutFromBottom,
        popEnterTransition = slideInFromBottom,
        popExitTransition = slideOutFromBottom,
        arguments = listOf(
            navArgument("vpId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) {
        AddHomeworkScreen(navHostController = navController, vpId = it.arguments?.getString("vpId")?.toLongOrNull())
    }

    composable(
        route = Screen.TimetableScreen.route,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        TimetableScreen(
            navHostController = navController,
            navBar = navBar
        )
    }

    composable(
        route = Screen.HomeworkScreen.route,
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        HomeworkScreen(
            navHostController = navController,
            navBar = navBar
        )
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

    composable(route = Screen.TimetableScreen.route + "/{startDate}",
        arguments = listOf(
            navArgument("startDate") {
                type = NavType.StringType
            }
        )
    ) {
        TimetableScreen(
            navHostController = navController,
            startDate = it.arguments?.getString("startDate")?.let { date ->
                LocalDate.parse(date)
            } ?: LocalDate.now(),
            navBar = navBar
        )
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
    navController: NavHostController,
    onboardingViewModel: OnboardingViewModel
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
        route = Screen.SettingsProfileScreen.route + "{profileId}",
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
        route = Screen.SettingsProfileScreen.route,
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = { fadeOut(tween(300)) }
    ) {
        ProfileManagementScreen(
            navController = navController,
            onNewProfileClicked = {
                onboardingViewModel.reset()
                onboardingViewModel.setTask(Task.CREATE_PROFILE)
                onboardingViewModel.setOnboardingCause(OnboardingCause.NEW_PROFILE)
                onboardingViewModel.onAutomaticSchoolIdInput(it.schoolId)
            },
            onNewSchoolClicked = {
                onboardingViewModel.reset()
                onboardingViewModel.setOnboardingCause(OnboardingCause.NEW_PROFILE)
                onboardingViewModel.setTask(Task.CREATE_SCHOOL)
            }
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

    composable(
        route = Screen.SettingsHomeworkScreen.route,
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight
    ) {
        HomeworkSettingsScreen(navController)
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
        route = Screen.GradesCalculatorScreen.route + "/{grades}",
        enterTransition = enterSlideTransitionLeft,
        exitTransition = { fadeOut(tween(300)) },
        popEnterTransition = { fadeIn(tween(300)) },
        popExitTransition = exitSlideTransitionRight,
        arguments = listOf(
            navArgument("grades") {
                type = NavType.StringType
            }
        ),
    ) {
        val decodedString = String(Base64.decode(it.arguments?.getString("grades")!!))
        val grades = Gson().fromJson(decodedString, Array<GradeCollection>::class.java)
        GradeCalculatorScreen(navHostController = navController, grades = grades.toList())
    }
}