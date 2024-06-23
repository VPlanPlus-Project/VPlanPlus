package es.jvbabi.vplanplus.feature.main_homework.shared.domain.model

import android.net.Uri
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.VppId
import java.time.LocalDate
import java.time.ZonedDateTime

open class Homework(
    val id: Long,
    val createdBy: VppId?,
    val group: Group,
    val createdAt: ZonedDateTime,
    val defaultLesson: DefaultLesson?,
    val isPublic: Boolean,
    val until: ZonedDateTime,
    val tasks: List<HomeworkTask>,
    val isHidden: Boolean,
    val profile: Profile,
    val documents: List<Uri>
) {
    fun isOverdue(date: LocalDate): Boolean {
        return until.toLocalDate().isBefore(date) && tasks.any { !it.isDone }
    }

    fun canBeEdited(profile: ClassProfile): Boolean {
        if (this.profile.id == profile.id && this.id < 0) return true // is local and created by the profile
        if (profile.vppId != null && this.createdBy?.id == profile.vppId.id) return true // is remote and created by the profiles vpp.ID
        return false
    }

    fun getTaskById(id: Long): HomeworkTask? {
        return tasks.find { it.id == id }
    }
}

data class HomeworkTask(
    val id: Long,
    val content: String,
    val isDone: Boolean,
)