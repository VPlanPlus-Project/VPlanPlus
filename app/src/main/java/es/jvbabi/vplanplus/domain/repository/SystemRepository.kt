package es.jvbabi.vplanplus.domain.repository

interface SystemRepository {
    fun isAppInForeground(): Boolean
}