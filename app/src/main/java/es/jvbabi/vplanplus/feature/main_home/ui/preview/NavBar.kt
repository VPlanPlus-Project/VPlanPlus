package es.jvbabi.vplanplus.feature.main_home.ui.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.NavigationBarItem
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.screens.Screen

val navBarItems = listOfNotNull(
    NavigationBarItem(
        onClick = {},
        icon = {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null
            )
        },
        label = { Text(text = stringResource(id = R.string.main_home)) },
        screen = Screen.HomeScreen
    ),
    NavigationBarItem(
        onClick = {},
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.MenuBook,
                contentDescription = null
            )
        },
        label = { Text(text = stringResource(id = R.string.main_homework)) },
        screen = Screen.HomeworkScreen
    ),
    NavigationBarItem(
        onClick = {},
        icon = {
            Icon(
                imageVector = Icons.Default.Grade,
                contentDescription = null
            )
        },
        label = { Text(text = stringResource(id = R.string.main_grades)) },
        screen = Screen.GradesScreen
    )
)

val navBar = @Composable { expanded: Boolean ->
    AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(tween(250)),
        exit = shrinkVertically(tween(250))
    ) {
        NavigationBar {
            navBarItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == 0,
                    onClick = item.onClick,
                    icon = item.icon,
                    label = item.label
                )
            }
        }
    }
}

@Preview
@Composable
private fun NavBarPreview() {
    navBar(true)
}