package es.jvbabi.vplanplus.ui.screens

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")

    object OnboardingWelcomeScreen: Screen("onboarding/welcome_screen")
    object OnboardingSchoolIdScreen: Screen("onboarding/school_id_screen")
}
