package es.jvbabi.vplanplus.feature.onboarding.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.shared.ExampleResponses
import es.jvbabi.vplanplus.shared.data.FakeKeyValueRepository
import es.jvbabi.vplanplus.shared.data.NetworkRepositoryImpl
import es.jvbabi.vplanplus.shared.data.VPlanRepositoryImpl
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test


class DefaultLessonUseCaseTest {
    private lateinit var defaultLessonUseCase: DefaultLessonUseCase

    private lateinit var keyValueRepository: KeyValueRepository
    private lateinit var vPlanRepository: VPlanRepository

    @Before
    fun setUp() {
        val server = MockWebServer()
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(ExampleResponses.timetable)
                .setHeader("Content-Type", "application/xml")
        )

        keyValueRepository = FakeKeyValueRepository()
        vPlanRepository = VPlanRepositoryImpl(NetworkRepositoryImpl(server = server.url("/").toString(), logRepository = null))

        defaultLessonUseCase = DefaultLessonUseCase(vPlanRepository, keyValueRepository)

    }

    @Test
    fun `Test default lessons`() {
        var a = ""
        runBlocking {
            val schoolId = 1L
            val defaultLessons = defaultLessonUseCase.invoke(schoolId = schoolId, username = "user", password = "password", className = "5a")
            assertThat(defaultLessons).isNotNull()
            assertThat(defaultLessons!!.size).isEqualTo(15)
            assertThat(defaultLessons.map { it.className }.toSet()).containsExactly("5a")
            assertThat(keyValueRepository.getOrDefault("onboarding.school.$schoolId.defaultLessons", "")).isNotEmpty()

            val gson = Gson()
            val defaultLessonsFromKV = gson.fromJson(keyValueRepository.getOrDefault("onboarding.school.$schoolId.defaultLessons", ""), Array<DefaultLesson>::class.java).toList()
            a = keyValueRepository.getOrDefault("onboarding.school.$schoolId.defaultLessons", "")
            assertThat(defaultLessonsFromKV).isEqualTo(defaultLessons)
        }
        assertThat(a).isNotEmpty()
    }
}