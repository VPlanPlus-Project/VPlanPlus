package es.jvbabi.vplanplus.feature.room_search.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.material.R

@Composable
fun RoomSearchField(
    onRoomNameQueryChanged: (query: String) -> Unit,
    roomNameQuery: String
) {
    TextField(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { onRoomNameQueryChanged("") }) {
                Icon(imageVector = Icons.Outlined.Cancel, contentDescription = stringResource(id = R.string.clear_text_end_icon_content_description))
            }
        },
        leadingIcon = {
            Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
        },
        placeholder = { Text(text = stringResource(id = es.jvbabi.vplanplus.R.string.searchAvailableRoom_searchRoomName)) },
        value = roomNameQuery,
        onValueChange = onRoomNameQueryChanged
    )
}