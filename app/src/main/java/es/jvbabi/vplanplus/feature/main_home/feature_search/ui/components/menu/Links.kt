package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.DOT
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter

@Composable
fun Links(
    modifier: Modifier = Modifier,
    onRepositoryClicked: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit
) {
    RowVerticalCenter(
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.home_menuPrivacy),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f, true)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onPrivacyPolicyClicked() }
                .padding(8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = DOT,
        )
        Text(
            text = stringResource(id = R.string.home_menuRepository),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .weight(1f, true)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onRepositoryClicked() }
                .padding(8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun LinksPreview() {
    Links(
        onRepositoryClicked = {},
        onPrivacyPolicyClicked = {}
    )
}