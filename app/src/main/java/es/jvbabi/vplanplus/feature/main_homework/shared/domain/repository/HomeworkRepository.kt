package es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository

import android.net.Uri
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
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

    suspend fun addNewTask(
        profile: ClassProfile,
        homework: Homework,
        content: String,
    ): HomeworkModificationResult

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

    suspend fun fetchHomework(sendNotification: Boolean)

    suspend fun getHomeworkByTask(task: HomeworkTask): Homework

    suspend fun changeShareStatus(profile: ClassProfile, homework: Homework): HomeworkModificationResult

    suspend fun updateDueDate(profile: ClassProfile, homework: Homework, newDate: ZonedDateTime): HomeworkModificationResult

    suspend fun clearCache()

    fun isUpdateRunning(): Boolean

    suspend fun setPreferredHomeworkNotificationTime(hour: Int, minute: Int, dayOfWeek: DayOfWeek)
    suspend fun removePreferredHomeworkNotificationTime(dayOfWeek: DayOfWeek)
    fun getPreferredHomeworkNotificationTimes(): Flow<List<PreferredHomeworkNotificationTime>>

    /**
     * Adds a document to a homework. This will not save the document to the device, it will only register it in the database and upload it to the cloud if the homework is not local. Creating the actual document is the responsibility of the caller.
     * @author Julius Babies
     * @param vppId The VPP ID of the homework. If null, the homework is assumed to be a local homework and the document will be stored locally.
     * @param homework The homework to which the document should be added.
     * @param content The content of the document. (See [es.jvbabi.vplanplus.data.repository.FileRepository.readBytes] for reading from a [Uri])
     * @param name The name of the document without the extension.
     * @param type The type of the document (only pdf and jpg is supported at the moment)
     * @param onUploading A callback that is called when the document is being uploaded. The first parameter is the number of bytes sent, the second is the total number of bytes. This will only be called if the document is uploaded to the cloud.
     * @return A [Response] with the result of the operation. The first parameter is the result, the second is the ID of the document if the operation was successful.
     */
    suspend fun addDocumentToHomework(vppId: VppId? = null, homework: Homework, content: ByteArray, name: String, type: HomeworkDocumentType, onUploading: (sent: Long, total: Long) -> Unit = { _, _ ->}): Response<HomeworkModificationResult, Int?>
    suspend fun editDocument(vppId: VppId? = null, homeworkDocument: HomeworkDocument, newName: String?): HomeworkModificationResult

    /**
     * Removes a document from the database and cloud if necessary. This will not delete the document from the device, it will only remove it from the database and cloud if the homework is not local. Deleting the actual document is the responsibility of the caller.
     * @param vppId The VPP ID of the homework. If null, the homework is assumed to be a local homework and no vpp.ID API calls will be made.
     * @param homeworkDocument The document to be deleted.
     * @return A [HomeworkModificationResult] indicating the result of the operation.
     */
    suspend fun deleteDocument(vppId: VppId? = null, homeworkDocument: HomeworkDocument): HomeworkModificationResult
    suspend fun getDocumentById(id: Int): HomeworkDocument?
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