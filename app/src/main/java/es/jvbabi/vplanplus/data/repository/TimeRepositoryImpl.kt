package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.domain.repository.TimeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.time.ZonedDateTime

class TimeRepositoryImpl : TimeRepository {
    override fun getTime() = flow {
        var time = ZonedDateTime.now()
        while (true) {
            val current = ZonedDateTime.now()
            if (current != time) {
                emit(current)
                time = current
            }
            delay(200)
        }
    }
}