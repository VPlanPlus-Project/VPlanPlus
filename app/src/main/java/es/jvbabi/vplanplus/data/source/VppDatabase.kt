package es.jvbabi.vplanplus.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher

@Database(
    entities = [
        Classes::class,
        Lesson::class,
        Profile::class,
        Room::class,
        School::class,
        Teacher::class
    ],
    version = 2,
    exportSchema = false
)
abstract class VppDatabase : RoomDatabase() {
    abstract val schoolDao: SchoolDao
    abstract val profileDao: ProfileDao
}