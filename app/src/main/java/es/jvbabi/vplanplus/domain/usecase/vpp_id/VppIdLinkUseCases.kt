package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.GetProfilesWhichCanBeUsedForVppIdUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.SetProfileVppIdUseCase

data class VppIdLinkUseCases(
    val getVppIdDetailsUseCase: GetVppIdDetailsUseCase,
    val getProfilesWhichCanBeUsedForVppIdUseCase: GetProfilesWhichCanBeUsedForVppIdUseCase,
    val setProfileVppIdUseCase: SetProfileVppIdUseCase,
    val updateMissingLinksStateUseCase: UpdateMissingLinksStateUseCase
)