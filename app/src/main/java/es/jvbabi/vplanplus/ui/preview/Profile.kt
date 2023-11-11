package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType

object Profile {

    fun generateClassProfile(): Profile {
        return Profile(
            id = 1,
            name = "7c",
            customName = "7c",
            type = ProfileType.STUDENT,
            referenceId = 1
        )
    }
}