package es.jvbabi.vplanplus.domain.usecase.general

import android.os.SystemClock.sleep
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class GetCurrentTimeUseCase {
    operator fun invoke() = flow {
        while (true) {
            emit(LocalDateTime.now())
            sleep(100)
        }
    }
}