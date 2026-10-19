package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.ReporteProblemaEntity
import cl.duoc.huertoescolarapp.data.repository.HuertoRepository
import cl.duoc.huertoescolarapp.util.Validadores
import cl.duoc.huertoescolarapp.util.fechaHoy
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReporteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HuertoRepository(AppDatabase.getDatabase(application))

    val estadosAtencion = listOf("Pendiente", "En revisión", "Resuelto")

    val listaReportes: StateFlow<List<ReporteProblemaEntity>> = repository.reportes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listaSectores = repository.sectores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listaCultivos = repository.cultivos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listaUsuarios = repository.usuarios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Campos del formulario de reporte
    var sectorIdSeleccionado by mutableStateOf<Long?>(null)
    var cultivoIdSeleccionado by mutableStateOf<Long?>(null)
    var descripcion by mutableStateOf("")
    var fotoRuta by mutableStateOf("")

    var errorSector by mutableStateOf<String?>(null)
    var errorDescripcion by mutableStateOf<String?>(null)

    var mensaje by mutableStateOf<String?>(null)
    var mensajeExito by mutableStateOf(true)

    private fun avisar(texto: String, exito: Boolean) {
        mensaje = texto
        mensajeExito = exito
    }

    fun nuevoReporte() {
        sectorIdSeleccionado = null
        cultivoIdSeleccionado = null
        descripcion = ""
        fotoRuta = ""
        errorSector = null
        errorDescripcion = null
        mensaje = null
    }

    fun limpiarMensaje() {
        mensaje = null
    }

    // El administrador ve todos los reportes; el estudiante solo los que él mismo informó
    fun reportesVisibles(reportes: List<ReporteProblemaEntity>): List<ReporteProblemaEntity> {
        val usuario = SesionActual.usuario ?: return emptyList()
        return if (usuario.rol == "Administrador") reportes else reportes.filter { it.reportanteId == usuario.id }
    }

    fun guardarReporte() {
        val usuario = SesionActual.usuario
        if (usuario == null) {
            avisar("Debes iniciar sesión para reportar un problema.", false)
            return
        }
        errorSector = Validadores.seleccion(sectorIdSeleccionado, "Selecciona el sector donde ocurre el problema.")
        errorDescripcion = Validadores.texto(descripcion, "La descripción del problema", 10, 200)
        if (errorSector != null || errorDescripcion != null) {
            avisar("Revisa los campos marcados en rojo.", false)
            return
        }
        val sectorId = sectorIdSeleccionado!!
        // Si se eligió un cultivo, debe pertenecer al sector elegido
        val cultivoId = cultivoIdSeleccionado
        if (cultivoId != null && listaCultivos.value.none { it.id == cultivoId && it.sectorId == sectorId }) {
            avisar("El cultivo elegido no pertenece a ese sector.", false)
            return
        }
        viewModelScope.launch {
            try {
                repository.guardarReporte(
                    ReporteProblemaEntity(
                        sectorId = sectorId,
                        cultivoId = cultivoId,
                        reportanteId = usuario.id,
                        descripcion = descripcion.trim(),
                        fecha = fechaHoy(),
                        estadoAtencion = "Pendiente",
                        fotoRuta = fotoRuta
                    )
                )
                nuevoReporte()
                avisar("¡Reporte enviado con éxito!", true)
            } catch (e: Exception) {
                avisar("No se pudo guardar el reporte. Intenta nuevamente.", false)
            }
        }
    }

    // Solo el administrador puede cambiar el estado de atención de un problema
    fun actualizarEstado(reporte: ReporteProblemaEntity, nuevoEstado: String) {
        if (!SesionActual.esAdmin()) {
            avisar("Solo el administrador puede cambiar el estado de un problema.", false)
            return
        }
        viewModelScope.launch {
            try {
                repository.actualizarReporte(reporte.copy(estadoAtencion = nuevoEstado))
                avisar("Estado actualizado a \"$nuevoEstado\".", true)
            } catch (e: Exception) {
                avisar("No se pudo actualizar el estado.", false)
            }
        }
    }
}
