package cl.duoc.huertoescolarapp.util

// Toda la lógica de validación vive aquí (fuera de los Composables).
// Cada función devuelve el mensaje de error, o null si el dato es válido.
object Validadores {

    private val regexCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun obligatorio(valor: String, campo: String): String? {
        return if (valor.isBlank()) "$campo es obligatoria." else null
    }

    fun texto(valor: String, campo: String, min: Int, max: Int): String? {
        val limpio = valor.trim()
        if (limpio.isEmpty()) return "$campo es obligatorio."
        if (limpio.length < min) return "$campo debe tener al menos $min caracteres."
        if (limpio.length > max) return "$campo no puede superar los $max caracteres."
        return null
    }

    fun textoOpcional(valor: String, campo: String, max: Int): String? {
        return if (valor.trim().length > max) "$campo no puede superar los $max caracteres." else null
    }

    fun correo(valor: String): String? {
        if (valor.isBlank()) return "El correo es obligatorio."
        if (!regexCorreo.matches(valor.trim())) return "Ingresa un correo válido (ej. nombre@huerto.cl)."
        return null
    }

    fun seleccion(id: Long?, mensaje: String): String? {
        return if (id == null) mensaje else null
    }

    fun fecha(valor: String, mensajeVacio: String): String? {
        if (valor.isBlank()) return mensajeVacio
        if (parsearFecha(valor) == null) return "El formato de la fecha no es válido."
        return null
    }

    // La cosecha estimada no puede ser anterior a la siembra
    fun coherenciaCosecha(siembra: String, cosecha: String): String? {
        val inicio = parsearFecha(siembra) ?: return null
        val fin = parsearFecha(cosecha) ?: return null
        return if (fin < inicio) "La cosecha estimada no puede ser anterior a la siembra." else null
    }

    fun frecuenciaRiego(valor: String): String? {
        if (valor.isBlank()) return "La frecuencia de riego es obligatoria."
        val numero = valor.trim().toIntOrNull() ?: return "Debe ser un número entero."
        if (numero <= 0) return "Debe ser mayor a 0."
        if (numero > 365) return "No puede superar los 365 días."
        return null
    }

    fun motivoRechazo(valor: String): String? {
        if (valor.isBlank()) return "Debes indicar el motivo del rechazo."
        if (valor.trim().length < 5) return "El motivo debe tener al menos 5 caracteres."
        return null
    }
}
