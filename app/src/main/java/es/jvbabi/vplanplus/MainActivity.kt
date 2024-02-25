package es.jvbabi.vplanplus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.IntentSanitizer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.usecase.home.Colors
import es.jvbabi.vplanplus.domain.usecase.home.HomeUseCases
import es.jvbabi.vplanplus.feature.onboarding.ui.OnboardingViewModel
import es.jvbabi.vplanplus.ui.NavigationGraph
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.HomeViewModel
import es.jvbabi.vplanplus.ui.theme.VPlanPlusTheme
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var homeUseCases: HomeUseCases

    @Inject
    lateinit var notificationRepository: NotificationRepository

    private lateinit var navController: NavHostController

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        processIntent(intent)

        setContent {
            var colors by remember { mutableStateOf(Colors.DYNAMIC) }
            var init by remember { mutableStateOf(false) }
            var goToOnboarding: Boolean? by remember { mutableStateOf(null) }
            LaunchedEffect(key1 = "init", block = {
                Log.d("MainActivity", "colorscheme: ${homeUseCases.getColorSchemeUseCase()}")
                goToOnboarding = homeUseCases.getCurrentIdentity.invoke().first()?.profile == null
                init = true
                homeUseCases.getColorSchemeUseCase().collect {
                    colors = it
                }
            })
            if (!init) return@setContent
            VPlanPlusTheme(
                cs = colors,
            ) {
                navController = rememberNavController()

                var selectedIndex by rememberSaveable {
                    mutableIntStateOf(0)
                }
                val navBarItems = listOf(
                    NavigationBarItem(
                        onClick = {
                            if (selectedIndex == 0) return@NavigationBarItem
                            selectedIndex = 0
                            navController.navigate(Screen.HomeScreen.route) { popUpTo(0) }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(id = R.string.main_home)) },
                        route = Screen.HomeScreen.route
                    ),
                    NavigationBarItem(
                        onClick = {
                            if (selectedIndex == 1) return@NavigationBarItem
                            selectedIndex = 1
                            navController.navigate(Screen.TimetableScreen.route){ popUpTo(Screen.HomeScreen.route) }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.FormatListNumbered,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(id = R.string.main_timetable)) },
                        route = Screen.TimetableScreen.route
                    ),
                    NavigationBarItem(
                        onClick = {
                            if (selectedIndex == 2) return@NavigationBarItem
                            selectedIndex = 2
                            navController.navigate(Screen.HomeworkScreen.route) { popUpTo(Screen.HomeScreen.route) }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.MenuBook,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(id = R.string.main_homework)) },
                        route = Screen.HomeworkScreen.route
                    ),
                    NavigationBarItem(
                        onClick = {
                            if (selectedIndex == 3) return@NavigationBarItem
                            selectedIndex = 3
                            navController.navigate(Screen.GradesScreen.route) { popUpTo(Screen.HomeScreen.route) }
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Grade,
                                contentDescription = null
                            )
                        },
                        label = { Text(text = stringResource(id = R.string.main_grades)) },
                        route = Screen.GradesScreen.route
                    )
                )

                val navBar = @Composable {
                    NavigationBar {
                        navBarItems.forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = index == selectedIndex,
                                onClick = item.onClick,
                                icon = item.icon,
                                label = item.label
                            )
                        }
                    }
                }
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    if (goToOnboarding != null) {
                        NavigationGraph(
                            navController = navController,
                            onboardingViewModel = onboardingViewModel,
                            homeViewModel = homeViewModel,
                            goToOnboarding = goToOnboarding!!,
                            navBar = navBar,
                            onNavigationChanged = { route ->
                                val item =
                                    navBarItems.firstOrNull { route?.startsWith(it.route) == true }
                                Log.d("Navigation", "Changed to $route")
                                if (item != null) {
                                    selectedIndex = navBarItems.indexOf(item)
                                    Log.d("Navigation", "Selected index: $selectedIndex")
                                }
                            }
                        )
                    }
                }
            }
            LaunchedEffect(key1 = true, block = {
                notificationRepository.createSystemChannels(applicationContext)
                notificationRepository.createProfileChannels(applicationContext, homeUseCases.getProfilesUseCase().first().map { it.value }.flatten())
                homeUseCases.refreshFirebaseToken()
            })
        }

        val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(
            15,
            TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
            .addTag("SyncWork")
            .addTag("AutomaticSyncWork")
            .build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("SyncWork", ExistingPeriodicWorkPolicy.KEEP, syncWork)
    }

    private fun processIntent(intent: Intent) {
        Log.d("MainActivity.Intent", "onNewIntent: $intent")
        Log.d("MainActivity.Intent", "Data: ${intent.data}")
        if (intent.hasExtra("screen")) {
            lifecycleScope.launch {
                while (homeViewModel.state.value.activeProfile == null) delay(50)
                when (intent.getStringExtra("screen")) {
                    "grades" -> navController.navigate(Screen.GradesScreen.route)
                    else -> {}
                }
            }
        }
        if (intent.hasExtra("profileId")) {
            val profileId = intent.getStringExtra("profileId")
            Log.d("MainActivity.Intent", "profileId: $profileId")
            Log.d("MainActivity.Intent", "dateStr: ${intent.getStringExtra("dateStr")}")

            homeViewModel.onProfileSelected(UUID.fromString(profileId))
            if (intent.getStringExtra("dateStr") != null) {
                val dateStr = intent.getStringExtra("dateStr") ?: return
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                Log.d(
                    "MainActivity.Intent",
                    "Switching to date: $date (Difference: ${
                        Period.between(
                            LocalDate.now(),
                            date
                        ).days
                    })"
                )
                lifecycleScope.launch {
                    while (homeViewModel.state.value.activeProfile == null) delay(50)
                    navController.navigate(Screen.TimetableScreen.route + "/$date")
                }
                // homeViewModel.onPageChanged(date) TODO fix this
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.finish()
        if (intent == null) return
        val sanitized = IntentSanitizer.Builder()
            .allowExtra("profileId", String::class.java)
            .allowExtra("dateStr", String::class.java)
            .allowExtra("screen", String::class.java)
            .allowData { true }
            .allowFlags(0x10000000)
            .allowAnyComponent()
            .allowPackage { true }
            .allowAction(Intent.ACTION_VIEW)
            .allowCategory(Intent.CATEGORY_BROWSABLE)
            .build()
            .sanitizeByFiltering(intent)
        startActivity(sanitized)
        processIntent(intent)
    }
}

private data class NavigationBarItem(
    val onClick: () -> Unit,
    val route: String,
    val icon: @Composable () -> Unit,
    val label: @Composable () -> Unit
)