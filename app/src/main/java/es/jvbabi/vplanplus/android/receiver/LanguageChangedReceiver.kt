package es.jvbabi.vplanplus.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.android.notification.Notification
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LanguageChangedReceiver : BroadcastReceiver() {
    @Inject lateinit var profileRepository: ProfileRepository
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        GlobalScope.launch {
            Notification.createChannels(context, profileRepository.getProfiles().first())
        }
    }
}