package es.jvbabi.vplanplus.domain.usecase.home

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.Keys

class GetColorSchemeUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(): ColorScheme {
        val id = keyValueRepository.get(Keys.COLOR) ?: return ColorScheme.DYNAMIC
        return ColorScheme.entries[id.toInt()]
    }
}

enum class ColorScheme {
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
    DYNAMIC


}