package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.UsuarioEntity
import cl.duoc.huertoescolarapp.data.repository.HuertoRepository
import cl.duoc.huertoescolarapp.util.Validadores
import kotlinx.coroutines.launch

class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HuertoRepository(AppDatabase.getDatabase(application))

    // Usuario que inició sesión (null = nadie ha iniciado sesión)
    var usuarioActual by mutableStateOf<UsuarioEntity?>(null)

    // Campos del formulario de login
    var correo by mutableStateOf("")
    var clave by mutableStateOf("")

    // Mensajes de error que se muestran debajo de cada campo
    var errorCorreo by mutableStateOf<String?>(null)
    var errorClave by mutableStateOf<String?>(null)
    var mensajeError by mutableStateOf("")

    init {
        // Carga los usuarios y datos de prueba la primera vez que se abre la app
        viewModelScope.launch {
            repository.sembrarDatosDemo()
        }
    }

    fun iniciarSesion() {
        errorCorreo = Validadores.correo(correo)
        errorClave = Validadores.obligatorio(clave, "La contraseña")
        mensajeError = ""
        if (errorCorreo != null || errorClave != null) return

        viewModelScope.launch {
            val usuario = repository.iniciarSesion(correo.trim(), clave)
            if (usuario == null) {
                mensajeError = "Correo o contraseña incorrectos."
            } else {
                clave = ""
                SesionActual.usuario = usuario
                usuarioActual = usuario
            }
        }
    }

    fun cerrarSesion() {
        SesionActual.usuario = null
        usuarioActual = null
        correo = ""
        clave = ""
        mensajeError = ""
        errorCorreo = null
        errorClave = null
    }
}
