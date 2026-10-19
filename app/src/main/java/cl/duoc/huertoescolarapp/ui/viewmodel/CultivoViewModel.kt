package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.CultivoEntity
import cl.duoc.huertoescolarapp.data.repository.HuertoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CultivoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HuertoRepository(AppDatabase.getDatabase(application))

    // Campos del formulario
    var nombreCultivo by mutableStateOf("")
    var fechaSiembra by mutableStateOf("")
    var fechaCosecha by mutableStateOf("")
    var estadoDesarrollo by mutableStateOf("Germinación")
    var frecuenciaRiego by mutableStateOf("")
    var sectorIdSeleccionado by mutableStateOf<Long?>(null)

    // Estados de error y éxito
    var errorNombre by mutableStateOf(false)
    var errorFechas by mutableStateOf(false)
    var errorFrecuencia by mutableStateOf(false)
    var registroExitoso by mutableStateOf(false)

    // Listado general de sectores para asignar en el selector
    val listaSectores = repository.sectores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun validarYGuardar() {
        errorNombre = nombreCultivo.isBlank()
        // Validación de negocio: campos completos
        errorFechas = fechaSiembra.isBlank() || fechaCosecha.isBlank()
        // La frecuencia de riego debe ser un número mayor a 0
        val frecuencia = frecuenciaRiego.toIntOrNull() ?: 0
        errorFrecuencia = frecuencia <= 0

        if (!errorNombre && !errorFechas && !errorFrecuencia && sectorIdSeleccionado != null) {
            viewModelScope.launch {
                val nuevoCultivo = CultivoEntity(
                    sectorId = sectorIdSeleccionado!!,
                    nombreCultivo = nombreCultivo,
                    fechaSiembra = fechaSiembra,
                    fechaCosechaEstimada = fechaCosecha,
                    estadoDesarrollo = estadoDesarrollo,
                    frecuenciaRiegoDias = frecuencia
                )
                repository.guardarCultivo(nuevoCultivo)
                registroExitoso = true

                // Limpiar campos
                nombreCultivo = ""
                fechaSiembra = ""
                fechaCosecha = ""
                frecuenciaRiego = ""
            }
        } else {
            registroExitoso = false
        }
    }
}
