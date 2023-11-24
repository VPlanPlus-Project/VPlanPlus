package es.jvbabi.vplanplus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.ui.NavigationGraph
import es.jvbabi.vplanplus.ui.screens.home.HomeViewModel
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingViewModel
import es.jvbabi.vplanplus.ui.theme.VPlanPlusTheme
import es.jvbabi.vplanplus.worker.SyncWorker
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        processIntent(intent)

        setContent {
            VPlanPlusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    val navController = rememberNavController()
                    NavigationGraph(
                        navController = navController,
                        onboardingViewModel = onboardingViewModel,
                        homeViewModel = homeViewModel
                    )
                }
            }
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
            val profileId = intent.getLongExtra("profileId", -1)
            Log.d("MainActivity.Intent", "profileId: $profileId")
            Log.d("MainActivity.Intent", "dateStr: ${intent.getStringExtra("dateStr")}")

            homeViewModel.onProfileSelected(applicationContext, profileId)
            if (intent.getStringExtra("dateStr") != null) {
                val dateStr = intent.getStringExtra("dateStr") ?: return
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                Log.d("MainActivity.Intent", "Switching to date: $date (Difference: ${Period.between(LocalDate.now(), date).days})")
                homeViewModel.changePage(Int.MAX_VALUE/2 + Period.between(LocalDate.now(), date).days)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        processIntent(intent ?: return)
    }
}
