package es.jvbabi.vplanplus.domain.repository

import kotlinx.coroutines.flow.Flow

interface SystemRepository {
    fun isAppInForeground(): Boolean
    fun closeApp()
    fun canSendNotifications(): Flow<Boolean>
}