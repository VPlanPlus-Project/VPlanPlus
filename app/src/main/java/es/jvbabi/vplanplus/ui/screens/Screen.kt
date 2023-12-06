package es.jvbabi.vplanplus.ui.screens

sealed class Screen(val route: String) {
    data object HomeScreen: Screen("home_screen")
    data object SearchAvailableRoomScreen: Screen("home_screen/available_room")

    data object Onboarding: Screen("onboarding")
    data object OnboardingWelcomeScreen: Screen("onboarding/welcome_screen")
    data object OnboardingSchoolIdScreen: Screen("onboarding/school_id_screen")
    data object OnboardingLoginScreen: Screen("onboarding/login_screen")
    data object OnboardingNewProfileScreen: Screen("onboarding/first_profile_screen") // used for creating another profile
    data object OnboardingFirstProfileScreen: Screen("onboarding/first_profile_screen") // used for creating the first profile
    data object OnboardingProfileSelectScreen: Screen("onboarding/profile_options_list_screen")
    data object OnboardingDefaultLessonScreen: Screen("onboarding/default_lesson_screen")
    data object OnboardingPermissionsScreen: Screen("onboarding/permissions_screen")
    data object OnboardingSetupScreen: Screen("onboarding/setup_screen")

    data object SettingsScreen: Screen("settings/main")
    data object SettingsProfileScreen: Screen("settings/profile")
    data object SettingsProfileDefaultLessonsScreen: Screen("settings/profile/{profileId}/default_lessons")
    data object SettingsGeneralSettingsScreen: Screen("settings/general_settings")

    data object LogsScreen: Screen("logs_screen")
}
