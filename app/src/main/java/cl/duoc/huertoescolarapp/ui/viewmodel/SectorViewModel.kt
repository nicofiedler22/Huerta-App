package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.CultivoEntity
import cl.duoc.huertoescolarapp.data.entity.SectorEntity
import cl.duoc.huertoescolarapp.data.repository.HuertoRepository
import cl.duoc.huertoescolarapp.util.Validadores
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SectorViewModel(application: Application) : AndroidViewModel(application) {

    // El ViewModel habla con el Repository, no directo con el DAO
    private val repository = HuertoRepository(AppDatabase.getDatabase(application))

    // Convertimos el Flow de la base de datos en un StateFlow para que la UI lo observe de forma segura
    val listaSectores: StateFlow<List<SectorEntity>> = repository.sectores
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Si tiene valor, el formulario está editando ese sector
    var idEdicion by mutableStateOf<Long?>(null)

    // Campos del formulario
    var nombre by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var ubicacion by mutableStateOf("")
    var estado by mutableStateOf("Activo")

    // Mensajes de error debajo de cada campo
    var errorNombre by mutableStateOf<String?>(null)
    var errorUbicacion by mutableStateOf<String?>(null)
    var errorDescripcion by mutableStateOf<String?>(null)

    // Confirmación o error de la operación
    var mensaje by mutableStateOf<String?>(null)
    var mensajeExito by mutableStateOf(true)

    private fun avisar(texto: String, exito: Boolean) {
        mensaje = texto
        mensajeExito = exito
    }

    private fun limpiarFormulario() {
        idEdicion = null
        nombre = ""
        ubicacion = ""
        descripcion = ""
        estado = "Activo"
        errorNombre = null
        errorUbicacion = null
        errorDescripcion = null
    }

    fun nuevoSector() {
        limpiarFormulario()
        mensaje = null
    }

    fun editarSector(sector: SectorEntity) {
        limpiarFormulario()
        mensaje = null
        idEdicion = sector.id
        nombre = sector.nombre
        ubicacion = sector.ubicacion
        descripcion = sector.descripcion
        estado = sector.estado
    }

    fun limpiarMensaje() {
        mensaje = null
    }

    fun validarYGuardar() {
        if (!SesionActual.esAdmin()) {
            avisar("No tienes autorización para gestionar sectores.", false)
            return
        }
        errorNombre = Validadores.texto(nombre, "El nombre del sector", 3, 40)
        errorUbicacion = Validadores.texto(ubicacion, "La ubicación", 3, 60)
        errorDescripcion = Validadores.textoOpcional(descripcion, "La descripción", 120)

        if (errorNombre != null || errorUbicacion != null || errorDescripcion != null) {
            avisar("Revisa los campos marcados en rojo.", false)
            return
        }

        val idActual = idEdicion
        viewModelScope.launch {
            try {
                if (idActual == null) {
                    repository.guardarSector(
                        SectorEntity(nombre = nombre.trim(), ubicacion = ubicacion.trim(), descripcion = descripcion.trim(), estado = estado)
                    )
                    limpiarFormulario()
                    avisar("¡Sector guardado con éxito!", true)
                } else {
                    repository.actualizarSector(
                        SectorEntity(id = idActual, nombre = nombre.trim(), ubicacion = ubicacion.trim(), descripcion = descripcion.trim(), estado = estado)
                    )
                    limpiarFormulario()
                    avisar("¡Sector actualizado con éxito!", true)
                }
            } catch (e: Exception) {
                avisar("No se pudo guardar el sector. Intenta nuevamente.", false)
            }
        }
    }

    fun eliminarSector(sector: SectorEntity) {
        if (!SesionActual.esAdmin()) {
            avisar("No tienes autorización para eliminar sectores.", false)
            return
        }
        viewModelScope.launch {
            try {
                repository.eliminarSector(sector)
                avisar("Sector eliminado junto con sus cultivos y tareas.", true)
            } catch (e: Exception) {
                avisar("No se pudo eliminar el sector.", false)
            }
        }
    }

    // Datos para el detalle de un sector
    fun sectorPorId(id: Long): Flow<SectorEntity?> = repository.obtenerSectorPorId(id)

    fun cultivosDelSector(id: Long): Flow<List<CultivoEntity>> = repository.obtenerCultivosPorSector(id)
}
