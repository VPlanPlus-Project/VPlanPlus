package es.jvbabi.vplanplus.ui.screens

sealed class Screen(val route: String) {
    data object HomeScreen: Screen("home_screen")
    data object SearchScreen: Screen("home_screen/search")
    data object SearchAvailableRoomScreen: Screen("home_screen/available_room")

    data object Onboarding: Screen("onboarding")
    data object OnboardingWelcomeScreen: Screen("onboarding/welcome_screen")
    data object OnboardingQrScreen: Screen("onboarding/qr_screen")
    data object OnboardingSchoolIdScreen: Screen("onboarding/school_id_screen")
    data object OnboardingLoginScreen: Screen("onboarding/login_screen")
    data object OnboardingNewProfileScreen: Screen("onboarding/first_profile_screen") // used for creating another profile
    data object OnboardingProfileSelectScreen: Screen("onboarding/profile_options_list_screen")
    data object OnboardingDefaultLessonScreen: Screen("onboarding/default_lesson_screen")
    data object OnboardingPermissionsScreen: Screen("onboarding/permissions_screen")
    data object OnboardingSetupScreen: Screen("onboarding/setup_screen")

    data object AccountAddedScreen: Screen("account_added_screen")

    data object HomeworkScreen: Screen("homework_screen")
    data object HomeworkDetailScreen : Screen("homework_detail_screen")

    data object GradesScreen: Screen("grades")
    data object GradesCalculatorScreen: Screen("grades/calculator")

    // SETTINGS
    data object SettingsScreen: Screen("settings/main")
    data object SettingsProfileScreen: Screen("settings/profile")

    data object SettingsProfileDefaultLessonsScreen: Screen("settings/profile/{profileId}/default_lessons")
    data object SettingsGeneralSettingsScreen: Screen("settings/general_settings")

    data object SettingsVppIdScreen: Screen("settings/vpp_id")
    data object SettingsVppIdManageScreen: Screen("settings/vpp_id/manage")

    data object SettingsAdvancedScreen: Screen("settings/advanced")
    data object SettingsAdvancedLogScreen: Screen("settings/advanced/logs")

    data object SettingsHomeworkScreen: Screen("settings/homework")

    data object SettingsHelpFeedbackScreen: Screen("settings/help_feedback")
    data object SettingsAboutScreen: Screen("settings/about")

    data object NewsScreen: Screen("news_screen")

    data object NewsDetailScreen: Screen("news_screen")
}
