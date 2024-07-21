package es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.GradeEvent
import es.jvbabi.vplanplus.ui.common.InfoCard

@Composable
fun BiometricNotSetUpBanner(
    isVisible: Boolean,
    onOpenSecuritySettings: () -> Unit,
    onEvent: (GradeEvent) -> Unit
) {
    AnimatedVisibility(visible = isVisible) {
        InfoCard(
            imageVector = Icons.Default.Fingerprint,
            title = stringResource(id = R.string.grades_biometricNotSetUpTitle),
            text = stringResource(id = R.string.grades_biometricNotSetUpText),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            buttonText1 = stringResource(id = R.string.grades_openSecuritySettings),
            buttonAction1 = onOpenSecuritySettings,
            buttonText2 = stringResource(id = R.string.disable),
            buttonAction2 = { onEvent(GradeEvent.DisableBiometric) }
        )
    }
}