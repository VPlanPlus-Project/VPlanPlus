package es.jvbabi.vplanplus.domain.usecase.settings.general

import androidx.compose.ui.graphics.Color
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.usecase.home.Colors
import es.jvbabi.vplanplus.domain.usecase.home.GetColorSchemeUseCase
import es.jvbabi.vplanplus.ui.theme.color.amber_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.amber_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.blue_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.blue_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.bluegray_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.bluegray_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.brown_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.brown_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.cyan_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.cyan_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.deeporange_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.deeporange_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.deeppurple_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.deeppurple_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.gray_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.gray_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.green_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.green_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.indigo_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.indigo_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.lightblue_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.lightblue_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.lightgreen_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.lightgreen_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.lime_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.lime_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.orange_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.orange_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.pink_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.pink_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.purple_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.purple_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.red_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.red_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.teal_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.teal_md_theme_light_primary
import es.jvbabi.vplanplus.ui.theme.color.yellow_md_theme_dark_primary
import es.jvbabi.vplanplus.ui.theme.color.yellow_md_theme_light_primary
import kotlinx.coroutines.flow.first


class GetColorsUseCase(
    private val keyValueRepository: KeyValueRepository
) {
    suspend operator fun invoke(darkScheme: Boolean): Map<Colors, ColorScheme> {
        val active = GetColorSchemeUseCase(keyValueRepository).invoke().first()
        return if (darkScheme) {
            mapOf(
                Colors.AMBER to ColorScheme(amber_md_theme_dark_primary, active == Colors.AMBER),
                Colors.BLUE to ColorScheme(blue_md_theme_dark_primary, active == Colors.BLUE),
                Colors.BLUE_GREY to ColorScheme(bluegray_md_theme_dark_primary, active == Colors.BLUE_GREY),
                Colors.BROWN to ColorScheme(brown_md_theme_dark_primary, active == Colors.BROWN),
                Colors.CYAN to ColorScheme(cyan_md_theme_dark_primary, active == Colors.CYAN),
                Colors.DEEP_ORANGE to ColorScheme(deeporange_md_theme_dark_primary, active == Colors.DEEP_ORANGE),
                Colors.DEEP_PURPLE to ColorScheme(deeppurple_md_theme_dark_primary, active == Colors.DEEP_PURPLE),
                Colors.GREEN to ColorScheme(green_md_theme_dark_primary, active == Colors.GREEN),
                Colors.GREY to ColorScheme(gray_md_theme_dark_primary, active == Colors.GREY),
                Colors.INDIGO to ColorScheme(indigo_md_theme_dark_primary, active == Colors.INDIGO),
                Colors.LIGHT_BLUE to ColorScheme(lightblue_md_theme_dark_primary, active == Colors.LIGHT_BLUE),
                Colors.LIGHT_GREEN to ColorScheme(lightgreen_md_theme_dark_primary, active == Colors.LIGHT_GREEN),
                Colors.LIME to ColorScheme(lime_md_theme_dark_primary, active == Colors.LIME),
                Colors.ORANGE to ColorScheme(orange_md_theme_dark_primary, active == Colors.ORANGE),
                Colors.PINK to ColorScheme(pink_md_theme_dark_primary, active == Colors.PINK),
                Colors.PURPLE to ColorScheme(purple_md_theme_dark_primary, active == Colors.PURPLE),
                Colors.RED to ColorScheme(red_md_theme_dark_primary, active == Colors.RED),
                Colors.TEAL to ColorScheme(teal_md_theme_dark_primary, active == Colors.TEAL),
                Colors.YELLOW to ColorScheme(yellow_md_theme_dark_primary, active == Colors.YELLOW)
            )
        } else {
            mapOf(
                Colors.AMBER to ColorScheme(amber_md_theme_light_primary, active == Colors.AMBER),
                Colors.BLUE to ColorScheme(blue_md_theme_light_primary, active == Colors.BLUE),
                Colors.BLUE_GREY to ColorScheme(bluegray_md_theme_light_primary, active == Colors.BLUE_GREY),
                Colors.BROWN to ColorScheme(brown_md_theme_light_primary, active == Colors.BROWN),
                Colors.CYAN to ColorScheme(cyan_md_theme_light_primary, active == Colors.CYAN),
                Colors.DEEP_ORANGE to ColorScheme(deeporange_md_theme_light_primary, active == Colors.DEEP_ORANGE),
                Colors.DEEP_PURPLE to ColorScheme(deeppurple_md_theme_light_primary, active == Colors.DEEP_PURPLE),
                Colors.GREEN to ColorScheme(green_md_theme_light_primary, active == Colors.GREEN),
                Colors.GREY to ColorScheme(gray_md_theme_light_primary, active == Colors.GREY),
                Colors.INDIGO to ColorScheme(indigo_md_theme_light_primary, active == Colors.INDIGO),
                Colors.LIGHT_BLUE to ColorScheme(lightblue_md_theme_light_primary, active == Colors.LIGHT_BLUE),
                Colors.LIGHT_GREEN to ColorScheme(lightgreen_md_theme_light_primary, active == Colors.LIGHT_GREEN),
                Colors.LIME to ColorScheme(lime_md_theme_light_primary, active == Colors.LIME),
                Colors.ORANGE to ColorScheme(orange_md_theme_light_primary, active == Colors.ORANGE),
                Colors.PINK to ColorScheme(pink_md_theme_light_primary, active == Colors.PINK),
                Colors.PURPLE to ColorScheme(purple_md_theme_light_primary, active == Colors.PURPLE),
                Colors.RED to ColorScheme(red_md_theme_light_primary, active == Colors.RED),
                Colors.TEAL to ColorScheme(teal_md_theme_light_primary, active == Colors.TEAL),
                Colors.YELLOW to ColorScheme(yellow_md_theme_light_primary, active == Colors.YELLOW)
            )
        }.plus(
            Colors.DYNAMIC to ColorScheme(null, active == Colors.DYNAMIC)
        )
    }
}

data class ColorScheme(
    val primary: Color?,
    val active: Boolean,
)