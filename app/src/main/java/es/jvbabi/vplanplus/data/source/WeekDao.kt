package es.jvbabi.vplanplus.data.source

import androidx.room.Dao
import androidx.room.Upsert
import es.jvbabi.vplanplus.domain.model.Week

@Dao
abstract class WeekDao {
    @Upsert
    abstract suspend fun insertWeek(week: Week)
}