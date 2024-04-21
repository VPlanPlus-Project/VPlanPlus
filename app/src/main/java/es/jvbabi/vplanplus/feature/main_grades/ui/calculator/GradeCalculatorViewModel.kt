package es.jvbabi.vplanplus.feature.main_grades.ui.calculator

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import es.jvbabi.vplanplus.feature.main_grades.domain.model.GradeModifier

class GradeCalculatorViewModel : ViewModel() {
    val state = mutableStateOf(GradeCalculatorState(emptyList(),0.0))
    private val originalGrades = mutableListOf<GradeCollection>()
    private var originalIsSek2 = false

    fun init(grades: List<GradeCollection>, isSek2: Boolean, init: Boolean) {
        if (init) {
            originalGrades.clear()
            originalGrades.addAll(grades)
            originalIsSek2 = isSek2
        }
        state.value = GradeCalculatorState(
            grades = grades,
            avg = grades.map { it.grades.sumOf { grade -> grade.first.toDouble() } / it.grades.size }
                .filter { !it.isNaN() }
                .average(),
            isSek2 = isSek2
        )
    }

    fun onSek2Change(isSek2: Boolean) {
        init(originalGrades, isSek2, false)
    }

    fun addGrade(collection: String, grade: Float) {
        init(
            state.value.grades.map {
                if (it.name == collection) {
                    it.copy(grades = it.grades.plus(grade to GradeModifier.NEUTRAL))
                } else {
                    it
                }
            },
            isSek2 = state.value.isSek2,
            init = false
        )
    }

    fun removeGradeByIndex(collection: String, index: Int) {
        init(
            state.value.grades.map {
                if (it.name == collection) {
                    it.copy(grades = it.grades.filterIndexed { i, _ -> i != index })
                } else {
                    it
                }
            },
            isSek2 = state.value.isSek2,
            init = false
        )
    }

    fun restore() {
        init(originalGrades, originalIsSek2, false)
    }
}

data class GradeCalculatorState(
    val grades: List<GradeCollection> = emptyList(),
    val avg: Double = 0.0,
    val isSek2: Boolean = false,
)

data class GradeCollection(
    val name: String,
    val grades: List<Pair<Float, GradeModifier>>,
)