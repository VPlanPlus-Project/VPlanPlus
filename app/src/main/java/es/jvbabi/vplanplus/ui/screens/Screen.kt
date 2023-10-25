package es.jvbabi.vplanplus.ui.screens

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")

    object Onboarding: Screen("onboarding")
    object OnboardingWelcomeScreen: Screen("onboarding/welcome_screen")
    object OnboardingSchoolIdScreen: Screen("onboarding/school_id_screen")
    object OnboardingLoginScreen: Screen("onboarding/login_screen")
    object OnboardingFirstProfileScreen: Screen("onboarding/first_profile_screen")
    object OnboardingClassListScreen: Screen("onboarding/class_list_screen")
    object OnboardingSetupScreen: Screen("onboarding/setup_screen")
}
