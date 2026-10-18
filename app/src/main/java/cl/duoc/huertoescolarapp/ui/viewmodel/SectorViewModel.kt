package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.SectorEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SectorViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del DAO de SQLite a través de Room
    private val sectorDao = AppDatabase.getDatabase(application).sectorDao()

    // 📌 NUEVO: Convertimos el Flow de la base de datos en un StateFlow para que la UI lo observe de forma segura
    val listaSectores: StateFlow<List<SectorEntity>> = sectorDao.obtenerSectores()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

                // Limpiamos los campos después de guardar con éxito
                nombre = ""
                ubicacion = ""
                descripcion = ""
            }
        } else {
            registroExitoso = false
        }
    }
}