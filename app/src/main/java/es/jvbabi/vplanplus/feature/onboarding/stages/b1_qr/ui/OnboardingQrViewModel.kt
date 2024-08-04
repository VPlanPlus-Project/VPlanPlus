package es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.feature.onboarding.stages.b1_qr.domain.usecase.OnboardingQrUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingQrViewModel @Inject constructor(
    private val onboardingQrUseCases: OnboardingQrUseCases
) : ViewModel() {

    var state by mutableStateOf(OnboardingQrState())
    private var testJob: Job? = null

    fun init() { state = OnboardingQrState() }

    fun doAction(action: UiAction) {
        when (action) {
            is InputQrResult -> {
                if (state.qrSchoolState == action.qrSchoolState) return
                testJob?.cancel()
                testJob = viewModelScope.launch {
                    val schoolId = action.qrSchoolState.schoolId!!.toLongOrNull() ?: return@launch
                    state = state.copy(
                        qrResultState = QrResultState.CHECKING,
                        qrSchoolState = action.qrSchoolState
                    )
                    val result = onboardingQrUseCases.testSchoolCredentialsUseCase(schoolId, action.qrSchoolState.username!!, action.qrSchoolState.password!!)
                    if (result == null) {
                        state = state.copy(qrResultState = QrResultState.LOADING_SCHOOL_DATA)
                        state = state.copy(
                            qrResultState = if (onboardingQrUseCases.checkCredentialsAndInitOnboardingForSchoolUseCase(schoolId.toInt(), action.qrSchoolState.username, action.qrSchoolState.password) == null) QrResultState.NETWORK_ERROR else QrResultState.PROCEED
                        )
                    } else state = state.copy(qrResultState = result)
                }
            }
            is OnInvalidQrScanned -> {
                state = state.copy(
                    qrSchoolState = null,
                    qrResultState = QrResultState.INVALID_QR
                )
            }
        }
    }
}

data class OnboardingQrState(
    val qrSchoolState: QrSchoolState? = null,
    val qrResultState: QrResultState? = null
)

data class QrSchoolState(
    @SerializedName("school_id") val schoolId: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QrSchoolState

        if (schoolId != other.schoolId) return false
        if (username != other.username) return false
        if (password != other.password) return false

        return true
    }

    override fun hashCode(): Int {
        var result = schoolId.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + password.hashCode()
        return result
    }
}

enum class QrResultState {
    SCHOOL_NOT_FOUND,
    INVALID_QR,
    NETWORK_ERROR,
    CHECKING,
    LOADING_SCHOOL_DATA,
    PROCEED
}

sealed class UiAction
data class InputQrResult(val qrSchoolState: QrSchoolState) : UiAction()
data object OnInvalidQrScanned : UiAction()