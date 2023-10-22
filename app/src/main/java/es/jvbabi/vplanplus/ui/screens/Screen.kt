package es.jvbabi.vplanplus.ui.screens

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
}
