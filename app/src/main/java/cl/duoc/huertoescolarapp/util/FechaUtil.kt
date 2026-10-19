package cl.duoc.huertoescolarapp.util

import java.util.Calendar

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
