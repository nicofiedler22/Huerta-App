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
import cl.duoc.huertoescolarapp.util.Validadores
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CultivoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HuertoRepository(AppDatabase.getDatabase(application))

    // Si tiene valor, el formulario está editando ese cultivo
    var idEdicion by mutableStateOf<Long?>(null)

    // Campos del formulario
    var nombreCultivo by mutableStateOf("")
    var fechaSiembra by mutableStateOf("")
    var fechaCosecha by mutableStateOf("")
    var estadoDesarrollo by mutableStateOf("Germinación")
    var frecuenciaRiego by mutableStateOf("")
    var sectorIdSeleccionado by mutableStateOf<Long?>(null)

    // Mensajes de error debajo de cada campo
    var errorSector by mutableStateOf<String?>(null)
    var errorNombre by mutableStateOf<String?>(null)
    var errorSiembra by mutableStateOf<String?>(null)
    var errorCosecha by mutableStateOf<String?>(null)
    var errorFrecuencia by mutableStateOf<String?>(null)

    // Confirmación o error de la operación
    var mensaje by mutableStateOf<String?>(null)
    var mensajeExito by mutableStateOf(true)

    // Listado general de sectores para asignar en el selector
    val listaSectores = repository.sectores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun avisar(texto: String, exito: Boolean) {
        mensaje = texto
        mensajeExito = exito
    }

    private fun limpiarCampos() {
        idEdicion = null
        nombreCultivo = ""
        fechaSiembra = ""
        fechaCosecha = ""
        estadoDesarrollo = "Germinación"
        frecuenciaRiego = ""
        errorSector = null
        errorNombre = null
        errorSiembra = null
        errorCosecha = null
        errorFrecuencia = null
    }

    fun nuevoCultivo(sectorId: Long? = null) {
        limpiarCampos()
        mensaje = null
        sectorIdSeleccionado = sectorId
    }

    fun editarCultivo(cultivo: CultivoEntity) {
        limpiarCampos()
        mensaje = null
        idEdicion = cultivo.id
        sectorIdSeleccionado = cultivo.sectorId
        nombreCultivo = cultivo.nombreCultivo
        fechaSiembra = cultivo.fechaSiembra
        fechaCosecha = cultivo.fechaCosechaEstimada
        estadoDesarrollo = cultivo.estadoDesarrollo
        frecuenciaRiego = cultivo.frecuenciaRiegoDias.toString()
    }

    fun limpiarMensaje() {
        mensaje = null
    }

    fun validarYGuardar() {
        if (!SesionActual.esAdmin()) {
            avisar("No tienes autorización para gestionar cultivos.", false)
            return
        }
        errorSector = Validadores.seleccion(sectorIdSeleccionado, "Selecciona un sector.")
        if (errorSector == null && listaSectores.value.none { it.id == sectorIdSeleccionado }) {
            errorSector = "El sector seleccionado no existe."
        }
        errorNombre = Validadores.texto(nombreCultivo, "El nombre del cultivo", 2, 40)
        errorSiembra = Validadores.fecha(fechaSiembra, "La fecha de siembra es obligatoria.")
        errorCosecha = Validadores.fecha(fechaCosecha, "La fecha de cosecha estimada es obligatoria.")
            ?: Validadores.coherenciaCosecha(fechaSiembra, fechaCosecha)
        errorFrecuencia = Validadores.frecuenciaRiego(frecuenciaRiego)

        if (errorSector != null || errorNombre != null || errorSiembra != null ||
            errorCosecha != null || errorFrecuencia != null
        ) {
            avisar("Revisa los campos marcados en rojo.", false)
            return
        }

        val sectorId = sectorIdSeleccionado!!
        val frecuencia = frecuenciaRiego.trim().toInt()
        val idActual = idEdicion
        viewModelScope.launch {
            try {
                if (idActual == null) {
                    repository.guardarCultivo(
                        CultivoEntity(
                            sectorId = sectorId,
                            nombreCultivo = nombreCultivo.trim(),
                            fechaSiembra = fechaSiembra,
                            fechaCosechaEstimada = fechaCosecha,
                            estadoDesarrollo = estadoDesarrollo,
                            frecuenciaRiegoDias = frecuencia
                        )
                    )
                    limpiarCampos()
                    sectorIdSeleccionado = sectorId
                    avisar("¡Cultivo registrado con éxito!", true)
                } else {
                    repository.actualizarCultivo(
                        CultivoEntity(
                            id = idActual,
                            sectorId = sectorId,
                            nombreCultivo = nombreCultivo.trim(),
                            fechaSiembra = fechaSiembra,
                            fechaCosechaEstimada = fechaCosecha,
                            estadoDesarrollo = estadoDesarrollo,
                            frecuenciaRiegoDias = frecuencia
                        )
                    )
                    limpiarCampos()
                    sectorIdSeleccionado = sectorId
                    avisar("¡Cultivo actualizado con éxito!", true)
                }
            } catch (e: Exception) {
                avisar("No se pudo guardar el cultivo. Intenta nuevamente.", false)
            }
        }
    }

    fun eliminarCultivo(cultivo: CultivoEntity) {
        if (!SesionActual.esAdmin()) {
            avisar("No tienes autorización para eliminar cultivos.", false)
            return
        }
        viewModelScope.launch {
            try {
                repository.eliminarCultivo(cultivo)
                avisar("Cultivo eliminado.", true)
            } catch (e: Exception) {
                avisar("No se pudo eliminar el cultivo.", false)
            }
        }
    }
}
