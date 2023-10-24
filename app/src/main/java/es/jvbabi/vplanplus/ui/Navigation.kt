package es.jvbabi.vplanplus.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.home.HomeScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingClassListScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingFirstProfileScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingLoginScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingSchoolIdScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingSetupScreen
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingViewModel
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingWelcomeScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    onboardingViewModel: OnboardingViewModel
) {

    val enterSlideTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) =
        {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        }

    val exitSlideTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) =
        {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(navController)
        }

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
            OnboardingFirstProfileScreen(navController, onboardingViewModel)
        }

        composable(
            route = Screen.OnboardingClassListScreen.route,
            enterTransition = enterSlideTransition,
            exitTransition = exitSlideTransition
        ) {
            OnboardingClassListScreen(navController, onboardingViewModel)
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