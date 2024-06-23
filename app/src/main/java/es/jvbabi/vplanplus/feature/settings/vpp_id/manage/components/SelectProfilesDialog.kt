package es.jvbabi.vplanplus.feature.settings.vpp_id.manage.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.ui.common.ComposableDialog

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SelectProfilesDialog(
    vppId: VppId,
    profiles: List<ClassProfile>,
    onDismiss: () -> Unit,
    onOk: (result: Map<ClassProfile, Boolean>) -> Unit
) {
    var selectedProfiles by rememberSaveable { mutableStateOf(emptyMap<ClassProfile, Boolean>()) }
    LaunchedEffect(key1 = profiles, key2 = vppId) {
        selectedProfiles = profiles
            .sortedBy { it.displayName }
            .associateWith { it.vppId == vppId }
            .toMutableMap()
    }

    ComposableDialog(
        icon = Icons.Default.ManageAccounts,
        title = stringResource(id = R.string.vppIdSettingsManagement_linkedProfilesTitle),
        cancelString = stringResource(id = android.R.string.cancel),
        onCancel = onDismiss,
        onDismiss = onDismiss,
        onOk = { onOk(selectedProfiles); onDismiss() },
        content = {
            Column {
                Text(text = stringResource(id = R.string.vppIdSettingsManagement_selectProfilesText))
                LazyColumn {
                    items(selectedProfiles.toList()) { (profile, selected) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { selectedProfiles = selectedProfiles.plus(profile to !selected) }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selected,
                                onCheckedChange = { selectedProfiles = selectedProfiles.plus(profile to !selected) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(text = profile.displayName)
                        }
                    }
                }
            }

        }
    )
}