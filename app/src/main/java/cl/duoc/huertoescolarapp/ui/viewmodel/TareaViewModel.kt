package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.TareaEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TareaViewModel(application: Application) : AndroidViewModel(application) {

    private val tareaDao = AppDatabase.getDatabase(application).tareaDao()
    val sectorDao = AppDatabase.getDatabase(application).sectorDao()

    // Campos del formulario de creación (Admin)
    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var tipo by mutableStateOf("Riego")
    var fechaProgramada by mutableStateOf("")
    var responsable by mutableStateOf("")
    var sectorIdSeleccionado by mutableStateOf<Long?>(null)

    // Estados de error y éxito para creación
    var errorTitulo by mutableStateOf(false)
    var errorResponsable by mutableStateOf(false)
    var registroExitoso by mutableStateOf(false)

    // Listas reactivas
    val listaSectores = sectorDao.obtenerSectores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listaTareas = tareaDao.obtenerTodasLasTareas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 📌 Crear nueva tarea (Admin)
    fun validarYGuardar() {
        errorTitulo = titulo.isBlank()
        errorResponsable = responsable.isBlank()

        if (!errorTitulo && !errorResponsable && sectorIdSeleccionado != null && fechaProgramada.isNotBlank()) {
            viewModelScope.launch {
                val nuevaTarea = TareaEntity(
                    sectorId = sectorIdSeleccionado!!,
                    titulo = titulo,
                    descripcion = descripcion,
                    tipo = tipo,
                    fechaProgramada = fechaProgramada,
                    responsable = responsable,
                    estado = "Pendiente"
                )
                tareaDao.insertarTarea(nuevaTarea)
                registroExitoso = true
                titulo = ""
                descripcion = ""
                fechaProgramada = ""
                responsable = ""
            }
        } else {
            registroExitoso = false
        }
    }

    // 📌 Flujo del Estudiante: Cambiar estado (En desarrollo / Pendiente de validación) y añadir comentarios
    fun actualizarEstadoEstudiante(tarea: TareaEntity, nuevoEstado: String, comentarios: String) {
        viewModelScope.launch {
            val tareaActualizada = tarea.copy(
                estado = nuevoEstado,
                comentariosEstudiante = comentarios
            )
            tareaDao.actualizarTarea(tareaActualizada)
        }
    }

    // 📌 Flujo del Administrador: Validar (Aprobar) o Rechazar tarea (Exige motivo)
    fun revisarTareaAdmin(tarea: TareaEntity, aprobado: Boolean, motivo: String) {
        viewModelScope.launch {
            val estadoFinal = if (aprobado) "Validada" else "Rechazada"
            val tareaActualizada = tarea.copy(
                estado = estadoFinal,
                motivoRechazo = if (!aprobado) motivo else ""
            )
            tareaDao.actualizarTarea(tareaActualizada)
        }
    }
}