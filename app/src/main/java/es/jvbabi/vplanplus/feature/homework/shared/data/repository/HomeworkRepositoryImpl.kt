package es.jvbabi.vplanplus.feature.homework.shared.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import es.jvbabi.vplanplus.data.model.DbHomework
import es.jvbabi.vplanplus.data.model.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDao
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkModificationResult
import es.jvbabi.vplanplus.feature.homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.shared.data.TokenAuthentication
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import es.jvbabi.vplanplus.shared.data.VppIdServer
import es.jvbabi.vplanplus.util.DateUtils.toLocalUnixTimestamp
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class HomeworkRepositoryImpl(
    private val homeworkDao: HomeworkDao,
    private val classRepository: ClassRepository,
    private val vppIdRepository: VppIdRepository,
    private val profileRepository: ProfileRepository,
    private val vppIdNetworkRepository: VppIdNetworkRepository
): HomeworkRepository {
    @Deprecated("Doesn't work")
    override suspend fun fetchData() {
        val vppIds = vppIdRepository.getVppIds().first()
        profileRepository
            .getProfiles()
            .first()
            .filter { it.type == ProfileType.STUDENT }
            .forEach { profile ->
                val vppId = vppIds
                    .firstOrNull { it.classes?.classId == profile.referenceId && it.isActive() }

                val `class` = classRepository.getClassById(profile.referenceId) ?: return@forEach
                val school = `class`.school

                if (vppId != null) {
                    val token = vppIdRepository.getVppIdToken(vppId) ?: return@forEach
                    vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", token)
                } else {
                    vppIdNetworkRepository.authentication = TokenAuthentication("sp24.", school.buildToken())
                    vppIdNetworkRepository.globalHeaders["Class"] = `class`.name
                }

                val response = vppIdNetworkRepository.doRequest(
                    "/api/${VppIdServer.apiVersion}/homework/",
                )

                if (response.response != HttpStatusCode.OK || response.data == null) return@forEach
                Gson().fromJson(response.data, HomeworkResponse::class.java).homework
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

    override suspend fun getHomeworkById(homeworkId: Int): Flow<Homework?> {
        return homeworkDao.getById(homeworkId).map { it?.toModel() }
    }

    override suspend fun insertHomework(
        createdBy: VppId?,
        createdAt: LocalDateTime,
        `class`: Classes,
        defaultLessonVpId: Long,
        shareWithClass: Boolean,
        until: LocalDate,
        tasks: List<String>,
        allowCloudUpdate: Boolean
    ): HomeworkModificationResult {
        if (!allowCloudUpdate || createdBy == null) {
            val dbHomework = DbHomework(
                id = findLocalId()-1,
                classes = `class`.classId,
                createdAt = createdAt,
                until = until,
                defaultLessonVpId = defaultLessonVpId,
                createdBy = createdBy?.id
            )
            homeworkDao.insert(dbHomework)
            tasks.forEach {
                val dbHomeworkTask = DbHomeworkTask(
                    id = findLocalTaskId()-1,
                    homeworkId = dbHomework.id,
                    content = it,
                    done = false,
                    individualId = null
                )
                homeworkDao.insertTask(dbHomeworkTask)
            }
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.classes?.classId == `class`.classId && it.isActive() } ?: return HomeworkModificationResult.FAILED

        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/${VppIdServer.apiVersion}/homework/",
            requestBody = Gson().toJson(
                AddHomeworkRequest(
                    vpId = vppId.id.toLong(),
                    shareWithClass = shareWithClass,
                    until = until.toLocalUnixTimestamp(),
                    tasks = tasks
                )
            ),
            requestMethod = HttpMethod.Post
        )
        if (result.response != HttpStatusCode.Created || result.data == null) return HomeworkModificationResult.FAILED
        val response = Gson().fromJson(result.data, AddHomeworkResponse::class.java)

        val dbHomework = DbHomework(
            id = response.id.toLong(),
            classes = `class`.classId,
            createdAt = createdAt,
            until = until,
            defaultLessonVpId = defaultLessonVpId,
            createdBy = createdBy.id,
            isPublic = shareWithClass
        )
        homeworkDao.insert(dbHomework)
        response.tasks.forEach {
            val dbHomeworkTask = DbHomeworkTask(
                id = it.id,
                homeworkId = dbHomework.id,
                content = it.content,
                done = false,
                individualId = it.individualId
            )
            homeworkDao.insertTask(dbHomeworkTask)
        }
        return HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
    }

    override suspend fun deleteOrHideHomework(
        homework: Homework,
        onlyHide: Boolean
    ): HomeworkModificationResult {
        if (homework.id < 0) {
            homeworkDao.deleteHomework(homework.id)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.isActive() && it.id == homework.createdBy?.id } ?: return HomeworkModificationResult.FAILED

        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/${VppIdServer.apiVersion}/homework/",
            requestBody = Gson().toJson(
                DeleteHomeworkRequest(
                    id = homework.id,
                    onlyHide = false
                )
            ),
            requestMethod = HttpMethod.Delete
        )
        return if (result.response == HttpStatusCode.OK) {
            homeworkDao.deleteHomework(homework.id)
            HomeworkModificationResult.SUCCESS_OFFLINE
        } else {
            HomeworkModificationResult.FAILED
        }
    }

    override suspend fun addNewTask(
        homework: Homework,
        content: String
    ): HomeworkModificationResult {
        if (homework.id < 0) {
            val dbHomeworkTask = DbHomeworkTask(
                id = findLocalTaskId()-1,
                homeworkId = homework.id,
                content = content,
                done = false,
                individualId = null
            )
            homeworkDao.insertTask(dbHomeworkTask)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.isActive() && it.id == homework.createdBy?.id } ?: return HomeworkModificationResult.FAILED

        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/${VppIdServer.apiVersion}/homework/",
            requestBody = Gson().toJson(
                AddTaskRequest(
                    homeworkId = homework.id,
                    content = content
                )
            ),
            requestMethod = HttpMethod.Put
        )

        if (result.response != HttpStatusCode.Created || result.data == null) return HomeworkModificationResult.FAILED
        val response = Gson().fromJson(result.data, AddHomeworkResponseTask::class.java)
        val dbHomeworkTask = DbHomeworkTask(
            id = response.id,
            homeworkId = homework.id,
            content = response.content,
            done = false,
            individualId = response.individualId
        )
        homeworkDao.insertTask(dbHomeworkTask)
        return HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
    }

    override suspend fun deleteTask(task: HomeworkTask): HomeworkModificationResult {
        val parent = getHomeworkByTask(task)

        if (task.id < 0) {
            homeworkDao.deleteTask(task.id)
            if (getHomeworkById(parent.id.toInt()).first()?.tasks?.isEmpty() == true) {
                homeworkDao.deleteHomework(parent.id)
            }
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.isActive() && it.id == parent.createdBy?.id } ?: return HomeworkModificationResult.FAILED
        if (task.individualId == null) return HomeworkModificationResult.FAILED

        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/${VppIdServer.apiVersion}/homework/",
            requestBody = Gson().toJson(
                DeleteHomeworkTaskRequest(
                    taskId = task.id,
                )
            ),
            requestMethod = HttpMethod.Delete
        )

        return if (result.response == HttpStatusCode.OK || result.response == HttpStatusCode.NotFound) {
            homeworkDao.deleteTask(task.id)
            if (getHomeworkById(parent.id.toInt()).first()?.tasks?.isEmpty() == true) {
                homeworkDao.deleteHomework(parent.id)
            }
            HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        } else {
            HomeworkModificationResult.FAILED
        }
    }

    override suspend fun editTaskContent(
        task: HomeworkTask,
        newContent: String
    ): HomeworkModificationResult {
        if (task.id < 0) {
            val dbHomeworkTask = homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(content = newContent)
            homeworkDao.insertTask(dbHomeworkTask)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val parent = getHomeworkByTask(task)
        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.isActive() && it.id == parent.createdBy?.id } ?: return HomeworkModificationResult.FAILED

        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/${VppIdServer.apiVersion}/homework/",
            requestBody = Gson().toJson(
                ChangeTaskRequest(
                    taskId = task.id,
                    content = newContent
                )
            ),
            requestMethod = HttpMethod.Put
        )

        return if (result.response == HttpStatusCode.OK) {
            val dbHomeworkTask = homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(content = newContent)
            homeworkDao.insertTask(dbHomeworkTask)
            HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        } else {
            HomeworkModificationResult.FAILED
        }
    }

    override suspend fun setTaskState(
        homework: Homework,
        task: HomeworkTask,
        done: Boolean
    ): HomeworkModificationResult {
        if (homework.id < 0) {
            val dbHomeworkTask = homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(done = done)
            homeworkDao.insertTask(dbHomeworkTask)
            return HomeworkModificationResult.SUCCESS_OFFLINE
        }

        val vppId = vppIdRepository
            .getVppIds().first()
            .firstOrNull { it.isActive() && it.id == homework.createdBy?.id } ?: return HomeworkModificationResult.FAILED

        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/${VppIdServer.apiVersion}/homework/",
            requestBody = Gson().toJson(
                MarkDoneRequest(
                    taskId = task.individualId!!,
                    done = done
                )
            ),
            requestMethod = HttpMethod.Put
        )
        return if (result.response == HttpStatusCode.OK) {
            val dbHomeworkTask = homeworkDao.getHomeworkTaskById(task.id.toInt()).first().copy(done = done)
            homeworkDao.insertTask(dbHomeworkTask)
            HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        } else {
            HomeworkModificationResult.FAILED
        }
    }

    override suspend fun findLocalId(): Long {
        val homework = homeworkDao.getAll().first().minByOrNull { it.homework.id }
        return (homework?.homework?.id ?: 0) - 1
    }

    override suspend fun findLocalTaskId(): Long {
        val task = homeworkDao.getAll().first().flatMap { it.tasks }.minByOrNull { it.id }
        return (task?.id ?: 0) - 1
    }

    override suspend fun getHomeworkByTask(task: HomeworkTask): Homework {
        val homeworkId = homeworkDao.getHomeworkTaskById(task.id.toInt()).first().homeworkId
        return homeworkDao.getById(homeworkId.toInt()).first()!!.toModel()
    }

    override suspend fun changeVisibility(homework: Homework): HomeworkModificationResult {
        if (homework.id < 0) throw UnsupportedOperationException("Cannot change visibility of local homework")
        val vppId = homework.createdBy ?: throw UnsupportedOperationException("Cannot change visibility of homework without creator")
        vppIdNetworkRepository.authentication = TokenAuthentication("vpp.", vppIdRepository.getVppIdToken(vppId) ?: return HomeworkModificationResult.FAILED)
        val result = vppIdNetworkRepository.doRequest(
            path = "/api/${VppIdServer.apiVersion}/homework/",
            requestBody = Gson().toJson(
                ChangeVisibilityRequest(
                    id = homework.id,
                    visibility = !homework.isPublic
                )
            ),
            requestMethod = HttpMethod.Put
        )

        return if (result.response == HttpStatusCode.OK) {
            homeworkDao.changeVisibility(homework.id, !homework.isPublic)
            HomeworkModificationResult.SUCCESS_ONLINE_AND_OFFLINE
        } else {
            HomeworkModificationResult.FAILED
        }
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
    val tasks: List<HomeRecordTask>
)

private data class HomeRecordTask @JvmOverloads constructor(
    @SerializedName("id") val id: Int,
    @SerializedName("individual_id") val individualId: Int? = null,
    @SerializedName("content") val content: String,
    @SerializedName("done") val done: Boolean? = null
)

private data class MarkDoneRequest(
    @SerializedName("change") val change: String = "state",
    @SerializedName("individual_id") val taskId: Long,
    @SerializedName("to") val done: Boolean
)

private data class AddTaskRequest(
    @SerializedName("change") val change: String = "add_task",
    @SerializedName("homework_id") val homeworkId: Long,
    @SerializedName("to") val content: String
)

private data class ChangeTaskRequest(
    @SerializedName("change") val change: String = "task_content",
    @SerializedName("task_id") val taskId: Long,
    @SerializedName("to") val content: String

)

private data class AddHomeworkRequest(
    @SerializedName("vp_id") val vpId: Long,
    @SerializedName("share_with_class") val shareWithClass: Boolean,
    @SerializedName("until") val until: Long,
    @SerializedName("tasks") val tasks: List<String>
)

private data class AddHomeworkResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("tasks") val tasks: List<AddHomeworkResponseTask>,
)

private data class AddHomeworkResponseTask(
    @SerializedName("id") val id: Long,
    @SerializedName("individual_id") val individualId: Long,
    @SerializedName("content") val content: String
)

private data class DeleteHomeworkRequest(
    @SerializedName("homework_id") val id: Long,
    @SerializedName("only_hide") val onlyHide: Boolean
)

private data class DeleteHomeworkTaskRequest(
    @SerializedName("task_id") val taskId: Long
)

private data class ChangeVisibilityRequest(
    @SerializedName("change") val change: String = "visibility",
    @SerializedName("homework_id") val id: Long,
    @SerializedName("to") val visibility: Boolean
)