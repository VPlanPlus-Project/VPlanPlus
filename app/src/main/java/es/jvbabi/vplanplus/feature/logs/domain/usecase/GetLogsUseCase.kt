package es.jvbabi.vplanplus.feature.logs.domain.usecase

import es.jvbabi.vplanplus.domain.model.LogRecord
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import kotlinx.coroutines.flow.Flow

class GetLogsUseCase(
    private val logRecordRepository: LogRecordRepository
) {
    suspend operator fun invoke(): Flow<List<LogRecord>> {
        return logRecordRepository.getLogs()
    }
}