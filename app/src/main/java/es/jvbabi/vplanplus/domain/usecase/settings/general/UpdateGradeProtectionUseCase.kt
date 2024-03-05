package es.jvbabi.vplanplus.domain.usecase.settings.general

import androidx.fragment.app.FragmentActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.StringRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpdateGradeProtectionUseCase(
    private val keyValueRepository: KeyValueRepository,
    private val biometricRepository: BiometricRepository,
    private val stringRepository: StringRepository
) {
    @OptIn(DelicateCoroutinesApi::class)
    suspend operator fun invoke(to: Boolean, activity: FragmentActivity) {
        if (to) {
            keyValueRepository.set(Keys.GRADES_BIOMETRIC_ENABLED, "true")
            return
        }
        biometricRepository.promptUser(
            title = stringRepository.getString(R.string.authenticate),
            subtitle = stringRepository.getString(R.string.settings_generalAuthenticateSubtitle),
            cancelString = stringRepository.getString(android.R.string.cancel),
            onSuccess = {
                GlobalScope.launch {
                    keyValueRepository.set(Keys.GRADES_BIOMETRIC_ENABLED, "false")
                }
            },
            onError = { _, _ -> },
            onFailed = { },
            activity = activity
        )
    }
}