package es.jvbabi.vplanplus.domain.usecase.logs

import es.jvbabi.vplanplus.domain.repository.LogRecordRepository

class DeleteAllLogsUseCase(
    private val logRecordRepository: LogRecordRepository
) {
    suspend operator fun invoke() {
        logRecordRepository.deleteAll()
    }
}