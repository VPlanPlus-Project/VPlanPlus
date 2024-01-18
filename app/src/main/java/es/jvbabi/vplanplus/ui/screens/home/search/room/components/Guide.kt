package es.jvbabi.vplanplus.ui.screens.home.search.room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun Guide(className: String?) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .height(40.dp)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.error),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.searchAvailableRoom_roomInUse),
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onError
            )
        }
        Box(
            modifier = Modifier
                .height(40.dp)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.tertiaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(
                    id = R.string.searchAvailableRoom_roomBooked,
                    className
                        ?: stringResource(R.string.searchAvailableRoom_roomBookedAClass)
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}