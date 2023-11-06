package es.jvbabi.vplanplus.ui.screens

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")

    object Onboarding: Screen("onboarding")
    object OnboardingWelcomeScreen: Screen("onboarding/welcome_screen")
    object OnboardingSchoolIdScreen: Screen("onboarding/school_id_screen")
    object OnboardingLoginScreen: Screen("onboarding/login_screen")
    object OnboardingNewProfileScreen: Screen("onboarding/first_profile_screen") // used for creating another profile
    object OnboardingFirstProfileScreen: Screen("onboarding/first_profile_screen") // used for creating the first profile
    object OnboardingClassListScreen: Screen("onboarding/class_list_screen")
    object OnboardingSetupScreen: Screen("onboarding/setup_screen")

    object SettingsScreen: Screen("settings/main")
    object SettingsProfileScreen: Screen("settings/profile")
}
