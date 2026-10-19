package cl.duoc.huertoescolarapp.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Devuelve la fecha de hoy (o de hoy + N días) con el mismo formato de los selectores de fecha: dd/MM/yyyy
fun fechaHoy(diasDesdeHoy: Int = 0): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_MONTH, diasDesdeHoy)
    return String.format(
        "%02d/%02d/%d",
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.YEAR)
    )
}

// Convierte "dd/MM/yyyy" en milisegundos para poder comparar fechas (null si el formato es inválido)
fun parsearFecha(fecha: String): Long? {
    return try {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formato.isLenient = false
        formato.parse(fecha)?.time
    } catch (e: Exception) {
        null
    }
}

// Una tarea está atrasada si su fecha programada ya pasó y todavía no fue entregada ni validada
fun esAtrasada(estado: String, fechaProgramada: String): Boolean {
    if (estado == "Validada" || estado == "Pendiente de validación") return false
    val programada = parsearFecha(fechaProgramada) ?: return false
    val hoy = parsearFecha(fechaHoy()) ?: return false
    return programada < hoy
}
