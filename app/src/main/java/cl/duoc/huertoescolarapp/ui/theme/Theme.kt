package cl.duoc.huertoescolarapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VerdePrimarioOscuro,
    onPrimary = Color(0xFF00390A),
    primaryContainer = VerdeContenedorOscuro,
    secondary = VerdeSecundarioOscuro,
    onSecondary = Color(0xFF1F3318),
    tertiary = TierraTerciarioOscuro,
    background = FondoOscuro,
    surface = FondoOscuro,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro,
    surfaceVariant = SuperficieVariantOscuro,
    onSurfaceVariant = TextoVariantOscuro,
    outline = BordeOscuro,
    error = Color(0xFFFFB4AB)
)

private val LightColorScheme = lightColorScheme(
    primary = VerdePrimario,
    onPrimary = Color.White,
    primaryContainer = VerdeContenedor,
    secondary = VerdeSecundario,
    onSecondary = Color.White,
    tertiary = TierraTerciario,
    background = FondoClaro,
    surface = FondoClaro,
    onBackground = TextoClaro,
    onSurface = TextoClaro,
    surfaceVariant = SuperficieVariantClaro,
    onSurfaceVariant = TextoVariantClaro,
    outline = BordeClaro,
    error = Color(0xFFBA1A1A)
)

@Composable
fun HuertoEscolarAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Se desactiva el color dinámico para que siempre se vea la paleta verde del huerto
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
