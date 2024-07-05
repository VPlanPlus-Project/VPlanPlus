package es.jvbabi.vplanplus.ui.common

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.model.HomeworkDocument
import java.io.File

@Composable
fun HomeworkDocument.buildUri(): Uri {
    val context = LocalContext.current
    return buildUri(context)
}

fun HomeworkDocument.buildUri(context: Context): Uri {
    val file = File(context.filesDir, "homework_documents/${documentId}")
    return Uri.fromFile(file)
}