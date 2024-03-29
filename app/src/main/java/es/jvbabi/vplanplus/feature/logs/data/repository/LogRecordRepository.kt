package es.jvbabi.vplanplus.feature.logs.data.repository

import es.jvbabi.vplanplus.domain.model.LogRecord
import kotlinx.coroutines.flow.Flow

interface LogRecordRepository {
    suspend fun log(tag: String, message: String)
    suspend fun getLogs(): Flow<List<LogRecord>>
    suspend fun deleteAll()
}