package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.MainActivity
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.RowVerticalCenter

@Composable
fun Header(onCloseMenu: () -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        IconButton(
            onClick = { onCloseMenu() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        }
        RowVerticalCenter(Modifier.align(Alignment.Center)) {
            Image(
                painter = if (MainActivity.isAppInDarkMode.value) painterResource(id = R.drawable.vpp_logo_light) else painterResource(
                    id = R.drawable.vpp_logo_dark
                ),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .size(32.dp),
            )
            VerticalDivider(
                modifier = Modifier
                    .padding(8.dp)
                    .height(20.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HeaderPreview() {
    Header(onCloseMenu = {})
}