package es.jvbabi.vplanplus.data.source.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import es.jvbabi.vplanplus.domain.model.State

@ProvidedTypeConverter
class VppIdStateConverter {

    @TypeConverter
    fun stateToInt(state: State): Int {
        return when (state) {
            State.ACTIVE -> 0
            State.DISABLED -> 1
        }
    }

    @TypeConverter
    fun intToState(int: Int): State {
        return when (int) {
            0 -> State.ACTIVE
            1 -> State.DISABLED
            else -> throw IllegalArgumentException("Unknown state")
        }
    }
}