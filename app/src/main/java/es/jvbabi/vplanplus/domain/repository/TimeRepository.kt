package es.jvbabi.vplanplus.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface TimeRepository {
    fun getTime(): Flow<ZonedDateTime>
}