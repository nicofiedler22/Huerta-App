package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.CultivoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CultivoViewModel(application: Application) : AndroidViewModel(application) {

    private val cultivoDao = AppDatabase.getDatabase(application).cultivoDao()
    val sectorDao = AppDatabase.getDatabase(application).sectorDao()

    // Campos del formulario
    var nombreCultivo by mutableStateOf("")
    var fechaSiembra by mutableStateOf("")
    var fechaCosecha by mutableStateOf("")
    var estadoDesarrollo by mutableStateOf("Germinación")
    var sectorIdSeleccionado by mutableStateOf<Long?>(null)

    // Estados de error y éxito
    var errorNombre by mutableStateOf(false)
    var errorFechas by mutableStateOf(false)
    var registroExitoso by mutableStateOf(false)

    // Listado general de sectores para asignar en el selector
    val listaSectores = sectorDao.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun validarYGuardar() {
        errorNombre = nombreCultivo.isBlank()
        // Validación de negocio: campos completos
        errorFechas = fechaSiembra.isBlank() || fechaCosecha.isBlank()

        if (!errorNombre && !errorFechas && sectorIdSeleccionado != null) {
            viewModelScope.launch {
                val nuevoCultivo = CultivoEntity(
                    sectorId = sectorIdSeleccionado!!,
                    nombreCultivo = nombreCultivo,
                    fechaSiembra = fechaSiembra,
                    fechaCosechaEstimada = fechaCosecha,
                    estadoDesarrollo = estadoDesarrollo
                )
                cultivoDao.insertarCultivo(nuevoCultivo)
                registroExitoso = true

                // Limpiar campos
                nombreCultivo = ""
                fechaSiembra = ""
                fechaCosecha = ""
            }
        } else {
            registroExitoso = false
        }
    }
}