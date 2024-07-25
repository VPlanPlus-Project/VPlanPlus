package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.SchoolSp24Access

interface GroupRepository {
    suspend fun insertGroup(schoolSp24Access: SchoolSp24Access, groupId: Int? = null, groupName: String, isClass: Boolean): Boolean
    suspend fun getGroupBySchoolAndName(schoolId: Int, groupName: String): Group?
    suspend fun getGroupById(id: Int): Group?
    suspend fun deleteGroupsBySchoolId(schoolId: Int)
    suspend fun getGroupsBySchool(school: School): List<Group>
    suspend fun getAll(): List<Group>
}