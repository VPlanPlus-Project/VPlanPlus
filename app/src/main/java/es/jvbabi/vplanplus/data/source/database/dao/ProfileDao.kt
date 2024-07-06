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

    @Transaction
    @Query("SELECT * FROM profile_teacher")
    abstract fun getTeacherProfiles(): Flow<List<CTeacherProfile>>

    @Transaction
    @Query("SELECT * FROM profile_room")
    abstract fun getRoomProfiles(): Flow<List<CRoomProfile>>

    @Query("INSERT INTO profile_class (id, name, custom_name, calendar_mode, calendar_id, class_id, is_homework_enabled, vpp_id) VALUES (:id, :name, :customName, :calendarMode, :calendarId, :classId, :isHomeworkEnabled, :vppId)")
    abstract fun createClassProfile(id: UUID, name: String, customName: String, calendarMode: ProfileCalendarType, calendarId: Long?, classId: Int, isHomeworkEnabled: Boolean, vppId: Int?)

    @Query("INSERT INTO profile_teacher (id, name, custom_name, calendar_mode, calendar_id, teacher_id) VALUES (:id, :name, :customName, :calendarMode, :calendarId, :teacherId)")
    abstract fun createTeacherProfile(id: UUID, name: String, customName: String, calendarMode: ProfileCalendarType, calendarId: Long?, teacherId: UUID)

    @Query("INSERT INTO profile_room (id, name, custom_name, calendar_mode, calendar_id, room_id) VALUES (:id, :name, :customName, :calendarMode, :calendarId, :roomId)")
    abstract fun createRoomProfile(id: UUID, name: String, customName: String, calendarMode: ProfileCalendarType, calendarId: Long?, roomId: Int)

    @Query("UPDATE profile_class SET vpp_id = :vppId WHERE id = :profileId")
    abstract fun setVppIdForClassProfile(profileId: UUID, vppId: Int?)

    @Query("DELETE FROM profile_class WHERE id = :profileId")
    abstract fun deleteClassProfile(profileId: UUID)

    @Query("DELETE FROM profile_teacher WHERE id = :profileId")
    abstract fun deleteTeacherProfile(profileId: UUID)

    @Query("DELETE FROM profile_room WHERE id = :profileId")
    abstract fun deleteRoomProfile(profileId: UUID)

    @Query("UPDATE profile_class SET calendar_id = :calendarId WHERE id = :profileId")
    abstract fun setCalendarIdForClassProfile(profileId: UUID, calendarId: Long?)

    @Query("UPDATE profile_teacher SET calendar_id = :calendarId WHERE id = :profileId")
    abstract fun setCalendarIdForTeacherProfile(profileId: UUID, calendarId: Long?)

    @Query("UPDATE profile_room SET calendar_id = :calendarId WHERE id = :profileId")
    abstract fun setCalendarIdForRoomProfile(profileId: UUID, calendarId: Long?)

    @Query("UPDATE profile_class SET calendar_mode = :calendarMode WHERE id = :profileId")
    abstract fun setCalendarModeForClassProfile(profileId: UUID, calendarMode: ProfileCalendarType)

    @Query("UPDATE profile_teacher SET calendar_mode = :calendarMode WHERE id = :profileId")
    abstract fun setCalendarModeForTeacherProfile(profileId: UUID, calendarMode: ProfileCalendarType)

    @Query("UPDATE profile_room SET calendar_mode = :calendarMode WHERE id = :profileId")
    abstract fun setCalendarModeForRoomProfile(profileId: UUID, calendarMode: ProfileCalendarType)

    @Query("UPDATE profile_class SET custom_name = :customName WHERE id = :profileId")
    abstract fun setCustomNameForClassProfile(profileId: UUID, customName: String)

    @Query("UPDATE profile_teacher SET custom_name = :customName WHERE id = :profileId")
    abstract fun setCustomNameForTeacherProfile(profileId: UUID, customName: String)

    @Query("UPDATE profile_room SET custom_name = :customName WHERE id = :profileId")
    abstract fun setCustomNameForRoomProfile(profileId: UUID, customName: String)

    @Query("UPDATE profile_class SET is_homework_enabled = :enabled WHERE id = :profileId")
    abstract fun setHomeworkEnabledForClassProfile(profileId: UUID, enabled: Boolean)
}