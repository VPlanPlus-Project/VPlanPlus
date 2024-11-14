package es.jvbabi.vplanplus.ui

import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ExamCategory
import es.jvbabi.vplanplus.feature.main_homework.add.ui.SaveType

fun ExamCategory.stringResource(): Int {
    return when (this) {
        is ExamCategory.ShortTest -> R.string.exam_type_shortTest
        is ExamCategory.Project -> R.string.exam_type_project
        is ExamCategory.ClassTest -> R.string.exam_type_classTest
        is ExamCategory.Oral -> R.string.exam_type_oral
        is ExamCategory.Other -> R.string.exam_type_other
    }
}

fun SaveType.stringResource(): Int {
    return when (this) {
        SaveType.LOCAL -> R.string.saveType_local
        SaveType.CLOUD -> R.string.saveType_cloud
        SaveType.SHARED -> R.string.saveType_shared
    }
}