package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.EvidenciaEntity
import cl.duoc.huertoescolarapp.data.entity.TareaEntity
import cl.duoc.huertoescolarapp.data.entity.UsuarioEntity
import cl.duoc.huertoescolarapp.data.entity.ValidacionEntity
import cl.duoc.huertoescolarapp.data.repository.HuertoRepository
import cl.duoc.huertoescolarapp.util.Validadores
import cl.duoc.huertoescolarapp.util.esAtrasada
import cl.duoc.huertoescolarapp.util.fechaHoy
import cl.duoc.huertoescolarapp.util.parsearFecha
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Una línea del historial de actividades
data class EventoHistorial(
    val titulo: String,
    val detalle: String,
    val fecha: String,
    val estado: String
)

class TareaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HuertoRepository(AppDatabase.getDatabase(application))

    val tiposTarea = listOf("Riego", "Siembra", "Mantenimiento", "Observación")
    val estadosFiltro = listOf("Todos", "Pendiente", "En desarrollo", "Pendiente de validación", "Validada", "Rechazada", "Atrasada")

    // ---------- Formulario de creación / edición (Admin) ----------
    private var tareaEnEdicion: TareaEntity? = null
    var idEdicion by mutableStateOf<Long?>(null)
    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var tipo by mutableStateOf("Riego")
    var fechaProgramada by mutableStateOf("")
    var responsableIdSeleccionado by mutableStateOf<Long?>(null)
    var sectorIdSeleccionado by mutableStateOf<Long?>(null)

    // Mensajes de error debajo de cada campo
    var errorTitulo by mutableStateOf<String?>(null)
    var errorDescripcion by mutableStateOf<String?>(null)
    var errorSector by mutableStateOf<String?>(null)
    var errorResponsable by mutableStateOf<String?>(null)
    var errorFecha by mutableStateOf<String?>(null)
    var errorComentario by mutableStateOf<String?>(null)

    // Confirmación o error de la operación
    var mensaje by mutableStateOf<String?>(null)
    var mensajeExito by mutableStateOf(true)

    // ---------- Filtros del listado de tareas ----------
    var filtroEstado by mutableStateOf("Todos")
    var filtroResponsableId by mutableStateOf<Long?>(null)
    var filtroFecha by mutableStateOf("")

    // ---------- Listas reactivas ----------
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

    val listaEvidencias = repository.evidencias
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun avisar(texto: String, exito: Boolean) {
        mensaje = texto
        mensajeExito = exito
    }

    fun limpiarMensaje() {
        mensaje = null
    }

    // ---------- Consultas (permisos según el rol) ----------

    // El administrador ve todas las tareas; el estudiante solo las que tiene asignadas
    fun tareasVisibles(tareas: List<TareaEntity>): List<TareaEntity> {
        val usuario = SesionActual.usuario ?: return emptyList()
        return if (usuario.rol == "Administrador") tareas else tareas.filter { it.responsableId == usuario.id }
    }

    fun filtrar(tareas: List<TareaEntity>): List<TareaEntity> {
        return tareasVisibles(tareas).filter { t ->
            val cumpleEstado = when (filtroEstado) {
                "Todos" -> true
                "Atrasada" -> esAtrasada(t.estado, t.fechaProgramada)
                else -> t.estado == filtroEstado
            }
            val cumpleResponsable = filtroResponsableId == null || t.responsableId == filtroResponsableId
            val cumpleFecha = filtroFecha.isBlank() || t.fechaProgramada == filtroFecha
            cumpleEstado && cumpleResponsable && cumpleFecha
        }.sortedBy { parsearFecha(it.fechaProgramada) ?: 0L }
    }

    fun limpiarFiltros() {
        filtroEstado = "Todos"
        filtroResponsableId = null
        filtroFecha = ""
    }

    fun contarPorHacer(tareas: List<TareaEntity>): Int {
        return tareasVisibles(tareas).count {
            it.estado == "Pendiente" || it.estado == "En desarrollo" || it.estado == "Rechazada"
        }
    }

    fun contarAtrasadas(tareas: List<TareaEntity>): Int {
        return tareasVisibles(tareas).count { esAtrasada(it.estado, it.fechaProgramada) }
    }

    fun contarPorValidar(tareas: List<TareaEntity>): Int {
        return tareas.count { it.estado == "Pendiente de validación" }
    }

    fun nombreDe(id: Long, usuarios: List<UsuarioEntity>): String {
        return usuarios.find { it.id == id }?.nombre ?: "Sin asignar"
    }

    // Historial: validaciones realizadas y tareas entregadas esperando revisión
    fun construirHistorial(
        tareas: List<TareaEntity>,
        validaciones: List<ValidacionEntity>,
        usuarios: List<UsuarioEntity>
    ): List<EventoHistorial> {
        val visibles = tareasVisibles(tareas)
        val eventos = mutableListOf<EventoHistorial>()
        validaciones.sortedByDescending { it.id }.forEach { v ->
            val tarea = visibles.find { it.id == v.tareaId } ?: return@forEach
            val validador = nombreDe(v.validadorId, usuarios)
            val motivo = if (v.motivo.isNotBlank()) " Motivo: ${v.motivo}" else ""
            eventos.add(
                EventoHistorial(
                    titulo = tarea.titulo,
                    detalle = "${v.resultado} por $validador (responsable: ${nombreDe(tarea.responsableId, usuarios)}).$motivo",
                    fecha = v.fecha,
                    estado = v.resultado
                )
            )
        }
        visibles.filter { it.estado == "Pendiente de validación" }.forEach { t ->
            eventos.add(
                EventoHistorial(
                    titulo = t.titulo,
                    detalle = "Entregada por ${nombreDe(t.responsableId, usuarios)}, esperando validación.",
                    fecha = t.fechaProgramada,
                    estado = "Pendiente de validación"
                )
            )
        }
        return eventos
    }

    // ---------- Formulario (Admin) ----------
    private fun limpiarFormulario() {
        tareaEnEdicion = null
        idEdicion = null
        titulo = ""
        descripcion = ""
        tipo = "Riego"
        fechaProgramada = ""
        responsableIdSeleccionado = null
        sectorIdSeleccionado = null
        errorTitulo = null
        errorDescripcion = null
        errorSector = null
        errorResponsable = null
        errorFecha = null
    }

    fun nuevaTarea() {
        limpiarFormulario()
        mensaje = null
    }

    fun editarTarea(tarea: TareaEntity) {
        limpiarFormulario()
        mensaje = null
        tareaEnEdicion = tarea
        idEdicion = tarea.id
        titulo = tarea.titulo
        descripcion = tarea.descripcion
        tipo = tarea.tipo
        fechaProgramada = tarea.fechaProgramada
        responsableIdSeleccionado = tarea.responsableId
        sectorIdSeleccionado = tarea.sectorId
    }

    // 📌 Crear o editar una tarea (Admin)
    fun validarYGuardar() {
        if (!SesionActual.esAdmin()) {
            avisar("No tienes autorización para crear o asignar tareas.", false)
            return
        }
        errorTitulo = Validadores.texto(titulo, "El título", 3, 60)
        errorDescripcion = Validadores.texto(descripcion, "La descripción", 5, 200)
        errorSector = Validadores.seleccion(sectorIdSeleccionado, "Selecciona un sector.")
        errorResponsable = Validadores.seleccion(responsableIdSeleccionado, "Selecciona un estudiante responsable.")
        errorFecha = Validadores.fecha(fechaProgramada, "La fecha programada es obligatoria.")

        if (errorTitulo != null || errorDescripcion != null || errorSector != null ||
            errorResponsable != null || errorFecha != null
        ) {
            avisar("Revisa los campos marcados en rojo.", false)
            return
        }

        val original = tareaEnEdicion
        if (original != null && original.estado == "Validada") {
            avisar("Una tarea ya validada no se puede editar.", false)
            return
        }

        val sectorId = sectorIdSeleccionado!!
        val responsableId = responsableIdSeleccionado!!
        if (listaSectores.value.none { it.id == sectorId }) {
            errorSector = "El sector seleccionado no existe."
            avisar("Revisa los campos marcados en rojo.", false)
            return
        }

        viewModelScope.launch {
            // El responsable debe existir y ser un estudiante
            val responsable = repository.obtenerUsuario(responsableId)
            if (responsable == null || responsable.rol != "Colaborador") {
                errorResponsable = "El responsable no existe o no está autorizado para recibir tareas."
                avisar("Revisa los campos marcados en rojo.", false)
                return@launch
            }
            try {
                if (original == null) {
                    repository.guardarTarea(
                        TareaEntity(
                            sectorId = sectorId,
                            responsableId = responsableId,
                            titulo = titulo.trim(),
                            descripcion = descripcion.trim(),
                            tipo = tipo,
                            fechaProgramada = fechaProgramada,
                            estado = "Pendiente"
                        )
                    )
                    limpiarFormulario()
                    avisar("¡Tarea asignada con éxito!", true)
                } else {
                    repository.actualizarTarea(
                        original.copy(
                            sectorId = sectorId,
                            responsableId = responsableId,
                            titulo = titulo.trim(),
                            descripcion = descripcion.trim(),
                            tipo = tipo,
                            fechaProgramada = fechaProgramada
                        )
                    )
                    limpiarFormulario()
                    avisar("¡Tarea actualizada con éxito!", true)
                }
            } catch (e: Exception) {
                avisar("No se pudo guardar la tarea. Intenta nuevamente.", false)
            }
        }
    }

    fun eliminarTarea(tarea: TareaEntity) {
        if (!SesionActual.esAdmin()) {
            avisar("No tienes autorización para eliminar tareas.", false)
            return
        }
        viewModelScope.launch {
            try {
                repository.eliminarTarea(tarea)
                avisar("Tarea eliminada.", true)
            } catch (e: Exception) {
                avisar("No se pudo eliminar la tarea.", false)
            }
        }
    }

    // ---------- Flujo del Estudiante ----------

    private fun esResponsable(tarea: TareaEntity): Boolean {
        val usuario = SesionActual.usuario
        return usuario != null && usuario.rol == "Colaborador" && tarea.responsableId == usuario.id
    }

    // Pendiente / Rechazada → En desarrollo
    fun iniciarTarea(tarea: TareaEntity) {
        if (!esResponsable(tarea)) {
            avisar("Solo el estudiante asignado puede trabajar esta tarea.", false)
            return
        }
        if (tarea.estado != "Pendiente" && tarea.estado != "Rechazada") {
            avisar("Esta tarea no se puede iniciar en su estado actual.", false)
            return
        }
        viewModelScope.launch {
            try {
                repository.actualizarTarea(tarea.copy(estado = "En desarrollo"))
                avisar("Tarea iniciada. ¡Manos a la obra!", true)
            } catch (e: Exception) {
                avisar("No se pudo iniciar la tarea.", false)
            }
        }
    }

    // Guarda una foto como evidencia de la tarea (solo mientras está en desarrollo)
    fun agregarEvidencia(tarea: TareaEntity, ruta: String) {
        if (!esResponsable(tarea) || tarea.estado != "En desarrollo") {
            avisar("No puedes agregar evidencias a esta tarea.", false)
            return
        }
        viewModelScope.launch {
            try {
                repository.guardarEvidencia(EvidenciaEntity(tareaId = tarea.id, rutaFoto = ruta, fecha = fechaHoy()))
                avisar("Foto agregada como evidencia.", true)
            } catch (e: Exception) {
                avisar("No se pudo guardar la evidencia.", false)
            }
        }
    }

    // En desarrollo → Pendiente de validación (exige comentario y al menos una foto)
    fun entregarTarea(tarea: TareaEntity, comentario: String) {
        if (!esResponsable(tarea)) {
            avisar("Solo el estudiante asignado puede entregar esta tarea.", false)
            return
        }
        if (tarea.estado != "En desarrollo") {
            avisar("Solo se pueden entregar tareas que están en desarrollo.", false)
            return
        }
        errorComentario = Validadores.texto(comentario, "El comentario", 5, 200)
        if (errorComentario != null) {
            avisar("Revisa el comentario antes de entregar.", false)
            return
        }
        val cantidadFotos = listaEvidencias.value.count { it.tareaId == tarea.id }
        if (cantidadFotos == 0) {
            avisar("Debes adjuntar al menos una foto como evidencia.", false)
            return
        }
        viewModelScope.launch {
            try {
                repository.actualizarTarea(
                    tarea.copy(estado = "Pendiente de validación", comentariosEstudiante = comentario.trim())
                )
                avisar("Tarea entregada. Quedó pendiente de validación.", true)
            } catch (e: Exception) {
                avisar("No se pudo entregar la tarea.", false)
            }
        }
    }

    // ---------- Flujo del Administrador ----------

    // Validar (aprobar) o rechazar una tarea (exige motivo). Devuelve el error para mostrarlo en la tarjeta.
    // Además de cambiar el estado, guarda quién validó, cuándo y el resultado
    fun revisarTareaAdmin(tarea: TareaEntity, aprobado: Boolean, motivo: String): String? {
        val admin = SesionActual.usuario
        if (admin == null || admin.rol != "Administrador") return "No tienes autorización para validar tareas."
        if (tarea.responsableId == admin.id) return "No puedes validar tus propias actividades."
        if (tarea.estado != "Pendiente de validación") return "Solo se pueden validar tareas que ya fueron entregadas."
        if (!aprobado) {
            val error = Validadores.motivoRechazo(motivo)
            if (error != null) return error
        }
        viewModelScope.launch {
            try {
                val estadoFinal = if (aprobado) "Validada" else "Rechazada"
                repository.actualizarTarea(tarea.copy(estado = estadoFinal))
                repository.guardarValidacion(
                    ValidacionEntity(
                        tareaId = tarea.id,
                        validadorId = admin.id,
                        resultado = estadoFinal,
                        motivo = if (!aprobado) motivo.trim() else "",
                        fecha = fechaHoy()
                    )
                )
                avisar(if (aprobado) "Tarea validada." else "Tarea rechazada. El estudiante podrá corregirla y reenviarla.", true)
            } catch (e: Exception) {
                avisar("No se pudo registrar la revisión.", false)
            }
        }
        return null
    }
}
