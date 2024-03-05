package es.jvbabi.vplanplus.feature.grades.domain.usecase

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository

class RequestBiometricUseCase(
    private val biometricRepository: BiometricRepository,
    private val stringRepository: StringRepository
) {
    operator fun invoke(
        fragmentActivity: FragmentActivity,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (errorCode: Int, errorString: String) -> Unit,
        onFail: () -> Unit
    ) {
        biometricRepository.promptUser(
            title = stringRepository.getString(R.string.authenticate),
            subtitle = stringRepository.getString(R.string.grades_authenticateSubtitle),
            cancelString = stringRepository.getString(android.R.string.cancel),
            activity = fragmentActivity,
            onSuccess = onSuccess,
            onError = onError,
            onFailed = onFail
        )
    }
}