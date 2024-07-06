package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.result.components

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.ui.common.unknownVppId
import es.jvbabi.vplanplus.util.DateUtils.progress
import es.jvbabi.vplanplus.util.DateUtils.toZonedLocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SearchResultBooking(
    booking: RoomBooking, resultType: ProfileType, currentTime: ZonedDateTime
) {
    Box(
        modifier = Modifier
            .padding(start = 8.dp, bottom = 8.dp)
            .size(65.dp)
            .border(
                1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(CardDefaults.cardColors().containerColor),
    ) {
        val progress = currentTime.progress(booking.from, booking.to)
        if (progress > 0) Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .fillMaxWidth(minOf(progress, 1f))
                .fillMaxHeight()
        ) // Progress bar
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.basicMarquee(
                        iterations = Int.MAX_VALUE, velocity = 80.dp, spacing = MarqueeSpacing(12.dp)
                    ), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = when (resultType) {
                        ProfileType.TEACHER -> "?"
                        ProfileType.ROOM -> booking.bookedBy?.group?.name ?: unknownVppId()
                        ProfileType.STUDENT -> booking.room.name
                    }, style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${booking.from.toZonedLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))}\n${booking.to.toZonedLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}