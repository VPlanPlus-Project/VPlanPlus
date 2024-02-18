package es.jvbabi.vplanplus.feature.logs.domain.usecase

import com.google.common.truth.Truth.assertThat
import es.jvbabi.vplanplus.feature.logs.data.repository.FakeLogRecordRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class GetLogsUseCaseTest {

    private lateinit var logsUseCase: GetLogsUseCase

    private lateinit var logsRepository: LogRecordRepository

    private var logCount = 0

    @Before
    fun setUp() {
        logsRepository = FakeLogRecordRepository()
        logsUseCase = GetLogsUseCase(logsRepository)
        for (i in 0..10) {
            for (j in 0..Random.nextInt(5, 10)) {
                runBlocking { logsRepository.log("tag$i", "message$j") }
                logCount++
            }
        }
    }

    @Test
    fun `Get logs`() {
        runBlocking {
            val logs = logsUseCase().first()
            assertThat(logs.size).isEqualTo(logCount)
            logs.forEach {
                assertThat(it.tag).startsWith("tag")
            }

            assertThat(logs.map { it.id }).isInOrder()
        }
    }
}