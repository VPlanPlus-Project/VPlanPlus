package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.repository.TimeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class TimeRepositoryImpl : TimeRepository {
    override fun getTime() = flow {
        var time = LocalDateTime.now()
        while (true) {
            val current = LocalDateTime.now()
            if (current != time) {
                emit(current)
                time = current
            }
            delay(200)
        }
    }
}