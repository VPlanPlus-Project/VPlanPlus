package es.jvbabi.vplanplus.feature.migration.usecase

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import kotlinx.coroutines.flow.first
import java.nio.charset.StandardCharsets
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class GenerateMigrationTextUseCase(
    private val profileRepository: ProfileRepository,
    private val keyValueRepository: KeyValueRepository,
    private val homeworkRepository: HomeworkRepository
) {
    @OptIn(ExperimentalEncodingApi::class)
    suspend operator fun invoke(): String {
        return Migration(
            schools = profileRepository.getProfiles().first().map { it.getSchool() }.distinctBy { it.id }.map { school ->
                SchoolMigration(
                    id = school.id,
                    indiwareId = school.sp24SchoolId.toString(),
                    username = school.username,
                    password = school.password,
                    profiles = profileRepository.getProfiles().first().map { profile ->
                        ProfileMigration(
                            type = profile.getType().name,
                            entityName = profile.originalName,
                            displayName = profile.displayName,
                            defaultLessons = if (profile is ClassProfile) profile.defaultLessons.map { (defaultLesson, enabled) ->
                                DefaultLessonMigration(
                                    vpId = defaultLesson.vpId,
                                    enabled = enabled
                                )
                            } else null,
                            homework = if (profile is ClassProfile) homeworkRepository.getAllByProfile(profile).first().filter { it.homework.id < 0 }.map { homework ->
                                HomeworkMigration(
                                    vpId = homework.homework.defaultLesson?.vpId,
                                    date = homework.homework.until.toLocalDate().toString(),
                                    tasks = homework.tasks.map { task ->
                                        HomeworkTaskMigration(
                                            task = task.content,
                                            isDone = task.isDone
                                        )
                                    }
                                )
                            }
                            else null,
                            vppIdToken = (profile as? ClassProfile)?.vppId?.vppIdToken
                        )
                    }
                )
            },
            settings = SettingsMigration(
                protectGrades = keyValueRepository.get(Keys.GRADES_BIOMETRIC_ENABLED)?.toBooleanStrict() == true
            )
        ).let {
            Base64.encode(Gson().toJson(it).toByteArray(StandardCharsets.UTF_8))
        }
    }
}

data class Migration(
    @SerializedName("schools") val schools: List<SchoolMigration>,
    @SerializedName("settings") val settings: SettingsMigration,
)

data class SchoolMigration(
    @SerializedName("id") val id: Int,
    @SerializedName("indiware_id") val indiwareId: String,
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("profiles") val profiles: List<ProfileMigration>,
)

data class ProfileMigration(
    @SerializedName("type") val type: String,
    @SerializedName("entity_name") val entityName: String,
    @SerializedName("display_name") val displayName: String,
    @SerializedName("default_lessons") val defaultLessons: List<DefaultLessonMigration>?,
    @SerializedName("homework") val homework: List<HomeworkMigration>?,
    @SerializedName("vpp_id") val vppIdToken: String?
)

data class DefaultLessonMigration(
    @SerializedName("vp_id") val vpId: Int,
    @SerializedName("enabled") val enabled: Boolean,
)

data class HomeworkMigration(
    @SerializedName("vp_id") val vpId: Int?,
    @SerializedName("date") val date: String,
    @SerializedName("tasks") val tasks: List<HomeworkTaskMigration>,
)

data class HomeworkTaskMigration(
    @SerializedName("task") val task: String,
    @SerializedName("is_done") val isDone: Boolean,
)

data class SettingsMigration(
    @SerializedName("protect_grades") val protectGrades: Boolean
)