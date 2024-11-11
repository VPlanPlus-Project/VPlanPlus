package es.jvbabi.vplanplus.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.domain.usecase.daily.UpdateDailyNotificationAlarmsUseCase
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var updateDailyNotificationAlarmsUseCase: UpdateDailyNotificationAlarmsUseCase
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            Log.e("BootReceiver", "Intent or context is null")
            return
        }
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "Intent is not ACTION_BOOT_COMPLETED")
            return
        }

        Log.i("BootReceiver", "BootReceiver called")
        runBlocking {
            updateDailyNotificationAlarmsUseCase()
        }
    }
}