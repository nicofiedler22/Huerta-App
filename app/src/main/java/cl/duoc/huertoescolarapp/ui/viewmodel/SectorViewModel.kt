package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.SectorEntity
import kotlinx.coroutines.launch


class SectorViewModel(application: Application) : AndroidViewModel(application) {

    // Obtenemos la instancia del DAO de SQLite a través de Room
    private val sectorDao = AppDatabase.getDatabase(application).sectorDao()

    // Campos del formulario
    var nombre by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var ubicacion by mutableStateOf("")

    // Estados de error para validaciones
    var errorNombre by mutableStateOf(false)
    var errorUbicacion by mutableStateOf(false)
    var registroExitoso by mutableStateOf(false)

    fun validarYGuardar() {
        errorNombre = nombre.isBlank()
        errorUbicacion = ubicacion.isBlank()

        if (!errorNombre && !errorUbicacion) {
            viewModelScope.launch {
                val nuevoSector = SectorEntity(
                    nombre = nombre,
                    ubicacion = ubicacion,
                    descripcion = descripcion
                )
                sectorDao.insertarSector(nuevoSector)
                registroExitoso = true
            }
        } else {
            registroExitoso = false
        }
    }
}