package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.source.database.dao.LogRecordDao
import es.jvbabi.vplanplus.domain.model.LogRecord
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import kotlinx.coroutines.flow.Flow

class LogRepositoryImpl(
    private val logRecordDao: LogRecordDao
): LogRecordRepository {
    override suspend fun log(tag: String, message: String) {
        logRecordDao.insert(
            LogRecord(
                timestamp = System.currentTimeMillis(),
                tag = tag,
                message = message
            )
        )
    }

    override suspend fun getLogs(): Flow<List<LogRecord>> {
        return logRecordDao.getAll()
    }

    override suspend fun deleteAll() {
        logRecordDao.deleteAll()
    }
}