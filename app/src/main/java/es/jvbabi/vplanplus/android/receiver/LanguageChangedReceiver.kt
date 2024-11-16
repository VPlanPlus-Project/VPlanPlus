package es.jvbabi.vplanplus.android.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LanguageChangedReceiver : BroadcastReceiver() {
    @Inject lateinit var profileRepository: ProfileRepository
    @Inject lateinit var notificationRepository: NotificationRepository

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        GlobalScope.launch {
            notificationRepository.createSystemChannels(context)
            notificationRepository.createProfileChannels(context, profileRepository.getProfiles().firstOrNull().orEmpty())
        }
    }
}