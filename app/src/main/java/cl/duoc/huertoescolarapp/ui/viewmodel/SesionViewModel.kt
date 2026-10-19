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
import kotlinx.coroutines.launch

class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HuertoRepository(AppDatabase.getDatabase(application))

    // Usuario que inició sesión (null = nadie ha iniciado sesión)
    var usuarioActual by mutableStateOf<UsuarioEntity?>(null)

    // Campos del formulario de login
    var correo by mutableStateOf("")
    var clave by mutableStateOf("")

    // Estados de error
    var errorCorreo by mutableStateOf(false)
    var errorClave by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    init {
        // Carga los usuarios y datos de prueba la primera vez que se abre la app
        viewModelScope.launch {
            repository.sembrarDatosDemo()
        }
    }

    fun iniciarSesion() {
        errorCorreo = correo.isBlank()
        errorClave = clave.isBlank()

        if (errorCorreo || errorClave) {
            mensajeError = "Completa el correo y la contraseña."
        } else {
            viewModelScope.launch {
                val usuario = repository.iniciarSesion(correo.trim(), clave)
                if (usuario == null) {
                    mensajeError = "Correo o contraseña incorrectos."
                } else {
                    mensajeError = ""
                    clave = ""
                    usuarioActual = usuario
                }
            }
        }
    }

    fun cerrarSesion() {
        usuarioActual = null
        correo = ""
        clave = ""
        mensajeError = ""
    }
}
