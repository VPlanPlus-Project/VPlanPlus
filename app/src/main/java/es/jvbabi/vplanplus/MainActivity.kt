package es.jvbabi.vplanplus

import android.os.Bundle
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
import es.jvbabi.vplanplus.ui.screens.onboarding.OnboardingViewModel
import es.jvbabi.vplanplus.ui.theme.VPlanPlusTheme
import es.jvbabi.vplanplus.worker.SyncWorker
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val onboardingViewModel: OnboardingViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VPlanPlusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    val navController = rememberNavController()
                    NavigationGraph(
                        navController = navController,
                        onboardingViewModel = onboardingViewModel
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
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("SyncWork", ExistingPeriodicWorkPolicy.KEEP, syncWork)
    }
}
