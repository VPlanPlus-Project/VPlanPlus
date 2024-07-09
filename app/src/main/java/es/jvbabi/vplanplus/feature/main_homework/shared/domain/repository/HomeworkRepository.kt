package es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository

import android.net.Uri
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.main_homework.shared.data.repository.AddHomeworkResponse
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

    suspend fun findLocalId(): Int
    suspend fun findLocalTaskId(): Int
    suspend fun findLocalDocumentId(): Int

    /**
     * Downloads the homework from the cloud and returns it. This won't save anything to the database. Use [downloadHomeworkDocument] to download the actual documents.
     * @param vppId The vpp.ID as which the request shall be executed. Null if only public homeworks should be downloaded.
     * @param group The group for which the homework should be downloaded.
     * @return A list of [Homework.CloudHomework] if the download was successful, null otherwise.
     * @see [downloadHomeworkDocument]
     * @see [addHomeworkDb]
     */
    suspend fun downloadHomework(vppId: VppId?, group: Group): List<Homework.CloudHomework>?

    suspend fun downloadHomeworkDocument(vppId: VppId?, group: Group, homeworkId: Int, homeworkDocumentId: Int): ByteArray?

    suspend fun downloadHomeworkDocumentMetadata(vppId: VppId?, group: Group, homeworkId: Int, homeworkDocumentId: Int): HomeworkDocument?

    suspend fun getHomeworkByTask(taskId: Int): Homework

    suspend fun clearCache()

    fun isUpdateRunning(): Boolean

    suspend fun setPreferredHomeworkNotificationTime(hour: Int, minute: Int, dayOfWeek: DayOfWeek)
    suspend fun removePreferredHomeworkNotificationTime(dayOfWeek: DayOfWeek)
    fun getPreferredHomeworkNotificationTimes(): Flow<List<PreferredHomeworkNotificationTime>>

    suspend fun getDocumentById(id: Int): HomeworkDocument?

    /**
     * Adds a document to the database. This will not upload the document to the cloud, it will only save it to the device. Uploading the document is the responsibility of the caller.
     * @param documentId The ID of the document. If null, the next available local ID will be used.
     * @param homeworkId The ID of the homework to which the document belongs.
     * @param name The name of the document.
     * @param type The type of the document.
     * @return The ID of the document, either the one provided or the next available local ID.
     */
    suspend fun addDocumentDb(documentId: Int? = null, homeworkId: Int, name: String, type: HomeworkDocumentType): HomeworkDocumentId

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
    suspend fun addDocumentCloud(vppId: VppId, name: String, homeworkId: Int, type: HomeworkDocumentType, content: ByteArray, onUploading: (sent: Long, total: Long) -> Unit): Response<HomeworkModificationResult, Int?>

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
    suspend fun addHomeworkDb(homeworkId: Int? = null, clazzProfile: ClassProfile? = null, defaultLessonVpId: Int?, dueTo: ZonedDateTime, vppId: VppId?, isHidden: Boolean = false, isPublic: Boolean, createdAt: ZonedDateTime): HomeworkId

    /**
     * Uploads a homework to the cloud. This will not save the homework to the device, it will only upload it to the cloud. Creating the actual homework is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param dueTo The due date of the homework.
     * @param tasks The tasks of the homework.
     * @param vpId The ID of the default lesson. If null, the default lesson will be set to null.
     * @param isPublic Whether the homework is public
     * @return A [Response] containing the result of the operation and the ID of the homework if it was successful.
     */
    suspend fun addHomeworkCloud(vppId: VppId, dueTo: ZonedDateTime, tasks: List<String>, vpId: Int? = null, isPublic: Boolean): Response<HomeworkModificationResult, AddHomeworkResponse?>

    /**
     * Adds a homework task to the database. This will not upload the task to the cloud, it will only save it to the device. Uploading the task is the responsibility of the caller.
     * @param homeworkId The ID of the homework to which the task belongs.
     * @param taskId The ID of the task. If null, the next available local ID will be used.
     * @param isDone Whether the task is done.
     * @param content The content of the task.
     * @return The ID of the task, either the one provided or the next available local ID.
     */
    suspend fun addTaskDb(homeworkId: Int, taskId: Int?, isDone: Boolean = false, content: String): HomeworkTaskId

    /**
     * Uploads a homework task to the cloud. This will not save the task to the device, it will only upload it to the cloud. Creating the actual task is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param homeworkId The ID of the homework to which the task belongs.
     * @param content The content of the task.
     * @return A [Response] containing the result of the operation and the ID of the task if it was successful.
     */
    suspend fun addTaskCloud(vppId: VppId, homeworkId: Int, content: String): Response<HomeworkModificationResult, Int?>

    /**
     * Uploads the state of a homework task to the cloud. This will not save the state to the device, it will only upload it to the cloud. Saving the state is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param homeworkTaskId The ID of the task.
     * @param isDone Whether the task is done.
     * @return A [Response] containing the result of the operation and null if it was not successful.
     */
    suspend fun changeTaskStateCloud(vppId: VppId, homeworkTaskId: Int, isDone: Boolean): Response<HomeworkModificationResult, Unit?>

    /**
     * Updates the state of a homework task in the database.
     * @param homeworkTaskId The ID of the task.
     * @param isDone Whether the task is done.
     */
    suspend fun changeTaskStateDb(homeworkTaskId: Int, isDone: Boolean)

    /**
     * Updates the name of a document in the cloud. This will not save the name to the device, it will only upload it to the cloud. Saving the name is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param homeworkDocument The document to be renamed.
     * @param newName The new name of the document.
     */
    suspend fun changeDocumentNameCloud(vppId: VppId, homeworkDocument: HomeworkDocument, newName: String): Response<HomeworkModificationResult, Unit?>

    /**
     * Updates the name of a document in the database.
     * @param homeworkDocument The document to be renamed.
     * @param newName The new name of the document.
     * @return The ID of the document.
     * @see [changeDocumentNameCloud]
     */
    suspend fun changeDocumentNameDb(homeworkDocument: HomeworkDocument, newName: String)

    /**
     * Deletes a document from the cloud. Be sure to delete the actual file before since this method won't remove it.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param homeworkDocument The document to be deleted.
     * @return A [Response] containing the result of the operation and null if it was not successful.
     * @see [deleteDocumentDb]
     */
    suspend fun deleteDocumentCloud(vppId: VppId, homeworkDocument: HomeworkDocument): Response<HomeworkModificationResult, Unit?>

    /**
     * Deletes a document from the database. Be sure to delete the actual file before since this method won't remove it.
     * @param homeworkDocument The document to be deleted.
     * @see [deleteDocumentCloud]
     */
    suspend fun deleteDocumentDb(homeworkDocument: HomeworkDocument)

    /**
     * Update the sharing status of a homework in the database.
     * @param homework The homework to be updated.
     * @param isPublic Whether the homework is public.
     * @see [changeHomeworkSharingCloud]
     */
    suspend fun changeHomeworkSharingDb(homework: Homework.CloudHomework, isPublic: Boolean)

    /**
     * Uploads the sharing status of a homework to the cloud. This will not save the sharing status to the device, it will only upload it to the cloud. Saving the sharing status is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param homework The homework to be updated.
     * @param isPublic Whether the homework is public.
     * @return A [Response] containing the result of the operation and null if it was not successful.
     * @see [changeHomeworkSharingDb]
     */
    suspend fun changeHomeworkSharingCloud(vppId: VppId, homework: Homework.CloudHomework, isPublic: Boolean): Response<HomeworkModificationResult, Unit?>

    /**
     * Updates the due date of a homework in the database.
     * @param homework The homework to be updated.
     * @param hide Whether the homework is hidden.
     */
    suspend fun changeHomeworkVisibilityDb(homework: Homework.CloudHomework, hide: Boolean)

    /**
     * Deletes a homework from the database. Be sure to delete all related documents before since this method won't remove them.
     * @param homework The homework to be deleted.
     * @see [deleteHomeworkCloud]
     */
    suspend fun deleteHomeworkDb(homework: Homework)

    /**
     * Deletes a homework from the cloud. Be sure to delete all related documents before since this method won't remove them.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param homework The homework to be deleted.
     * @return A [Response] containing the result of the operation and null if it was not successful.
     * @see [deleteHomeworkDb]
     */
    suspend fun deleteHomeworkCloud(vppId: VppId, homework: Homework.CloudHomework): Response<HomeworkModificationResult, Unit?>

    /**
     * Updates the content of a homework task in the cloud. This will not save the content to the device, it will only upload it to the cloud. Saving the content is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param homeworkTask The task to be updated.
     * @param newContent The new content of the task.
     * @return A [Response] containing the result of the operation and null if it was not successful.
     * @see [changeTaskContentDb]
     */
    suspend fun changeTaskContentCloud(vppId: VppId, homeworkTask: HomeworkTask, newContent: String): Response<HomeworkModificationResult, Unit?>

    /**
     * Updates the content of a homework task in the database. This will only save the content to the device, it will not upload it to the cloud.
     * @param homeworkTask The task to be updated.
     * @param newContent The new content of the task.
     * @see [changeTaskContentCloud]
     */
    suspend fun changeTaskContentDb(homeworkTask: HomeworkTask, newContent: String)

    /**
     * Deletes a homework task from the cloud. This won't delete it from the local database.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param task The task to be deleted.
     * @return A [Response] containing the result of the operation and null if it was not successful.
     * @see [deleteTaskDb]
     */
    suspend fun deleteTaskCloud(vppId: VppId, task: HomeworkTask): Response<HomeworkModificationResult, Unit?>

    /**
     * Deletes a homework task from the database. This won't delete it from the cloud.
     * @param task The task to be deleted.
     * @see [deleteTaskCloud]
     */
    suspend fun deleteTaskDb(task: HomeworkTask)

    /**
     * Updates the due date of a homework in the database. This will not upload the due date to the cloud, it will only save it to the device. Uploading the due date is the responsibility of the caller.
     * @param homework The homework to be updated.
     * @param newDate The new due date of the homework.
     * @see [changeDueDateCloud]
     */
    suspend fun changeDueDateDb(homework: Homework, newDate: ZonedDateTime)

    /**
     * Updates the due date of a homework in the cloud. This will not save the due date to the device, it will only upload it to the cloud. Saving the due date is the responsibility of the caller.
     * @param vppId The vpp.ID as which the request shall be executed
     * @param homework The homework to be updated.
     * @param newDate The new due date of the homework.
     * @return A [Response] containing the result of the operation and null if it was not successful.
     * @see [changeDueDateDb]
     */
    suspend fun changeDueDateCloud(vppId: VppId, homework: Homework, newDate: ZonedDateTime): Response<HomeworkModificationResult, Unit?>
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

data class Document(
    val uri: Uri,
    val name: String = UUID.randomUUID().toString(),
    val extension: String
)

typealias HomeworkDocumentId = Int
typealias HomeworkTaskId = Int
typealias HomeworkId = Int