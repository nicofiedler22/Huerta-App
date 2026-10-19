package cl.duoc.huertoescolarapp.ui.theme

import androidx.compose.ui.graphics.Color

// ---------- Paleta del huerto (modo claro) ----------
val VerdePrimario = Color(0xFF2E7D32)
val VerdeContenedor = Color(0xFFC8E6C9)
val VerdeSecundario = Color(0xFF5F7A52)
val TierraTerciario = Color(0xFF8D6E3F)
val FondoClaro = Color(0xFFFBFDF7)
val TextoClaro = Color(0xFF1A1C19)
val SuperficieVariantClaro = Color(0xFFDDE5D8)
val TextoVariantClaro = Color(0xFF41493F)
val BordeClaro = Color(0xFF72796F)

// ---------- Paleta del huerto (modo oscuro) ----------
val VerdePrimarioOscuro = Color(0xFF81C784)
val VerdeContenedorOscuro = Color(0xFF1B5E20)
val VerdeSecundarioOscuro = Color(0xFFA8C69A)
val TierraTerciarioOscuro = Color(0xFFE0C28F)
val FondoOscuro = Color(0xFF121411)
val TextoOscuro = Color(0xFFE2E3DD)
val SuperficieVariantOscuro = Color(0xFF41493F)
val TextoVariantOscuro = Color(0xFFC1C9BD)
val BordeOscuro = Color(0xFF8B9389)

// ---------- Colores semánticos ----------
val ColorExito = Color(0xFF388E3C)
val ColorAdvertencia = Color(0xFFEF8F00)
val ColorInfo = Color(0xFF1E88E5)
val ColorError = Color(0xFFD32F2F)

// Estados de las tareas
val ColorTareaPendiente = Color(0xFF5C6BC0)
val ColorTareaEnDesarrollo = ColorInfo
val ColorTareaPorValidar = ColorAdvertencia
val ColorTareaValidada = ColorExito
val ColorTareaRechazada = ColorError

// Estados de los cultivos
val ColorCultivoGerminacion = Color(0xFF8E8E2E)
val ColorCultivoCrecimiento = ColorExito
val ColorCultivoCosecha = ColorAdvertencia

// Estados de los reportes de problemas
val ColorReportePendiente = ColorError
val ColorReporteEnRevision = ColorAdvertencia
val ColorReporteResuelto = ColorExito

// Estados de los sectores
val ColorSectorActivo = ColorExito
val ColorSectorEnDescanso = Color(0xFF757575)
