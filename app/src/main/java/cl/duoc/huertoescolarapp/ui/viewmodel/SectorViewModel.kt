package cl.duoc.huertoescolarapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SectorViewModel : ViewModel() {
    var nombre by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var ubicacion by mutableStateOf("")

    var errorNombre by mutableStateOf(false)
    var errorUbicacion by mutableStateOf(false)
    var registroExitoso by mutableStateOf(false)

    fun validarYGuardar() {
        errorNombre = nombre.isBlank()
        errorUbicacion = ubicacion.isBlank()

        if (!errorNombre && !errorUbicacion) {
            registroExitoso = true
        } else {
            registroExitoso = false
        }
    }
}