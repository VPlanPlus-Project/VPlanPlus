package es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository

import android.net.Uri
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.main_homework.shared.data.repository.AddHomeworkResponse
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.CloudHomework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.Homework
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocumentType
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkTask
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.PreferredHomeworkNotificationTime
import es.jvbabi.vplanplus.shared.data.Response
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.util.UUID

interface HomeworkRepository {

    suspend fun getHomeworkByGroupId(groupId: Int): Flow<List<Homework>>

    suspend fun getHomeworkById(homeworkId: Int): Flow<Homework?>

    suspend fun getAll(): Flow<List<Homework>>

    @Deprecated("Use split up methods instead instead")
    suspend fun insertHomework(
        id: Long? = null,
        profile: ClassProfile,
        defaultLessonVpId: Int?,
        storeInCloud: Boolean,
        shareWithClass: Boolean,
        until: ZonedDateTime,
        tasks: List<NewTaskRecord>,
        isHidden: Boolean,
        createdAt: ZonedDateTime = ZonedDateTime.now(),
        documentUris: List<Document>,
        onDocumentUploadProgressChanges: (Uri, Float) -> Unit = { _, _ -> }
    ): HomeworkModificationResult

    @Deprecated("Use split up methods instead instead")
    suspend fun setTaskState(
        profile: ClassProfile,
        homework: Homework,
        task: HomeworkTask,
        done: Boolean
    ): HomeworkModificationResult

    suspend fun editTaskContent(
        profile: ClassProfile,
        task: HomeworkTask,
        newContent: String
    ): HomeworkModificationResult

    suspend fun removeOrHideHomework(
        profile: ClassProfile,
        homework: Homework,
        task: DeleteTask
    ): HomeworkModificationResult

    suspend fun deleteTask(
        profile: ClassProfile,
        task: HomeworkTask
    ): HomeworkModificationResult

    suspend fun findLocalId(): Long
    suspend fun findLocalTaskId(): Long
    suspend fun findLocalDocumentId(): Int

    @Deprecated("don't do this")
    suspend fun fetchHomework(sendNotification: Boolean)

    suspend fun getHomeworkByTask(taskId: Int): Homework

    @Deprecated("Use split up methods instead instead")
    suspend fun changeShareStatus(profile: ClassProfile, homework: CloudHomework): HomeworkModificationResult

    @Deprecated("Use split up methods instead instead")
    suspend fun updateDueDate(profile: ClassProfile, homework: Homework, newDate: ZonedDateTime): HomeworkModificationResult

    suspend fun clearCache()

    fun isUpdateRunning(): Boolean

    suspend fun setPreferredHomeworkNotificationTime(hour: Int, minute: Int, dayOfWeek: DayOfWeek)
    suspend fun removePreferredHomeworkNotificationTime(dayOfWeek: DayOfWeek)
    fun getPreferredHomeworkNotificationTimes(): Flow<List<PreferredHomeworkNotificationTime>>

    @Deprecated("Use split up methods instead instead")
    suspend fun editDocument(vppId: VppId? = null, homeworkDocument: HomeworkDocument, newName: String?): HomeworkModificationResult

    /**
     * Removes a document from the database and cloud if necessary. This will not delete the document from the device, it will only remove it from the database and cloud if the homework is not local. Deleting the actual document is the responsibility of the caller.
     * @param vppId The VPP ID of the homework. If null, the homework is assumed to be a local homework and no vpp.ID API calls will be made.
     * @param homeworkDocument The document to be deleted.
     * @return A [HomeworkModificationResult] indicating the result of the operation.
     */
    @Deprecated("Use split up methods instead instead")
    suspend fun deleteDocument(vppId: VppId? = null, homeworkDocument: HomeworkDocument): HomeworkModificationResult
    suspend fun getDocumentById(id: Int): HomeworkDocument?

    /**
     * Adds a document to the database. This will not upload the document to the cloud, it will only save it to the device. Uploading the document is the responsibility of the caller.
     * @param documentId The ID of the document. If null, the next available local ID will be used.
     * @param homeworkId The ID of the homework to which the document belongs.
     * @param name The name of the document.
     * @param type The type of the document.
     * @return The ID of the document, either the one provided or the next available local ID.
     */
    suspend fun addDocumentToDb(documentId: Int? = null, homeworkId: Int, name: String, type: HomeworkDocumentType): HomeworkDocumentId

    /**
     * Uploads a document to the cloud. This will not save the document to the device, it will only upload it to the cloud. Creating the actual document is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param name The name of the document without the extension
     * @param homeworkId The ID of the homework to which the document belongs
     * @param type The type of the document
     * @param content The content of the document
     * @param onUploading A callback that is called when the document is being uploaded. The first parameter is the number of bytes sent, the second is the total number of bytes.
     * @return A [Response] containing the result of the operation and the ID of the document if it was successful.
     */
    suspend fun uploadDocument(vppId: VppId, name: String, homeworkId: Int, type: HomeworkDocumentType, content: ByteArray, onUploading: (sent: Long, total: Long) -> Unit): Response<HomeworkModificationResult, Int?>

    /**
     * Adds a homework to the database. This will not upload the homework to the cloud, it will only save it to the device. Uploading the homework is the responsibility of the caller.
     * @param homeworkId The ID of the homework. If null, the next available local ID will be used.
     * @param clazzProfile The class profile to which the homework belongs. Ignored if vppID is set since the homework isn't tied to a profile at that point.
     * @param defaultLessonVpId The ID of the default lesson. If null, the default lesson will be set to null.
     * @param dueTo The due date of the homework.
     * @param vppId The VPP ID of the homeworks creator. Can be null if it's a local homework.
     * @param isHidden Whether the homework is hidden
     * @param isPublic Whether the homework is public
     * @param createdAt The creation date of the homework.
     * @return The ID of the homework, either the one provided or the next available local ID.
     */
    suspend fun addHomeworkToDb(homeworkId: Int? = null, clazzProfile: ClassProfile, defaultLessonVpId: Int?, dueTo: ZonedDateTime, vppId: VppId?, isHidden: Boolean = false, isPublic: Boolean, createdAt: ZonedDateTime): HomeworkId

    /**
     * Uploads a homework to the cloud. This will not save the homework to the device, it will only upload it to the cloud. Creating the actual homework is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param dueTo The due date of the homework.
     * @param tasks The tasks of the homework.
     * @param vpId The ID of the default lesson. If null, the default lesson will be set to null.
     * @param isPublic Whether the homework is public
     * @return A [Response] containing the result of the operation and the ID of the homework if it was successful.
     */
    suspend fun uploadHomework(vppId: VppId, dueTo: ZonedDateTime, tasks: List<String>, vpId: Int? = null, isPublic: Boolean): Response<HomeworkModificationResult, AddHomeworkResponse?>

    /**
     * Adds a homework task to the database. This will not upload the task to the cloud, it will only save it to the device. Uploading the task is the responsibility of the caller.
     * @param homeworkId The ID of the homework to which the task belongs.
     * @param taskId The ID of the task. If null, the next available local ID will be used.
     * @param isDone Whether the task is done.
     * @param content The content of the task.
     * @return The ID of the task, either the one provided or the next available local ID.
     */
    suspend fun addHomeworkTaskToDb(homeworkId: Int, taskId: Int?, isDone: Boolean = false, content: String): HomeworkTaskId

    /**
     * Uploads a homework task to the cloud. This will not save the task to the device, it will only upload it to the cloud. Creating the actual task is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param homeworkId The ID of the homework to which the task belongs.
     * @param content The content of the task.
     * @return A [Response] containing the result of the operation and the ID of the task if it was successful.
     */
    suspend fun uploadHomeworkTask(vppId: VppId, homeworkId: Int, content: String): Response<HomeworkModificationResult, Int?>

    /**
     * Uploads the state of a homework task to the cloud. This will not save the state to the device, it will only upload it to the cloud. Saving the state is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param homeworkTaskId The ID of the task.
     * @param isDone Whether the task is done.
     * @return A [Response] containing the result of the operation and null if it was not successful.
     */
    suspend fun uploadTaskState(vppId: VppId, homeworkTaskId: Int, isDone: Boolean): Response<HomeworkModificationResult, Unit?>

    suspend fun setTaskStateToDb(homeworkTaskId: Int, isDone: Boolean)
}

enum class HomeworkModificationResult {
    FAILED,
    SUCCESS_OFFLINE,
    SUCCESS_ONLINE_AND_OFFLINE
}

data class NewTaskRecord(
    val content: String,
    val done: Boolean = false,
    val id: Long? = null
)

enum class DeleteTask {
    DELETE,
    HIDE,
    FORCE_DELETE_LOCALLY
}


data class Document(
    val uri: Uri,
    val name: String = UUID.randomUUID().toString(),
    val extension: String
)

typealias HomeworkDocumentId = Int
typealias HomeworkTaskId = Int
typealias HomeworkId = Int
