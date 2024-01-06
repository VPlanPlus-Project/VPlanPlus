package es.jvbabi.vplanplus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.android.notification.Notification
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.ui.NavigationGraph
import es.jvbabi.vplanplus.ui.screens.home.viewmodel.HomeViewModel
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingViewModel
import es.jvbabi.vplanplus.ui.theme.VPlanPlusTheme
import es.jvbabi.vplanplus.worker.SyncWorker
import kotlinx.coroutines.flow.first
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
    lateinit var profileUseCases: ProfileUseCases

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        processIntent(intent)

        setContent {
            var goToOnboarding: Boolean? by remember { mutableStateOf(null) }
            LaunchedEffect(key1 = "init", block = {
                goToOnboarding = profileUseCases.getActiveProfile() == null
            })
            VPlanPlusTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    val navController = rememberNavController()
                    if (goToOnboarding != null) {
                        NavigationGraph(
                            navController = navController,
                            onboardingViewModel = onboardingViewModel,
                            homeViewModel = homeViewModel,
                            goToOnboarding = goToOnboarding!!
                        )
                    }
                }
            }
            LaunchedEffect(key1 = true, block = {
                Notification.createChannels(applicationContext, profileUseCases.getProfiles().first())
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
        if (intent.hasExtra("profileId")) {
            val profileId = intent.getStringExtra("profileId")
            Log.d("MainActivity.Intent", "profileId: $profileId")
            Log.d("MainActivity.Intent", "dateStr: ${intent.getStringExtra("dateStr")}")

            homeViewModel.onProfileSelected(UUID.fromString(profileId))
            if (intent.getStringExtra("dateStr") != null) {
                val dateStr = intent.getStringExtra("dateStr") ?: return
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                Log.d("MainActivity.Intent", "Switching to date: $date (Difference: ${Period.between(LocalDate.now(), date).days})")
                homeViewModel.onInitPageChanged(date)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        processIntent(intent ?: return)
    }
}
