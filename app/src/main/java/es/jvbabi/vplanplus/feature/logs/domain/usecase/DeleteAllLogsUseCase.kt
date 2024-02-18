package es.jvbabi.vplanplus.feature.logs.domain.usecase

import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository

class DeleteAllLogsUseCase(
    private val logRecordRepository: LogRecordRepository
) {
    suspend operator fun invoke() {
        logRecordRepository.deleteAll()
    }
}