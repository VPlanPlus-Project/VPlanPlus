package es.jvbabi.vplanplus.feature.settings.profile.ui.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.ui.common.InfoDialog

@Composable
fun SchoolDetailsDialog(
    school: School,
    onClose: () -> Unit
) {
    InfoDialog(
        icon = Icons.Default.Info,
        title = stringResource(id = R.string.profileManagement_detailsLabel),
        message = buildAnnotatedString {
            val default = MaterialTheme.typography.bodyMedium.toSpanStyle()
            val bold = default.copy(fontWeight = FontWeight.Bold)
            val values = mapOf(
                stringResource(id = R.string.profileManagement_detailsId) to school.id.toString(),
                stringResource(id = R.string.profileManagement_detailsSp24Id) to school.sp24SchoolId.toString(),
                stringResource(id = R.string.profileManagement_detailsUsername) to school.username,
                stringResource(id = R.string.profileManagement_detailsPassword) to school.password,
                stringResource(id = R.string.profileManagement_detailsName) to school.name,
                stringResource(id = R.string.profileManagement_detailsPreferredDownloadMode) to school.schoolDownloadMode.name,
                stringResource(id = R.string.profileManagement_detailsDaysPerWeek) to school.daysPerWeek.toString(),
                stringResource(id = R.string.profileManagement_detailsCredentialsValid) to school.credentialsValid.toString(),
                stringResource(id = R.string.profileManagement_detailsFullyCompatible) to school.fullyCompatible.toString()
            )

            values.forEach { (key, value) ->
                withStyle(bold) {
                    append("$key: ")
                }
                withStyle(default) {
                    append(value)
                }
                append("\n")
            }

        },
        onOk = onClose
    )
}

@Preview
@Composable
private fun SchoolDetailsDialogPreview() {
    SchoolDetailsDialog(
        school = School(
            id = 1,
            sp24SchoolId = 1,
            username = "username",
            password = "password",
            name = "name",
            schoolDownloadMode = SchoolDownloadMode.INDIWARE_MOBIL,
            daysPerWeek = 5,
            credentialsValid = true,
            fullyCompatible = false
        ),
        onClose = {}
    )
}