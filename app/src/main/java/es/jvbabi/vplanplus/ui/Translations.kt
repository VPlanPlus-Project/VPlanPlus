package es.jvbabi.vplanplus.ui

import androidx.compose.runtime.Composable
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ExamType
import es.jvbabi.vplanplus.feature.main_homework.add.ui.SaveType

@Composable
fun ExamType.stringResource(): Int {
    return when (this) {
        ExamType.SHORT_TEST -> R.string.exam_type_shortTest
        ExamType.PROJECT -> R.string.exam_type_project
        ExamType.CLASS_TEST -> R.string.exam_type_classTest
        ExamType.ORAL -> R.string.exam_type_oral
        ExamType.OTHER -> R.string.exam_type_other
    }
}

@Composable
fun SaveType.stringResource(): Int {
    return when (this) {
        SaveType.LOCAL -> R.string.saveType_local
        SaveType.CLOUD -> R.string.saveType_cloud
        SaveType.SHARED -> R.string.saveType_shared
    }
}