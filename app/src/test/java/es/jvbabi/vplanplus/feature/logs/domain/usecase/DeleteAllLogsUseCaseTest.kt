package es.jvbabi.vplanplus.feature.logs.domain.usecase

import com.google.common.truth.Truth.assertThat
import es.jvbabi.vplanplus.feature.logs.data.repository.FakeLogRecordRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class DeleteAllLogsUseCaseTest {

    private lateinit var deleteAllLogsUseCase: DeleteAllLogsUseCase
    private lateinit var logRecordRepository: LogRecordRepository

    @Before
    fun setUp() {
        logRecordRepository = FakeLogRecordRepository()
        deleteAllLogsUseCase = DeleteAllLogsUseCase(logRecordRepository)

        for (i in 0..10) {
            for (j in 0..Random.nextInt(5, 10)) {
                runBlocking { logRecordRepository.log("tag$i", "message$j") }
            }
        }
    }

    @Test
    fun `Delete all logs`() {
        runBlocking {
            assertThat(logRecordRepository.getLogs().first().size).isGreaterThan(0)
            deleteAllLogsUseCase()
            val logs = logRecordRepository.getLogs().first()
            assertThat(logs.size).isEqualTo(0)
        }
    }
}