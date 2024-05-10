package es.jvbabi.vplanplus.feature.room_search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Room

@Composable
fun RoomRow(room: Room, lessons: List<Lesson>) {

}

@Composable
fun RoomName(roomName: String) {
    Box(modifier = Modifier
        .padding(start = 8.dp)
        .height(48.dp)
        .width(64.dp)
        .clip(RoundedCornerShape(4.dp, 0.dp, 0.dp, 4.dp))
        .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = roomName,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
@Preview
private fun RoomNamePreview() {
    RoomName(roomName = "115a")
}