package es.jvbabi.vplanplus.data.repository

import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.source.database.dao.GroupDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolDao
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.SchoolSp24Access
import es.jvbabi.vplanplus.domain.repository.GroupInfoResponse
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.first

class GroupRepositoryImpl(
    private val groupDao: GroupDao,
    private val schoolDao: SchoolDao,
    private val vppIdNetworkRepository: VppIdNetworkRepository
) : GroupRepository {
    private var groupsThatDoNotExist = mutableListOf<String>()
    override suspend fun insertGroup(schoolSp24Access: SchoolSp24Access, groupId: Int?, groupName: String, isClass: Boolean): Boolean {
        val id = groupId ?: run {
            vppIdNetworkRepository.authentication = schoolSp24Access.buildVppAuthentication()
            val response = vppIdNetworkRepository.doRequest("/api/$API_VERSION/school/${schoolSp24Access.schoolId}/group")
            if (response.response != HttpStatusCode.OK || response.data == null) return false
            val groups = ResponseDataWrapper.fromJson<List<GroupInfoResponse>>(response.data)!!
            groups.firstOrNull { it.className == groupName }?.groupId ?: return false
        }
        groupDao.upsert(
            DbGroup(
                schoolId = schoolSp24Access.schoolId,
                name = groupName,
                id = id,
                isClass = isClass
            )
        )
        return true
    }

    override suspend fun getGroupBySchoolAndName(
        schoolId: Int,
        groupName: String
    ): Group? {
        val group = groupDao.getGroupsBySchoolId(schoolId = schoolId).first().firstOrNull { it.group.name == groupName } ?: run {
            if ("$schoolId.$groupName" in groupsThatDoNotExist) return null
            val updateSuccessful = insertGroup(schoolDao.getSchoolFromId(schoolId)?.toModel()?.buildAccess() ?: return null, null, groupName, true)
            if (!updateSuccessful) {
                groupsThatDoNotExist.add("$schoolId.$groupName")
                return null
            }
            return getGroupBySchoolAndName(schoolId, groupName)
        }
        return group.toModel()
    }

    override suspend fun getGroupById(id: Int): Group? {
        return groupDao.getGroupById(groupId = id).first()?.toModel()
    }

    override suspend fun deleteGroupsBySchoolId(schoolId: Int) {
        groupDao.deleteGroupsBySchoolId(schoolId = schoolId)
    }

    override suspend fun getGroupsBySchool(school: School): List<Group> {
        return groupDao.getGroupsBySchoolId(schoolId = school.id).first().map { it.toModel() }
    }

    override suspend fun getAll(): List<Group> {
        return groupDao.getAll().first().map { it.toModel() }
    }
}