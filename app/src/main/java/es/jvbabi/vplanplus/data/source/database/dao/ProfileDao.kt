package es.jvbabi.vplanplus.data.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import es.jvbabi.vplanplus.data.model.combined.CClassProfile
import es.jvbabi.vplanplus.data.model.combined.CRoomProfile
import es.jvbabi.vplanplus.data.model.combined.CTeacherProfile
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
abstract class ProfileDao {

    @Query("SELECT * FROM profile_class")
    @Transaction
    abstract fun getClassProfiles(): Flow<List<CClassProfile>>

    @Query("SELECT * FROM profile_class WHERE id = :profileId")
    @Transaction
    abstract fun getClassProfileById(profileId: UUID): Flow<CClassProfile?>

    @Transaction
    @Query("SELECT * FROM profile_teacher")
    abstract fun getTeacherProfiles(): Flow<List<CTeacherProfile>>

    @Query("SELECT * FROM profile_teacher WHERE id = :profileId")
    @Transaction
    abstract fun getTeacherProfileById(profileId: UUID): Flow<CTeacherProfile?>

    @Transaction
    @Query("SELECT * FROM profile_room")
    abstract fun getRoomProfiles(): Flow<List<CRoomProfile>>

    @Query("SELECT * FROM profile_room WHERE id = :profileId")
    @Transaction
    abstract fun getRoomProfileById(profileId: UUID): Flow<CRoomProfile?>

    @Query("INSERT INTO profile_class (id, name, custom_name, calendar_mode, calendar_id, class_id, is_homework_enabled, is_assessments_enabled, is_notifications_enabled, vpp_id) VALUES (:id, :name, :customName, :calendarMode, :calendarId, :classId, :isHomeworkEnabled, :isAssessmentsEnabled, :isNotificationsEnabled, :vppId)")
    abstract suspend fun createClassProfile(id: UUID, name: String, customName: String, calendarMode: ProfileCalendarType, calendarId: Long?, classId: Int, isHomeworkEnabled: Boolean, isAssessmentsEnabled: Boolean, isNotificationsEnabled: Boolean, vppId: Int?)

    @Query("INSERT INTO profile_teacher (id, name, custom_name, calendar_mode, calendar_id, teacher_id) VALUES (:id, :name, :customName, :calendarMode, :calendarId, :teacherId)")
    abstract suspend fun createTeacherProfile(id: UUID, name: String, customName: String, calendarMode: ProfileCalendarType, calendarId: Long?, teacherId: UUID)

    @Query("INSERT INTO profile_room (id, name, custom_name, calendar_mode, calendar_id, room_id) VALUES (:id, :name, :customName, :calendarMode, :calendarId, :roomId)")
    abstract suspend fun createRoomProfile(id: UUID, name: String, customName: String, calendarMode: ProfileCalendarType, calendarId: Long?, roomId: Int)

    @Query("UPDATE profile_class SET vpp_id = :vppId WHERE id = :profileId")
    abstract suspend fun setVppIdForClassProfile(profileId: UUID, vppId: Int?)

    @Query("DELETE FROM profile_class WHERE id = :profileId")
    abstract suspend fun deleteClassProfile(profileId: UUID)

    @Query("DELETE FROM profile_teacher WHERE id = :profileId")
    abstract suspend fun deleteTeacherProfile(profileId: UUID)

    @Query("DELETE FROM profile_room WHERE id = :profileId")
    abstract suspend fun deleteRoomProfile(profileId: UUID)

    @Query("UPDATE profile_class SET calendar_id = :calendarId WHERE id = :profileId")
    abstract suspend fun setCalendarIdForClassProfile(profileId: UUID, calendarId: Long?)

    @Query("UPDATE profile_teacher SET calendar_id = :calendarId WHERE id = :profileId")
    abstract suspend fun setCalendarIdForTeacherProfile(profileId: UUID, calendarId: Long?)

    @Query("UPDATE profile_room SET calendar_id = :calendarId WHERE id = :profileId")
    abstract suspend fun setCalendarIdForRoomProfile(profileId: UUID, calendarId: Long?)

    @Query("UPDATE profile_class SET calendar_mode = :calendarMode WHERE id = :profileId")
    abstract suspend fun setCalendarModeForClassProfile(profileId: UUID, calendarMode: ProfileCalendarType)

    @Query("UPDATE profile_teacher SET calendar_mode = :calendarMode WHERE id = :profileId")
    abstract suspend fun setCalendarModeForTeacherProfile(profileId: UUID, calendarMode: ProfileCalendarType)

    @Query("UPDATE profile_room SET calendar_mode = :calendarMode WHERE id = :profileId")
    abstract suspend fun setCalendarModeForRoomProfile(profileId: UUID, calendarMode: ProfileCalendarType)

    @Query("UPDATE profile_class SET custom_name = :customName WHERE id = :profileId")
    abstract suspend fun setCustomNameForClassProfile(profileId: UUID, customName: String)

    @Query("UPDATE profile_teacher SET custom_name = :customName WHERE id = :profileId")
    abstract suspend fun setCustomNameForTeacherProfile(profileId: UUID, customName: String)

    @Query("UPDATE profile_room SET custom_name = :customName WHERE id = :profileId")
    abstract suspend fun setCustomNameForRoomProfile(profileId: UUID, customName: String)

    @Query("UPDATE profile_class SET is_homework_enabled = :enabled WHERE id = :profileId")
    abstract suspend fun setHomeworkEnabledForClassProfile(profileId: UUID, enabled: Boolean)

    @Query("UPDATE profile_class SET is_assessments_enabled = :enabled WHERE id = :profileId")
    abstract suspend fun setAssessmentEnabledForClassProfile(profileId: UUID, enabled: Boolean)

    @Query("UPDATE profile_class SET custom_name = :customName, calendar_mode = :calendarMode, calendar_id = :calendarId, class_id = :classId, is_homework_enabled = :isHomeworkEnabled, is_assessments_enabled = :isAssessmentsEnabled, vpp_id = :vppId, is_notifications_enabled = :isNotificationsEnabled, notification_settings = :notificationSettings WHERE id = :profileId")
    abstract suspend fun updateClassProfile(profileId: UUID, customName: String, calendarMode: ProfileCalendarType, calendarId: Long?, classId: Int, isHomeworkEnabled: Boolean, isAssessmentsEnabled: Boolean, vppId: Int?, isNotificationsEnabled: Boolean, notificationSettings: String)

    @Query("UPDATE profile_teacher SET custom_name = :customName, calendar_mode = :calendarMode, calendar_id = :calendarId, teacher_id = :teacherId, is_notifications_enabled = :isNotificationsEnabled, notification_settings = :notificationSettings WHERE id = :profileId")
    abstract suspend fun updateTeacherProfile(profileId: UUID, customName: String, calendarMode: ProfileCalendarType, calendarId: Long?, teacherId: UUID, isNotificationsEnabled: Boolean, notificationSettings: String)

    @Query("UPDATE profile_room SET custom_name = :customName, calendar_mode = :calendarMode, calendar_id = :calendarId, room_id = :roomId, is_notifications_enabled = :isNotificationsEnabled, notification_settings = :notificationSettings WHERE id = :profileId")
    abstract suspend fun updateRoomProfile(profileId: UUID, customName: String, calendarMode: ProfileCalendarType, calendarId: Long?, roomId: Int, isNotificationsEnabled: Boolean, notificationSettings: String)
}