package es.jvbabi.vplanplus.domain.usecase.vpp_id.web_auth

import es.jvbabi.vplanplus.domain.model.vpp_id.WebAuthTask
import es.jvbabi.vplanplus.domain.repository.VppIdRepository

class PickEmojiUseCase(
    private val vppIdRepository: VppIdRepository
) {

    /**
     * @return null if something went wrong, true if successful, false if failed
     */
    suspend operator fun invoke(task: WebAuthTask, emoji: String): Boolean? {
        val result = vppIdRepository.pickEmoji(task, emoji)
        if (!result.code) return null
        return result.value
    }
}