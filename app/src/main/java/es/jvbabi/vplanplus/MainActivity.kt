package es.jvbabi.vplanplus

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.vpp_id.WebAuthTask
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.home.Colors
import es.jvbabi.vplanplus.domain.usecase.home.MainUseCases
import es.jvbabi.vplanplus.domain.usecase.vpp_id.web_auth.OPEN_TASK_NOTIFICATION_TAG
import es.jvbabi.vplanplus.domain.usecase.vpp_id.web_auth.OpenTaskNotificationOnClickTaskPayload
import es.jvbabi.vplanplus.feature.settings.general.domain.data.AppThemeMode
import es.jvbabi.vplanplus.ui.NavigationGraph
import es.jvbabi.vplanplus.ui.NotificationDestination
import es.jvbabi.vplanplus.ui.screens.Screen
import es.jvbabi.vplanplus.ui.screens.overlay.vpp_web_auth.VppIdAuthWrapper
import es.jvbabi.vplanplus.ui.theme.VPlanPlusTheme
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var mainUseCases: MainUseCases

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var vppIdRepository: VppIdRepository

    private var navController: NavHostController? = null
    private var showSplashScreen: Boolean = true

    private var currentProfile by mutableStateOf<Profile?>(null)
    private var colorScheme = mutableStateOf(Colors.DYNAMIC)
    private var appTheme = mutableStateOf(AppThemeMode.SYSTEM)

    private var authTask by mutableStateOf<WebAuthTask?>(null)

    companion object {
        var isAppInDarkMode = mutableStateOf(true)
    }

    private var initDone = false

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this

        processIntent(intent)

        var goToOnboarding: Boolean? = null
        lifecycleScope.launch {
            Log.d("MainActivity.Setup", "Loading Identity")
            currentProfile = mainUseCases.getCurrentIdentity.invoke().first()
            goToOnboarding = currentProfile == null

            Log.i("MainActivity.Setup", "Creating or updating notification channels")
            if (goToOnboarding == true) notificationRepository.deleteAllChannels()
            notificationRepository.createSystemChannels(applicationContext)
            notificationRepository.createProfileChannels(
                applicationContext,
                mainUseCases.getProfilesUseCase().first().map { it.value }.flatten()
            )

            Log.i("MainActivity.Setup", "Run preparation")
            mainUseCases.setUpUseCase()

            combine(
                listOf(
                    mainUseCases.getColorSchemeUseCase(),
                    mainUseCases.getCurrentIdentity(),
                    mainUseCases.getAppThemeUseCase()
                )
            ) { data ->
                colorScheme.value = data[0] as Colors
                currentProfile = data[1] as Profile?
                appTheme.value = AppThemeMode.valueOf(data[2] as String)
            }.collect {
                initDone = true
            }
        }

        if (showSplashScreen) installSplashScreen().apply {
            setKeepOnScreenCondition {
                //Log.d("MainActivity", "Showing splash screen")
                !initDone
            }
            setOnExitAnimationListener { screen ->
                Log.d("MainActivity", "Exiting splash screen")
                try {
                    val moveIconAnimator = ObjectAnimator.ofFloat(
                        screen.iconView,
                        View.TRANSLATION_X,
                        screen.iconView.paddingStart.toFloat(),
                        0f
                    )
                    val fadeScreenAnimator = ObjectAnimator.ofFloat(
                        screen.view,
                        View.ALPHA,
                        1f,
                        0f
                    )

                    fadeScreenAnimator.interpolator = AccelerateInterpolator()
                    fadeScreenAnimator.duration = 500L
                    moveIconAnimator.interpolator = AccelerateInterpolator()
                    moveIconAnimator.duration = 500L
                    moveIconAnimator.start()
                    fadeScreenAnimator.start()
                    moveIconAnimator.doOnEnd { screen.remove() }
                } catch (e: NullPointerException) {
                    screen.remove()
                }

                doInit(true)
            }
        } else doInit(false)

        lifecycleScope.launch {
            vppIdRepository.getCurrentAuthTask().collect {
                authTask = it
            }
        }

        lifecycleScope.launch {
            while (!initDone) delay(50)
            setContent {
                isAppInDarkMode.value = appTheme.value == AppThemeMode.DARK || (appTheme.value == AppThemeMode.SYSTEM && isSystemInDarkTheme())
                VPlanPlusTheme(cs = colorScheme.value, darkTheme = isAppInDarkMode.value) {
                    LaunchedEffect(key1 = Unit) {
                        enableEdgeToEdge()
                    }
                    navController = rememberNavController()

                    var selectedIndex by rememberSaveable {
                        mutableIntStateOf(0)
                    }
                    val navBarItems = listOfNotNull(
                        NavigationBarItem(
                            onClick = {
                                if (selectedIndex == 0) return@NavigationBarItem
                                selectedIndex = 0
                                navController!!.navigate(Screen.HomeScreen.route) { popUpTo(0) }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = stringResource(id = R.string.main_home)) },
                            screen = Screen.HomeScreen
                        ),
                        NavigationBarItem(
                            onClick = {
                                if (selectedIndex == 1) return@NavigationBarItem
                                selectedIndex = 1
                                navController!!.navigate(Screen.CalendarScreen()) { popUpTo(Screen.HomeScreen.route) }
                            },
                            icon = {
                                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)
                            },
                            label = { Text(text = stringResource(id = R.string.main_calendar)) },
                            screen = Screen.CalendarScreen()
                        ),
                        if (currentProfile is ClassProfile) NavigationBarItem(
                            onClick = {
                                if (selectedIndex == 2) return@NavigationBarItem
                                selectedIndex = 2
                                navController!!.navigate(Screen.HomeworkScreen.route) { popUpTo(Screen.HomeScreen.route) }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.MenuBook,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = stringResource(id = R.string.main_homework)) },
                            screen = Screen.HomeworkScreen
                        ) else null,
                        if (currentProfile is ClassProfile) NavigationBarItem(
                            onClick = {
                                if (selectedIndex == 3) return@NavigationBarItem
                                selectedIndex = 3
                                navController!!.navigate(Screen.GradesScreen.route) { popUpTo(Screen.HomeScreen.route) }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Grade,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = stringResource(id = R.string.main_grades)) },
                            screen = Screen.GradesScreen
                        ) else null
                    )

                    val navBar = @Composable { expanded: Boolean ->
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(tween(250)),
                            exit = shrinkVertically(tween(250))
                        ) {
                            navBarItems.BottomBar(selectedIndex)
                        }
                    }

                    val navRail = @Composable { expanded: Boolean, fab: @Composable () -> Unit ->
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(tween(250)),
                            exit = shrinkVertically(tween(250))
                        ) {
                            navBarItems.RailBar(selectedIndex, fab)
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding(),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        if (goToOnboarding != null && navController != null) {
                            NavigationGraph(
                                navController = navController!!,
                                goToOnboarding = goToOnboarding!!,
                                navBar = navBar,
                                navRail = navRail,
                                onNavigationChanged = { route ->
                                    val item =
                                        navBarItems.firstOrNull { route?.startsWith(it.screen.route) == true || route?.startsWith(it.screen::class.qualifiedName?:"-") == true }
                                    if (item != null && navBarItems.indexOf(item) != selectedIndex) {
                                        selectedIndex = navBarItems.indexOf(item)
                                        Log.d("Navigation", "Selected index: $selectedIndex")
                                    }
                                }
                            )
                        }
                        if (authTask != null) {
                            Box(modifier = Modifier.zIndex(500f)) {
                                VppIdAuthWrapper(task = authTask, onFinished = { authTask = null })
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            val intervalMinutes = mainUseCases.getSyncIntervalMinutesUseCase()
            if (intervalMinutes == -1L) return@launch

            val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(
                intervalMinutes,
                TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
                .addTag("SyncWork")
                .addTag("AutomaticSyncWork")
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("SyncWork", ExistingPeriodicWorkPolicy.KEEP, syncWork)
        }
    }

    private fun processIntent(intent: Intent) {
        Log.d("MainActivity.Intent", "onNewIntent: $intent")
        Log.d("MainActivity.Intent", "Data: ${intent.data}")
        if (intent.hasExtra("screen")) {
            showSplashScreen = false
            lifecycleScope.launch {
                while (currentProfile == null || navController == null) delay(50)
                val destination = intent.getStringExtra("screen") ?: Screen.HomeScreen.route
                if (destination.startsWith("{")) {
                    val destinationData = Json.decodeFromString<NotificationDestination>(destination)
                    if (destinationData.profileId != null) {
                        mainUseCases.setCurrentProfileUseCase(UUID.fromString(destinationData.profileId))
                    }
                    when (destinationData.screen) {
                        "calendar" -> {
                            if (destinationData.payload == null) navController!!.navigate(Screen.CalendarScreen)
                            else navController!!.navigate(Json.decodeFromString<Screen.CalendarScreen>(destinationData.payload))
                        }
                        "grades" -> {
                            navController!!.navigate(Screen.GradesScreen.route)
                        }
                        "homework/item" -> {
                            if (destinationData.payload == null) return@launch
                            navController!!.navigate(Json.decodeFromString<Screen.HomeworkDetailScreen>(destinationData.payload))
                        }
                    }
                } else when (destination) {
                    "grades" -> navController!!.navigate(Screen.GradesScreen.route)
                    "homework" -> navController!!.navigate(Screen.HomeworkScreen.route)
                    else -> {
                        Log.d("MainActivity.Intent", "Navigating to $destination")
                        navController!!.navigate(destination)
                    }
                }
            }
        }
        if (intent.hasExtra("tag")) {
            val tag = intent.getStringExtra("tag")
            when (tag) {
                OPEN_TASK_NOTIFICATION_TAG -> {
                    val payload = intent.getStringExtra("payload") ?: return
                    val task =
                        Gson().fromJson(payload, OpenTaskNotificationOnClickTaskPayload::class.java)
                    runBlocking {
                        val vppId = vppIdRepository.getActiveVppIds().first()
                            .firstOrNull { it.id == task.accountId } ?: return@runBlocking
                        authTask = WebAuthTask(
                            taskId = task.taskId,
                            emojis = task.emojis,
                            validUntil = ZonedDateTimeConverter().timestampToZonedDateTime(task.validUntil),
                            vppId = vppId
                        )
                    }
                }
            }
        }
    }

    private fun doInit(calledBySplashScreen: Boolean) {
        if (!calledBySplashScreen) setTheme(R.style.Theme_VPlanPlus)
        enableEdgeToEdge()
    }
}

@Composable
fun List<NavigationBarItem>.BottomBar(selectedIndex: Int) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = item.onClick,
                icon = item.icon,
                label = item.label
            )
        }
    }
}

@Composable
fun List<NavigationBarItem>.RailBar(selectedIndex: Int, fab: @Composable () -> Unit) {
    NavigationRail(
        header = { fab() }
    ) {
        forEachIndexed { index, item ->
            NavigationRailItem(
                selected = index == selectedIndex,
                onClick = item.onClick,
                icon = item.icon,
                label = item.label
            )
        }
    }
}

data class NavigationBarItem(
    val onClick: () -> Unit,
    val screen: Screen,
    val icon: @Composable () -> Unit,
    val label: @Composable () -> Unit
)