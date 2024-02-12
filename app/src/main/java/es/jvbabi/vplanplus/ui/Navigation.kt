package es.jvbabi.vplanplus.ui

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import es.jvbabi.vplanplus.ui.screens.timetable.TimetableScreen
import es.jvbabi.vplanplus.ui.common.Transition.enterSlideTransition
import es.jvbabi.vplanplus.ui.common.Transition.enterSlideTransitionRight
import es.jvbabi.vplanplus.ui.common.Transition.exitSlideTransition
import es.jvbabi.vplanplus.ui.common.Transition.exitSlideTransitionRight
import es.jvbabi.vplanplus.ui.screens.id_link.VppIdLinkScreen
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.home.HomeScreen
import es.jvbabi.vplanplus.ui.screens.home.search.room.FindAvailableRoomScreen
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.HomeViewModel
import es.jvbabi.vplanplus.ui.screens.settings.advanced.logs.LogsScreen
import es.jvbabi.vplanplus.ui.screens.news.NewsScreen
import es.jvbabi.vplanplus.ui.screens.news.detail.NewsDetailScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingAddProfileScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingCause
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingDefaultLessonScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingLoginScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingPermissionScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingProfileOptionListScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingSchoolIdScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingSetupScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingViewModel
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingWelcomeScreen
import es.jvbabi.vplanplus.feature.onboarding.ui.Task
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingQrScreen
import es.jvbabi.vplanplus.ui.screens.settings.SettingsScreen
import es.jvbabi.vplanplus.ui.screens.settings.account.AccountSettingsScreen
import es.jvbabi.vplanplus.ui.screens.settings.advanced.AdvancedSettingsScreen
import es.jvbabi.vplanplus.ui.screens.settings.general.GeneralSettingsScreen
import es.jvbabi.vplanplus.ui.screens.settings.profile.ProfileManagementScreen
import es.jvbabi.vplanplus.ui.screens.settings.profile.settings.ProfileSettingsDefaultLessonScreen
import es.jvbabi.vplanplus.ui.screens.settings.profile.settings.ProfileSettingsScreen
import java.time.LocalDate
import java.util.UUID

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onboardingViewModel: OnboardingViewModel,
    homeViewModel: HomeViewModel,
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

        composable(
            route = Screen.AccountAddedScreen.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://id.vpp.jvbabi.es/link_success/{token}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument("token") {
                    type = NavType.StringType
                }
            ),
            content = {
                VppIdLinkScreen(navHostController = navController, token = it.arguments?.getString("token"))
            }
        )

        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                navHostController = navController,
                viewModel = homeViewModel,
                navBar = navBar
            )
        }

        composable(route = Screen.TimetableScreen.route) {
            TimetableScreen(
                navHostController = navController,
                navBar = navBar
            )
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

        composable(route = Screen.SearchAvailableRoomScreen.route) {
            FindAvailableRoomScreen(navController)
        }

        composable(route = Screen.SettingsScreen.route) {
            SettingsScreen(navController)
        }

        composable(route = Screen.SettingsVppIdScreen.route) {
            AccountSettingsScreen(navHostController = navController)
        }

        composable(
            route = Screen.SettingsProfileScreen.route + "{profileId}",
            arguments = listOf(
                navArgument("profileId") {
                    type = NavType.StringType
                }
            )
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
            )
        ) {
            ProfileSettingsDefaultLessonScreen(
                profileId = UUID.fromString(it.arguments?.getString("profileId")!!),
                navController = navController
            )
        }

        composable(route = Screen.SettingsProfileScreen.route) {
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

        composable(route = Screen.SettingsAdvancedScreen.route) {
            AdvancedSettingsScreen(navHostController = navController)
        }

        composable(route = Screen.SettingsAdvancedLogScreen.route) {
            LogsScreen(navController)
        }

        composable(route = Screen.SettingsGeneralSettingsScreen.route) {
            GeneralSettingsScreen(navController)
        }

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
                OnboardingWelcomeScreen(navController, onboardingViewModel)
            }

            composable(
                route = Screen.OnboardingQrScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition,
                popEnterTransition = enterSlideTransitionRight,
                popExitTransition = exitSlideTransitionRight
            ) {
                OnboardingQrScreen(navController, onboardingViewModel)
            }

            composable(
                route = Screen.OnboardingSchoolIdScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition,
                popEnterTransition = enterSlideTransitionRight,
                popExitTransition = exitSlideTransitionRight
            ) {
                OnboardingSchoolIdScreen(navController, onboardingViewModel)
            }

            composable(
                route = Screen.OnboardingLoginScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition,
                popEnterTransition = enterSlideTransitionRight,
                popExitTransition = exitSlideTransitionRight
            ) {
                OnboardingLoginScreen(navController, onboardingViewModel)
            }

            composable(
                route = Screen.OnboardingFirstProfileScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition,
                popEnterTransition = enterSlideTransitionRight,
                popExitTransition = exitSlideTransitionRight
            ) {
                OnboardingAddProfileScreen(navController, onboardingViewModel)
            }

            composable(
                route = Screen.OnboardingNewProfileScreen.route + "/{schoolId}",
                arguments = listOf(
                    navArgument("schoolId") {
                        type = NavType.LongType
                    }
                ),
            ) {
                OnboardingAddProfileScreen(navController, onboardingViewModel)
            }

            composable(
                route = Screen.OnboardingProfileSelectScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition,
                popEnterTransition = enterSlideTransitionRight,
                popExitTransition = exitSlideTransitionRight
            ) {
                OnboardingProfileOptionListScreen(navController, onboardingViewModel)
            }

            composable(
                route = Screen.OnboardingDefaultLessonScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition,
                popEnterTransition = enterSlideTransitionRight,
                popExitTransition = exitSlideTransitionRight
            ) {
                OnboardingDefaultLessonScreen(navController, onboardingViewModel)
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
                    viewModel = onboardingViewModel,
                )
            }

            composable(
                route = Screen.OnboardingSetupScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition,
                popEnterTransition = enterSlideTransitionRight,
                popExitTransition = exitSlideTransitionRight
            ) {
                OnboardingSetupScreen(navController, onboardingViewModel)
            }
        }
    }
}