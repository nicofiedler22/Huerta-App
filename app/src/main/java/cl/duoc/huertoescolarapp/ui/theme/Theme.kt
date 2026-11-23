package cl.duoc.huertoescolarapp.ui.theme

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = VerdePrimarioOscuro,
    onPrimary = Color(0xFF05290F),
    primaryContainer = VerdeContenedorOscuro,
    onPrimaryContainer = Color(0xFFBBF7D0),
    secondary = VerdeSecundarioOscuro,
    onSecondary = Color(0xFF052E16),
    secondaryContainer = Color(0xFF1F4D34),
    onSecondaryContainer = Color(0xFFD1FAE5),
    tertiary = TierraTerciarioOscuro,
    background = FondoOscuro,
    surface = SuperficieOscuro,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro,
    surfaceVariant = SuperficieVariantOscuro,
    onSurfaceVariant = TextoVariantOscuro,
    outline = BordeOscuro,
    outlineVariant = Color(0xFF2B4A3A),
    surfaceContainerLowest = Color(0xFF06100A),
    surfaceContainerLow = Color(0xFF0C1F15),
    surfaceContainer = Color(0xFF11291C),
    surfaceContainerHigh = Color(0xFF163423),
    surfaceContainerHighest = Color(0xFF1B3D2A),
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
    darkTheme: Boolean = true, // diseño oscuro verde por defecto
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
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(26.dp)
        ),
        content = content
    )
}
