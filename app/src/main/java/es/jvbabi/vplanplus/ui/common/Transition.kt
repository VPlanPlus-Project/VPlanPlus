package es.jvbabi.vplanplus.ui.common

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

object Transition {
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

    val enterSlideTransitionRight: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) =
        {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300))
        }

    val exitSlideTransitionRight: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) =
        {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        }

    val slideInFromBottom: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) = {
        slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300))
    }

    val slideOutFromBottom: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    }

}