package es.jvbabi.vplanplus.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import es.jvbabi.vplanplus.ui.common.Transition.enterSlideTransition
import es.jvbabi.vplanplus.ui.common.Transition.exitSlideTransition
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.home.HomeScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingAddProfileScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingProfileOptionListScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingLoginScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingSchoolIdScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingSetupScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingViewModel
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingWelcomeScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.Task
import es.jvbabi.vplanplus.ui.screens.settings.SettingsScreen
import es.jvbabi.vplanplus.ui.screens.settings.profile.ProfileManagementScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onboardingViewModel: OnboardingViewModel
) {

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {

        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController)
        }

        composable(route = Screen.SettingsScreen.route) {
            SettingsScreen(navController)
        }

        composable(route = Screen.SettingsProfileScreen.route) {
            ProfileManagementScreen(
                navController = navController,
                onNewProfileClicked = {
                    onboardingViewModel.reset()
                    onboardingViewModel.setTask(Task.CREATE_PROFILE)
                    onboardingViewModel.onAutomaticSchoolIdInput(it.id!!)
                },
                onNewSchoolClicked = {
                    onboardingViewModel.reset()
                    onboardingViewModel.setTask(Task.CREATE_SCHOOL)
                }
            )
        }

        navigation(
            route = Screen.Onboarding.route,
            startDestination = Screen.OnboardingWelcomeScreen.route
        ) {

            composable(
                route = Screen.OnboardingWelcomeScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition
            ) {
                OnboardingWelcomeScreen(navController)
            }

            composable(
                route = Screen.OnboardingSchoolIdScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition
            ) {
                OnboardingSchoolIdScreen(navController, onboardingViewModel)
            }

            composable(
                route = Screen.OnboardingLoginScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition
            ) {
                OnboardingLoginScreen(navController, onboardingViewModel)
            }

            composable(
                route = Screen.OnboardingFirstProfileScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition
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
                exitTransition = exitSlideTransition
            ) {
                OnboardingProfileOptionListScreen(navController, onboardingViewModel)
            }

            composable(
                route = Screen.OnboardingSetupScreen.route,
                enterTransition = enterSlideTransition,
                exitTransition = exitSlideTransition
            ) {
                OnboardingSetupScreen(navController, onboardingViewModel)
            }
        }
    }
}