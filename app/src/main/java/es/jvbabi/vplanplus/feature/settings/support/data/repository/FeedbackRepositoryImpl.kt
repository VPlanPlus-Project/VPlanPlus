package es.jvbabi.vplanplus.feature.settings.support.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.BuildConfig
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.settings.support.domain.repository.FeedbackRepository
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.BearerAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first

class FeedbackRepositoryImpl(
    private val vppIdRepository: VppIdRepository,
    private val profileRepository: ProfileRepository,
    private val vppIdNetworkRepository: VppIdNetworkRepository
) : FeedbackRepository {
    override suspend fun sendFeedback(
        email: String?,
        feedback: String,
        attachSystemDetails: Boolean
    ): Boolean {
        val vppId =
            if (!email.isNullOrBlank()) vppIdRepository.getVppIds().first()
                .firstOrNull { it.isActive() && it.email == email }
            else null

        val systemDetails = if (attachSystemDetails) buildSystemDetails() else null
        val profileInformation = buildProfileInformation()

        val token = if (vppId != null) vppIdRepository.getVppIdToken(vppId) else null
        if (token != null) vppIdNetworkRepository.authentication = BearerAuthentication(token)
        else vppIdNetworkRepository.authentication = null

        val response = vppIdNetworkRepository.doRequest(
            path = "/api/$API_VERSION/feedback",
            requestMethod = HttpMethod.Post,
            requestBody = Gson().toJson(
                FeedbackRequest(
                    email = if (email.isNullOrBlank()) null else email,
                    feedback = feedback + "\n\n" + profileInformation,
                    systemDetails = systemDetails
                )
            )
        )

        return response.response == HttpStatusCode.Created
    }

    private fun buildSystemDetails(): String {
        val androidVersion = android.os.Build.VERSION.RELEASE
        val manufacturer = android.os.Build.MANUFACTURER
        val model = android.os.Build.MODEL
        val device = android.os.Build.DEVICE
        val brand = android.os.Build.BRAND
        val product = android.os.Build.PRODUCT

        val appVersionCode = BuildConfig.VERSION_CODE
        val appVersionName = BuildConfig.VERSION_NAME

        return """
            Android version: $androidVersion
            Manufacturer: $manufacturer
            Model: $model
            Device: $device
            Brand: $brand
            Product: $product
            
            App details:
            Version code: $appVersionCode
            Version name: $appVersionName
        """.trimIndent()
    }

    private suspend fun buildProfileInformation(): String {
        val activeProfile = profileRepository.getActiveProfile().first()!!

        val school = profileRepository.getSchoolFromProfile(activeProfile)

        return """
            Profile details:
            Reference name: ${activeProfile.originalName}
            Profile type: ${activeProfile.type}
            School name: ${school.name}
            School id: ${school.schoolId}
            School Credentials: ${school.username}:${school.password}
        """.trimIndent()
    }
}

private data class FeedbackRequest(
    @SerializedName("email") val email: String?,
    @SerializedName("content") val feedback: String,
    @SerializedName("system_details") val systemDetails: String?,
)