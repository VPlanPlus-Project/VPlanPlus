package es.jvbabi.vplanplus.feature.homework.shared.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.model.DbHomework
import es.jvbabi.vplanplus.data.model.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.shared.data.TokenAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import es.jvbabi.vplanplus.shared.data.VppIdServer
import es.jvbabi.vplanplus.util.DateUtils
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class HomeworkRepositoryImpl(
    private val homeworkDao: HomeworkDao,
    private val classRepository: ClassRepository,
    private val vppIdRepository: VppIdRepository,
    private val profileRepository: ProfileRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val vppIdNetworkRepository: VppIdNetworkRepository
): HomeworkRepository {
    override suspend fun fetchData() {
        var vppIds = vppIdRepository.getVppIds().first()
        profileRepository
            .getProfiles()
            .first()
            .filter { it.type == ProfileType.STUDENT }
            .forEach { profile ->
                val vppId = vppIds
                    .firstOrNull { it.classes?.classId == profile.referenceId && it.isActive() }

                if (vppId != null) {
                    val token = vppIdRepository.getVppIdToken(vppId) ?: return@forEach
                    vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", token)
                }

                val response = vppIdNetworkRepository.doRequest(
                    "/api/${VppIdServer.apiVersion}/homework/",
                )

                if (response.response != HttpStatusCode.OK || response.data == null) return@forEach
                val homework = Gson().fromJson(response.data, HomeworkResponse::class.java).homework
                val `class` = classRepository.getClassById(profile.referenceId) ?: return@forEach
                val school = `class`.school

                homework.forEach homework@{
                    val existing = getHomeworkById(it.id)?.first()
                    val createdBy = vppIds.firstOrNull { user ->
                        user.id == it.createdBy
                    } ?: run {
                        vppIdRepository.cacheVppId(it.createdBy, school)
                        vppIds = vppIdRepository.getVppIds().first()
                        vppIds.firstOrNull { user ->
                            user.id == it.createdBy
                        } ?: return@homework
                    }
                    val until = DateUtils.getDateFromTimestamp(it.until)

                    if (existing == null) {
                        insertHomeworkLocally(
                            Homework(
                                id = it.id,
                                createdBy = createdBy,
                                createdAt = DateUtils.getDateFromTimestamp(it.createdAt),
                                defaultLesson = defaultLessonRepository.getDefaultLessonByVpId(it.vpId.toLong()) ?: return@homework,
                                until = until,
                                classes = `class`,
                                tasks = it.tasks.map { task ->
                                    HomeworkTask(
                                        id = task.id,
                                        content = task.content,
                                        done = task.done ?: false
                                    )
                                }
                            )
                        )
                    } else {
                        insertHomeworkLocally(
                            existing.copy(
                                until = until,
                                tasks = existing.tasks.mapNotNull { task ->
                                    val newTask = it.tasks.firstOrNull { newTask -> newTask.id == task.id } ?: return@mapNotNull null
                                    task.copy(
                                        content = newTask.content,
                                        done = newTask.done ?: task.done
                                    )
                                }.plus(
                                    it
                                        .tasks
                                        .filter { existing.tasks.none { task -> task.id == it.id } }
                                        .map { task ->
                                            HomeworkTask(
                                                id = task.id,
                                                content = task.content,
                                                done = task.done ?: false
                                            )
                                        }
                                )
                            )
                        )
                    }
                }
        }
    }

    override suspend fun getHomeworkByClassId(classId: UUID): Flow<List<Homework>> {
        return homeworkDao.getByClassId(classId).map {
            it.map { homework -> homework.toModel() }
        }
    }

    override suspend fun getAll(): Flow<List<Homework>> {
        return homeworkDao.getAll().map {
            it.map { homework -> homework.toModel() }
        }
    }

    override suspend fun getHomeworkById(homeworkId: Int): Flow<Homework>? {
        return homeworkDao.getById(homeworkId)?.map { it.toModel() }
    }

    override suspend fun insertHomeworkLocally(homework: Homework) {
        val dbHomework = DbHomework(
            id = homework.id,
            createdBy = homework.createdBy?.id,
            createdAt = homework.createdAt,
            defaultLessonVpId = homework.defaultLesson.vpId,
            until = homework.until,
            classes = homework.classes.classId
        )
        homeworkDao.insert(dbHomework)
        homework.tasks.forEach { task ->
            homeworkDao.insertTask(
                DbHomeworkTask(
                    id = task.id,
                    content = task.content,
                    done = task.done,
                    homeworkId = homework.id
                )
            )
        }
    }

    override suspend fun updateTask(task: HomeworkTask) {
        val homework = homeworkDao.getHomeworkTaskById(task.id).first().copy(
            done = task.done,
            content = task.content
        )
        homeworkDao.insertTask(homework)
    }

    override suspend fun findLocalId(): Long {
        val homework = homeworkDao.getAll().first().minByOrNull { it.homework.id }
        return (homework?.homework?.id?.toLong() ?: 0) - 1
    }

    override suspend fun findLocalTaskId(): Long {
        val task = homeworkDao.getAll().first().flatMap { it.tasks }.minByOrNull { it.id }
        return (task?.id?.toLong() ?: 0) - 1
    }
}


private data class HomeworkResponse(
    val homework: List<HomeworkResponseRecord>
)

private data class HomeworkResponseRecord(
    val id: Int,
    @SerializedName("created_by") val createdBy: Int,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("vp_id") val vpId: Int,
    @SerializedName("due_at") val until: Long,
    val classes: Int,
    val tasks: List<HomeRecordTas>
)

private data class HomeRecordTas @JvmOverloads constructor(
    @SerializedName("id") val id: Int,
    @SerializedName("individual_id") val individualId: Int? = null,
    @SerializedName("content") val content: String,
    @SerializedName("done") val done: Boolean? = null
)