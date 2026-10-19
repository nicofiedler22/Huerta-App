package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.TareaEntity
import cl.duoc.huertoescolarapp.data.entity.ValidacionEntity
import cl.duoc.huertoescolarapp.data.repository.HuertoRepository
import cl.duoc.huertoescolarapp.util.fechaHoy
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TareaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HuertoRepository(AppDatabase.getDatabase(application))

    // Campos del formulario de creación (Admin)
    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var tipo by mutableStateOf("Riego")
    var fechaProgramada by mutableStateOf("")
    var responsableIdSeleccionado by mutableStateOf<Long?>(null)
    var sectorIdSeleccionado by mutableStateOf<Long?>(null)

    // Estados de error y éxito para creación
    var errorTitulo by mutableStateOf(false)
    var errorResponsable by mutableStateOf(false)
    var registroExitoso by mutableStateOf(false)

    // Listas reactivas
    val listaSectores = repository.sectores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Solo los estudiantes (para poder asignarles tareas)
    val listaColaboradores = repository.colaboradores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Todos los usuarios (para mostrar el nombre del responsable en las tarjetas)
    val listaUsuarios = repository.usuarios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listaTareas = repository.tareas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listaValidaciones = repository.validaciones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 📌 Crear nueva tarea (Admin)
    fun validarYGuardar() {
        errorTitulo = titulo.isBlank()
        errorResponsable = responsableIdSeleccionado == null

        if (!errorTitulo && !errorResponsable && sectorIdSeleccionado != null && fechaProgramada.isNotBlank()) {
            viewModelScope.launch {
                val nuevaTarea = TareaEntity(
                    sectorId = sectorIdSeleccionado!!,
                    responsableId = responsableIdSeleccionado!!,
                    titulo = titulo,
                    descripcion = descripcion,
                    tipo = tipo,
                    fechaProgramada = fechaProgramada,
                    estado = "Pendiente"
                )
                repository.guardarTarea(nuevaTarea)
                registroExitoso = true
                titulo = ""
                descripcion = ""
                fechaProgramada = ""
                responsableIdSeleccionado = null
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
            repository.actualizarTarea(tareaActualizada)
        }
    }

    // 📌 Flujo del Administrador: Validar (Aprobar) o Rechazar tarea (Exige motivo)
    // Además de cambiar el estado, guarda quién validó, cuándo y el resultado
    fun revisarTareaAdmin(tarea: TareaEntity, aprobado: Boolean, motivo: String, validadorId: Long) {
        viewModelScope.launch {
            val estadoFinal = if (aprobado) "Validada" else "Rechazada"
            repository.actualizarTarea(tarea.copy(estado = estadoFinal))
            repository.guardarValidacion(
                ValidacionEntity(
                    tareaId = tarea.id,
                    validadorId = validadorId,
                    resultado = estadoFinal,
                    motivo = if (!aprobado) motivo else "",
                    fecha = fechaHoy()
                )
            )
        }
    }
}
