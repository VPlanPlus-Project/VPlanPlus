package es.jvbabi.vplanplus.feature.main_grades.view.ui.view.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.feature.main_grades.view.ui.view.GradeEvent
import es.jvbabi.vplanplus.ui.common.InfoCard

@Composable
fun AdvertiseBiometricAuthenticationBanner(
    isVisible: Boolean,
    onEvent: (GradeEvent) -> Unit
) {
    val context = LocalContext.current
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(tween(200)),
        exit = shrinkVertically(tween(200))
    ) {
        InfoCard(
            imageVector = Icons.Default.Fingerprint,
            title = stringResource(id = R.string.grades_enableBiometricTitle),
            text = stringResource(id = R.string.grades_enableBiometricText),
            buttonText1 = stringResource(id = R.string.not_now),
            buttonAction1 = { onEvent(GradeEvent.DismissEnableBiometricBanner) },
            buttonText2 = stringResource(id = R.string.enable),
            buttonAction2 = {
                onEvent(GradeEvent.DismissEnableBiometricBanner)
                onEvent(GradeEvent.EnableBiometric)
                Toast.makeText(
                    context,
                    context.getString(R.string.grades_biometricNextTime),
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        )
    }
}