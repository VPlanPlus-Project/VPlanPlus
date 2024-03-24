package es.jvbabi.vplanplus.feature.main_grades.ui.calculator

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import es.jvbabi.vplanplus.feature.main_grades.domain.model.GradeModifier

class GradeCalculatorViewModel : ViewModel() {
    val state = mutableStateOf(GradeCalculatorState(emptyList(), 0.0))
    private val originalGrades = mutableListOf<GradeCollection>()

    fun init(grades: List<GradeCollection>, init: Boolean) {
        if (init) {
            originalGrades.clear()
            originalGrades.addAll(grades)
        }
        state.value = GradeCalculatorState(
            grades = grades,
            avg = grades.map { it.grades.sumOf { grade -> grade.first.toDouble() } / it.grades.size }
                .filter { !it.isNaN() }
                .average()
        )
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
            init = false
        )
    }

    fun restore() {
        init(originalGrades, false)
    }
}

data class GradeCalculatorState(
    val grades: List<GradeCollection>,
    val avg: Double
)

data class GradeCollection(
    val name: String,
    val grades: List<Pair<Float, GradeModifier>>,
)