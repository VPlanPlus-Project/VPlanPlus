package es.jvbabi.vplanplus.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface TimeRepository {
    fun getTime(): Flow<LocalDateTime>
}