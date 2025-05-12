package es.jvbabi.vplanplus

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.animation.doOnEnd
import androidx.core.net.toUri
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
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
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
import es.jvbabi.vplanplus.feature.migration.usecase.GenerateMigrationTextUseCase
import es.jvbabi.vplanplus.feature.settings.general.domain.data.AppThemeMode
import es.jvbabi.vplanplus.ui.NavigationGraph
import es.jvbabi.vplanplus.ui.NotificationDestination
import es.jvbabi.vplanplus.ui.common.CrashAnalyticsDialog
import es.jvbabi.vplanplus.ui.common.InfoCard
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter
import es.jvbabi.vplanplus.ui.common.Spacer8Dp
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
import java.time.LocalDate
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

    @Inject
    lateinit var generateMigrationTextUseCase: GenerateMigrationTextUseCase

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

    @OptIn(ExperimentalFoundationApi::class)
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

            var hasBuiltMigrationText = false
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
                if (!hasBuiltMigrationText) generateMigrationTextUseCase()
                initDone = true
                hasBuiltMigrationText = true
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
                } catch (_: NullPointerException) {
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
                val scope = rememberCoroutineScope()

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

                    var showNewAppPage by rememberSaveable { mutableStateOf(false) }

                    val navBar = @Composable { expanded: Boolean ->
                        AnimatedVisibility(
                            visible = expanded,
                            enter = expandVertically(tween(250)),
                            exit = shrinkVertically(tween(250))
                        ) {
                            Column {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ) {
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
                                goToOnboarding = goToOnboarding,
                                navBar = navBar,
                                onNewAppClicked = remember { { showNewAppPage = true } },
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

                        AnimatedVisibility(
                            visible = showNewAppPage,
                            enter = fadeIn(tween(250)),
                            exit = fadeOut(tween(250))
                        ) {
                            BackHandler(enabled = showNewAppPage) { showNewAppPage = false }

                            var migrationText by rememberSaveable { mutableStateOf<String?>(null) }
                            LaunchedEffect(Unit) {
                                migrationText = generateMigrationTextUseCase()
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(top = WindowInsets.safeContent.asPaddingValues().calculateTopPadding())
                                        .padding(horizontal = 8.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = "Wechsle jetzt zur neuen VPlanPlus-App",
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                    Text("und profitiere von vielen neuen Funktionen, z.B. ein Kalender, eine deutlich höhere Zuverlässigkeit, Leistungserhebungen und vielem mehr.")
                                    Spacer8Dp()
                                    Spacer8Dp()

                                    repeat(3) { step ->
                                        Spacer(Modifier.size(8.dp))
                                        Row(Modifier.padding(horizontal = 8.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .border(
                                                        1.dp,
                                                        MaterialTheme.colorScheme.outline,
                                                        RoundedCornerShape(16.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${step+1}.",
                                                    color = MaterialTheme.colorScheme.outline,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                            Spacer8Dp()
                                            Spacer8Dp()
                                            Column {
                                                Text(
                                                    text = when (step) {
                                                        0 -> "Lade die neue App herunter"
                                                        1 -> "Öffne die neue App"
                                                        2 -> "Lösche die alte App"
                                                        else -> ""
                                                    },
                                                    style = MaterialTheme.typography.headlineSmall
                                                )
                                                when (step) {
                                                    0 -> {
                                                        Text("Öffne den Google PlayStore und suche nach 'VPlanPlus: Digitaler Schultag'")
                                                        Spacer8Dp()
                                                        RowVerticalCenter {
                                                            Image(
                                                                painter = painterResource(R.drawable.app_new),
                                                                contentDescription = null,
                                                                modifier = Modifier
                                                                    .size(48.dp)
                                                                    .clip(RoundedCornerShape(8.dp))
                                                            )
                                                            Spacer8Dp()
                                                            Column {
                                                                Text(
                                                                    text = "VPlanPlus: Digitaler Schultag",
                                                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                                                )
                                                                Text(
                                                                    text = "Familie Babies",
                                                                    style = MaterialTheme.typography.labelLarge,
                                                                    color = MaterialTheme.colorScheme.secondary
                                                                )
                                                            }
                                                        }
                                                        Spacer8Dp()
                                                        Button(onClick = {
                                                            val intent = Intent(Intent.ACTION_VIEW)
                                                            val referrer = "VPP_Legacy"
                                                            val id = "plus.vplan.app"
                                                            val callerId = BuildConfig.APPLICATION_ID
                                                            intent.setPackage("com.android.vending")
                                                            val deepLinkUrl = "https://play.google.com/d?id=$id&referrer=$referrer"
                                                            intent.data = deepLinkUrl.toUri()
                                                            intent.putExtra("overlay", true)
                                                            intent.putExtra("callerId", callerId)
                                                            val packageManager = context.packageManager
                                                            if (intent.resolveActivity(packageManager) != null) {
                                                                startActivityForResult(intent, 0)
                                                            } else {
                                                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                                                    data = "https://play.google.com/store/apps/details?id=$id".toUri()
                                                                    setPackage("com.android.vending")
                                                                }

                                                                startActivity(intent)
                                                            }
                                                        }) {
                                                            RowVerticalCenter {
                                                                Icon(
                                                                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                                Spacer8Dp()
                                                                Text("Im Google PlayStore fortfahren")
                                                            }
                                                        }
                                                    }
                                                    1 -> {
                                                        Text("Öffne die neue VPlanPlus-App tippe auf 'Aus VPlanPlus importieren'.")
                                                        Spacer8Dp()
                                                        InfoCard(
                                                            imageVector = Icons.Default.Info,
                                                            title = "Wichtig",
                                                            text = "Um deine Daten zu importieren, musst du die neue VPlanPlus-App über die Schaltfläche weiter unten öffnen."
                                                        )
                                                        Spacer8Dp()
                                                        Button(
                                                            onClick = {
                                                                val intent = applicationContext.packageManager.getLaunchIntentForPackage("plus.vplan.app") ?: return@Button
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                intent.putExtra("migration_data", migrationText ?: return@Button)
                                                                startActivity(intent)
                                                            }
                                                        ) {
                                                            RowVerticalCenter {
                                                                Icon(
                                                                    imageVector = Icons.AutoMirrored.Default.OpenInNew,
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                                Spacer8Dp()
                                                                Text("Neue App öffnen")
                                                            }
                                                        }
                                                    }
                                                    2 -> {
                                                        Text("Wenn die neue App eingerichtet ist, kannst du diese App löschen. Vielen Dank, dass du VPlanPlus in seinen frühen Tagen verwendet hast.")
                                                        Spacer8Dp()
                                                        Button(onClick = {
                                                            val intent = Intent(Intent.ACTION_DELETE)
                                                            intent.data = "package:${BuildConfig.APPLICATION_ID}".toUri()
                                                            startActivity(intent)
                                                        }) {
                                                            RowVerticalCenter {
                                                                Icon(
                                                                    imageVector = Icons.Default.Delete,
                                                                    contentDescription = null,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                                Spacer8Dp()
                                                                Text("App löschen")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(Modifier.size(64.dp))
                                }
                                OutlinedButton(
                                    onClick = { showNewAppPage = false },
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .padding(bottom = WindowInsets.safeContent.asPaddingValues().calculateBottomPadding() + 8.dp)
                                        .fillMaxWidth()
                                ) {
                                    RowVerticalCenter {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                            contentDescription = null
                                        )
                                        Spacer8Dp()
                                        Text(text = "Zurück")
                                    }
                                }
                            }
                        }

                        val hasSetCrashlyticsSettings by mainUseCases.hasSetCrashlyticsSettingsUseCase().collectAsState(initial = true)
                        if (!hasSetCrashlyticsSettings) {
                            CrashAnalyticsDialog(
                                onAccept = {
                                    Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
                                    scope.launch { mainUseCases.setCrashlyticsSettingsUseCase() }
                                },
                                onDeny = {
                                    Firebase.crashlytics.setCrashlyticsCollectionEnabled(false)
                                    scope.launch { mainUseCases.setCrashlyticsSettingsUseCase() }
                                }
                            )
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
                            if (destinationData.payload == null) navController!!.navigate(Screen.CalendarScreen(LocalDate.now()))
                            else navController!!.navigate(Json.decodeFromString<Screen.CalendarScreen>(destinationData.payload))
                        }
                        "grades" -> {
                            navController!!.navigate(Screen.GradesScreen.route)
                        }
                        "homework/item" -> {
                            if (destinationData.payload == null) return@launch
                            navController!!.navigate(Json.decodeFromString<Screen.HomeworkDetailScreen>(destinationData.payload))
                        }
                        "settings/profile/notification" -> {
                            navController!!.navigate(Screen.SettingsProfileNotificationsScreen(destinationData.profileId!!))
                        }
                        "exam/item" -> {
                            if (destinationData.payload == null) return@launch
                            navController!!.navigate(Json.decodeFromString<Screen.ExamDetailsScreen>(destinationData.payload))
                        }
                        "home" -> Unit
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

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("vpp-upgrade", text)
    clipboard.setPrimaryClip(clip)
}