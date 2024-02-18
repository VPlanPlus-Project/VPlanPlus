package es.jvbabi.vplanplus.feature.logs.data.repository

import es.jvbabi.vplanplus.domain.model.LogRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLogRecordRepository : LogRecordRepository {
    private val logs = mutableListOf<LogRecord>()

    override suspend fun log(tag: String, message: String) {
        logs.add(LogRecord(message = message, tag = tag, timestamp = System.currentTimeMillis()/1000, id = logs.size.toLong()))
    }

    override suspend fun getLogs(): Flow<List<LogRecord>> {
        return flow { emit(logs) }
    }

    override suspend fun deleteAll() {
        logs.clear()
    }
}