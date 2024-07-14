package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import kotlinx.coroutines.flow.flow

class IsBalloonUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    operator fun invoke(balloon: Balloon, default: Boolean) = flow {
        keyValueRepository.getFlowOrDefault("BALLOON_" + balloon.name, default.toString()).collect {
            emit(it.toBoolean())
        }
    }
}

data class Balloon(
    val name: String,
)

val HOMEWORK_DOCUMENT_BALLOON = Balloon("homework_document_balloon")
val HOMEWORK_VPPID_BALLOON = Balloon("homework_vppid_balloon")
val HOMEWORK_HIDDEN_WHERE_TO_FIND_BALLOON = Balloon("homework_hidden_where_to_find_balloon")
val HOMEWORK_SWIPE_DEMO_BALLOON = Balloon("homework_swipe_demo_balloon")