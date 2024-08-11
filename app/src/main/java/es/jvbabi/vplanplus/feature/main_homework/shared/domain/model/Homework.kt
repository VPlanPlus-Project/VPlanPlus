package es.jvbabi.vplanplus.feature.main_homework.shared.domain.model

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.VppId
import java.time.ZonedDateTime

open class HomeworkTaskCore(
    val id: Int,
    val homeworkId: Int,
    val content: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HomeworkTaskCore) return false

        if (id != other.id) return false
        if (homeworkId != other.homeworkId) return false
        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + homeworkId
        result = 31 * result + content.hashCode()
        return result
    }
}

class HomeworkTaskDone(
    id: Int,
    homeworkId: Int,
    content: String,
    val isDone: Boolean
) : HomeworkTaskCore(id, homeworkId, content)

sealed class HomeworkCore(
    val id: Int,
    val createdAt: ZonedDateTime,
    val until: ZonedDateTime,
    open val tasks: List<HomeworkTaskCore>,
    val documents: List<HomeworkDocument>,
    val defaultLesson: DefaultLesson?
) {
    abstract fun getAssociatedGroup(): Group

    fun getTaskById(id: Int) = tasks.find { it.id == id }

    class LocalHomework(
        id: Int,
        createdAt: ZonedDateTime,
        until: ZonedDateTime,
        tasks: List<HomeworkTaskCore>,
        documents: List<HomeworkDocument>,
        defaultLesson: DefaultLesson?,
        val profile: ClassProfile
    ) : HomeworkCore(id, createdAt, until, tasks, documents, defaultLesson) {
        init {
            require(id < 0)
        }

        override fun getAssociatedGroup(): Group {
            return profile.group
        }
    }

    class CloudHomework(
        id: Int,
        createdAt: ZonedDateTime,
        until: ZonedDateTime,
        tasks: List<HomeworkTaskCore>,
        documents: List<HomeworkDocument>,
        defaultLesson: DefaultLesson?,
        val createdBy: VppId,
        val group: Group,
        val isPublic: Boolean,
    ) : HomeworkCore(id, createdAt, until, tasks, documents, defaultLesson) {
        init {
            require(id > 0)
        }

        override fun getAssociatedGroup(): Group {
            return this.group
        }
    }
}
sealed class PersonalizedHomework(
    val profile: ClassProfile,
    open val homework: HomeworkCore,
    val tasks: List<HomeworkTaskDone>
) {
    class LocalHomework(
        profile: ClassProfile,
        homework: HomeworkCore.LocalHomework,
        tasks: List<HomeworkTaskDone>
    ) : PersonalizedHomework(profile, homework, tasks)

    class CloudHomework(
        profile: ClassProfile,
        override val homework: HomeworkCore.CloudHomework,
        tasks: List<HomeworkTaskDone>,
        val isHidden: Boolean
    ) : PersonalizedHomework(profile, homework, tasks)

    fun allDone(): Boolean {
        return tasks.all { it.isDone }
    }

    fun getTaskById(id: Int) = tasks.find { it.id == id }
}