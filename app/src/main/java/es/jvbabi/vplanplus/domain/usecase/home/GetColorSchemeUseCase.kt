package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import kotlinx.coroutines.flow.flow

class GetColorSchemeUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke() = flow {
        keyValueRepository.getFlow(Keys.COLOR).collect {
            if (it == null) emit(Colors.DYNAMIC) else emit(Colors.entries[it.toInt()])
        }
    }
}

enum class Colors {
    DYNAMIC,
    AMBER,
    BLUE,
    BLUE_GREY,
    BROWN,
    CYAN,
    DEEP_ORANGE,
    DEEP_PURPLE,
    GREEN,
    GREY,
    INDIGO,
    LIGHT_BLUE,
    LIGHT_GREEN,
    LIME,
    ORANGE,
    PINK,
    PURPLE,
    RED,
    TEAL,
    YELLOW,
}