package es.jvbabi.vplanplus.ui.screens.settings.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.ui.screens.settings.profile.dashedBorder

@Composable
fun ProfileCard(
    type: ProfileType?,
    name: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(),
        border = if (type != null) BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer) else null,
        modifier = Modifier
            .padding(end = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .then(modifier)
            .size(width = 80.dp, height = 80.dp)
            .dashedBorder(if (type == null) 2.dp else 0.dp, MaterialTheme.colorScheme.onSecondaryContainer, 16.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                if (type != null) {
                    Text(text = name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = when (type) {
                            ProfileType.STUDENT -> stringResource(id = R.string.classStr)
                            ProfileType.TEACHER -> stringResource(id = R.string.teacher)
                            ProfileType.ROOM -> stringResource(id = R.string.room)
                        }, style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(text = "+", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }

}

@Preview
@Composable
private fun ProfileCardPreview() {
    ProfileCard(ProfileType.STUDENT, "7a")
}