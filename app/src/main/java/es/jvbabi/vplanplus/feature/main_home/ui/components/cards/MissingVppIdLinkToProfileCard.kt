package es.jvbabi.vplanplus.feature.main_home.ui.components.cards

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.InfoCard

@Composable
fun MissingVppIdLinkToProfileCard(
    modifier: Modifier = Modifier,
    onFixClicked: () -> Unit
) {
    InfoCard(
        modifier = modifier,
        imageVector = Icons.Default.ManageAccounts,
        title = stringResource(id = R.string.home_linkVppIdToProfileBannerTitle),
        text = stringResource(id = R.string.home_linkVppIdToProfileBannerText),
        buttonAction1 = onFixClicked,
        buttonText1 = stringResource(id = R.string.to_settings),
        backgroundColor = MaterialTheme.colorScheme.errorContainer,
        textColor = MaterialTheme.colorScheme.onErrorContainer
    )
}

@Preview(showBackground = true)
@Composable
fun MissingVppIdLinkToProfileCardPreview() {
    MissingVppIdLinkToProfileCard {}
}