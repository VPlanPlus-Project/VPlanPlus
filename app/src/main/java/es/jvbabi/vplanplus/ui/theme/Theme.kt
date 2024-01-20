package es.jvbabi.vplanplus.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import es.jvbabi.vplanplus.ui.theme.color.amber_dark
import es.jvbabi.vplanplus.ui.theme.color.amber_light
import es.jvbabi.vplanplus.ui.theme.color.blue_dark
import es.jvbabi.vplanplus.ui.theme.color.blue_light
import es.jvbabi.vplanplus.ui.theme.color.bluegray_dark
import es.jvbabi.vplanplus.ui.theme.color.bluegray_light
import es.jvbabi.vplanplus.ui.theme.color.brown_dark
import es.jvbabi.vplanplus.ui.theme.color.brown_light
import es.jvbabi.vplanplus.ui.theme.color.cyan_dark
import es.jvbabi.vplanplus.ui.theme.color.cyan_light
import es.jvbabi.vplanplus.ui.theme.color.deeporange_dark
import es.jvbabi.vplanplus.ui.theme.color.deeporange_light
import es.jvbabi.vplanplus.ui.theme.color.deeppurple_dark
import es.jvbabi.vplanplus.ui.theme.color.deeppurple_light
import es.jvbabi.vplanplus.ui.theme.color.gray_dark
import es.jvbabi.vplanplus.ui.theme.color.gray_light
import es.jvbabi.vplanplus.ui.theme.color.green_dark
import es.jvbabi.vplanplus.ui.theme.color.green_light
import es.jvbabi.vplanplus.ui.theme.color.indigo_dark
import es.jvbabi.vplanplus.ui.theme.color.indigo_light
import es.jvbabi.vplanplus.ui.theme.color.lightBlue_dark
import es.jvbabi.vplanplus.ui.theme.color.lightBlue_light
import es.jvbabi.vplanplus.ui.theme.color.lightgreen_dark
import es.jvbabi.vplanplus.ui.theme.color.lightgreen_light
import es.jvbabi.vplanplus.ui.theme.color.lime_dark
import es.jvbabi.vplanplus.ui.theme.color.lime_light
import es.jvbabi.vplanplus.ui.theme.color.orange_dark
import es.jvbabi.vplanplus.ui.theme.color.orange_light
import es.jvbabi.vplanplus.ui.theme.color.pink_dark
import es.jvbabi.vplanplus.ui.theme.color.pink_light
import es.jvbabi.vplanplus.ui.theme.color.purple_dark
import es.jvbabi.vplanplus.ui.theme.color.purple_light
import es.jvbabi.vplanplus.ui.theme.color.red_dark
import es.jvbabi.vplanplus.ui.theme.color.red_light
import es.jvbabi.vplanplus.ui.theme.color.teal_dark
import es.jvbabi.vplanplus.ui.theme.color.teal_light
import es.jvbabi.vplanplus.ui.theme.color.yellow_dark
import es.jvbabi.vplanplus.ui.theme.color.yellow_light
import es.jvbabi.vplanplus.domain.usecase.home.Colors as CustomColorScheme


@Composable
fun VPlanPlusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    cs: CustomColorScheme,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        cs == CustomColorScheme.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        !darkTheme -> {
            when (cs) {
                CustomColorScheme.AMBER -> amber_light
                CustomColorScheme.BLUE -> blue_light
                CustomColorScheme.BLUE_GREY -> bluegray_light
                CustomColorScheme.BROWN -> brown_light
                CustomColorScheme.CYAN -> cyan_light
                CustomColorScheme.DEEP_ORANGE -> deeporange_light
                CustomColorScheme.DEEP_PURPLE -> deeppurple_light
                CustomColorScheme.GREEN -> green_light
                CustomColorScheme.GREY -> gray_light
                CustomColorScheme.INDIGO -> indigo_light
                CustomColorScheme.LIGHT_BLUE -> lightBlue_light
                CustomColorScheme.LIGHT_GREEN -> lightgreen_light
                CustomColorScheme.LIME -> lime_light
                CustomColorScheme.ORANGE -> orange_light
                CustomColorScheme.PINK -> pink_light
                CustomColorScheme.PURPLE -> purple_light
                CustomColorScheme.RED -> red_light
                CustomColorScheme.TEAL -> teal_light
                CustomColorScheme.YELLOW -> yellow_light
                CustomColorScheme.DYNAMIC -> red_light
            }
        }
        else -> when (cs) {
            CustomColorScheme.AMBER -> amber_dark
            CustomColorScheme.BLUE -> blue_dark
            CustomColorScheme.BLUE_GREY -> bluegray_dark
            CustomColorScheme.BROWN -> brown_dark
            CustomColorScheme.CYAN -> cyan_dark
            CustomColorScheme.DEEP_ORANGE -> deeporange_dark
            CustomColorScheme.DEEP_PURPLE -> deeppurple_dark
            CustomColorScheme.GREEN -> green_dark
            CustomColorScheme.GREY -> gray_dark
            CustomColorScheme.INDIGO -> indigo_dark
            CustomColorScheme.LIGHT_BLUE -> lightBlue_dark
            CustomColorScheme.LIGHT_GREEN -> lightgreen_dark
            CustomColorScheme.LIME -> lime_dark
            CustomColorScheme.ORANGE -> orange_dark
            CustomColorScheme.PINK -> pink_dark
            CustomColorScheme.PURPLE -> purple_dark
            CustomColorScheme.RED -> red_dark
            CustomColorScheme.TEAL -> teal_dark
            CustomColorScheme.YELLOW -> yellow_dark
            CustomColorScheme.DYNAMIC -> red_dark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}