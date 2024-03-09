package es.jvbabi.vplanplus.domain.repository

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity

interface BiometricRepository {
    fun canAuthenticate(): BiometricStatus
    fun promptUser(
        title: String,
        subtitle: String,
        cancelString: String,
        activity: FragmentActivity,
        onSuccess: (result: BiometricPrompt.AuthenticationResult) -> Unit,
        onFailed: () -> Unit,
        onError: (errorCode: Int, errorString: String) -> Unit
    )

    companion object {
        const val RESULT_CODE_TOO_MANY_ATTEMPTS = 7
        const val RESULT_CODE_CANCELED_BY_USER = 10
        const val RESULT_CODE_CANCELED = 13
    }
}

enum class BiometricStatus {
    AVAILABLE,
    NOT_READY,
    NOT_SUPPORTED,
    NOT_SET_UP
}