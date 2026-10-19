package cl.duoc.huertoescolarapp.ui.theme

import androidx.compose.ui.graphics.Color

// Devuelven el color semántico de cada estado, así las pantallas no repiten colores

fun colorEstadoTarea(estado: String): Color = when (estado) {
    "Pendiente" -> ColorTareaPendiente
    "En desarrollo" -> ColorTareaEnDesarrollo
    "Pendiente de validación" -> ColorTareaPorValidar
    "Validada" -> ColorTareaValidada
    "Rechazada" -> ColorTareaRechazada
    else -> ColorTareaPendiente
}

fun colorEstadoCultivo(estado: String): Color = when (estado) {
    "Germinación" -> ColorCultivoGerminacion
    "Crecimiento" -> ColorCultivoCrecimiento
    "Cosecha" -> ColorCultivoCosecha
    else -> ColorCultivoCrecimiento
}

fun colorEstadoReporte(estado: String): Color = when (estado) {
    "Pendiente" -> ColorReportePendiente
    "En revisión" -> ColorReporteEnRevision
    "Resuelto" -> ColorReporteResuelto
    else -> ColorReportePendiente
}

fun colorEstadoSector(estado: String): Color = when (estado) {
    "Activo" -> ColorSectorActivo
    else -> ColorSectorEnDescanso
}
