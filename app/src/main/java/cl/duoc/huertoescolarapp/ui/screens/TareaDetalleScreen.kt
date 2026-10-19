package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.data.entity.TareaEntity
import cl.duoc.huertoescolarapp.ui.components.BotonVolver
import cl.duoc.huertoescolarapp.ui.components.CampoTexto
import cl.duoc.huertoescolarapp.ui.components.DialogoConfirmar
import cl.duoc.huertoescolarapp.ui.components.Encabezado
import cl.duoc.huertoescolarapp.ui.components.EstadoChip
import cl.duoc.huertoescolarapp.ui.components.ListaVistaPrevia
import cl.duoc.huertoescolarapp.ui.components.MensajeEstado
import cl.duoc.huertoescolarapp.ui.components.SeccionFotos
import cl.duoc.huertoescolarapp.ui.theme.ColorError
import cl.duoc.huertoescolarapp.ui.theme.colorEstadoTarea
import cl.duoc.huertoescolarapp.ui.viewmodel.SesionActual
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel
import cl.duoc.huertoescolarapp.util.esAtrasada

// Detalle de una tarea: instrucciones, estado, fecha, evidencias y acciones según el rol
@Composable
fun TareaDetalleScreen(
    tareaId: Long,
    viewModel: TareaViewModel,
    esAdmin: Boolean,
    modifier: Modifier = Modifier,
    onEditar: (TareaEntity) -> Unit,
    onBack: () -> Unit
) {
    val tareas by viewModel.listaTareas.collectAsState()
    val usuarios by viewModel.listaUsuarios.collectAsState()
    val sectores by viewModel.listaSectores.collectAsState()
    val validaciones by viewModel.listaValidaciones.collectAsState()
    val evidencias by viewModel.listaEvidencias.collectAsState()
    var confirmarEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.limpiarMensaje()
        viewModel.errorComentario = null
    }

    val tarea = tareas.find { it.id == tareaId }
    val usuario = SesionActual.usuario

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (tarea == null) {
            Text(text = "La tarea ya no existe.")
        } else if (!esAdmin && tarea.responsableId != usuario?.id) {
            // El estudiante no puede ver tareas de otros
            Text(text = "⛔ No tienes autorización para ver esta tarea.", color = ColorError)
        } else {
            val fotos = evidencias.filter { it.tareaId == tarea.id }.map { it.rutaFoto }
            val historial = validaciones.filter { it.tareaId == tarea.id }.sortedByDescending { it.id }
            var comentario by remember(tarea.id) { mutableStateOf(tarea.comentariosEstudiante) }

            Encabezado("📌 ${tarea.titulo}")

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EstadoChip(texto = tarea.estado, color = colorEstadoTarea(tarea.estado))
                if (esAtrasada(tarea.estado, tarea.fechaProgramada)) {
                    EstadoChip(texto = "⏰ Atrasada", color = ColorError)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = "Tipo: ${tarea.tipo}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Sector: ${sectores.find { it.id == tarea.sectorId }?.nombre ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Responsable: ${viewModel.nombreDe(tarea.responsableId, usuarios)}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Fecha programada: ${tarea.fechaProgramada}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Instrucciones:", style = MaterialTheme.typography.titleMedium)
                    Text(text = tarea.descripcion, style = MaterialTheme.typography.bodyMedium)
                    if (tarea.comentariosEstudiante.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Comentario del estudiante: ${tarea.comentariosEstudiante}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            if (tarea.estado == "Rechazada") {
                val ultima = historial.firstOrNull()
                if (ultima != null) {
                    Text(text = "Motivo de rechazo: ${ultima.motivo}", color = ColorError, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (fotos.isNotEmpty()) {
                Text(text = "Evidencias (${fotos.size})", style = MaterialTheme.typography.titleMedium)
                ListaVistaPrevia(fotos)
            }

            if (historial.isNotEmpty()) {
                Text(text = "Historial de validaciones", style = MaterialTheme.typography.titleMedium)
                historial.forEach { v ->
                    Text(
                        text = "• ${v.fecha}: ${v.resultado} por ${viewModel.nombreDe(v.validadorId, usuarios)}" +
                            if (v.motivo.isNotBlank()) " (${v.motivo})" else "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // ----- Acciones del estudiante responsable -----
            if (!esAdmin) {
                if (tarea.estado == "Pendiente" || tarea.estado == "Rechazada") {
                    Button(onClick = { viewModel.iniciarTarea(tarea) }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = if (tarea.estado == "Rechazada") "Corregir y volver a iniciar" else "Iniciar tarea")
                    }
                }
                if (tarea.estado == "En desarrollo") {
                    CampoTexto(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = "Comentarios de la entrega",
                        error = viewModel.errorComentario,
                        singleLine = false
                    )
                    Text(text = "Agregar evidencia fotográfica:", style = MaterialTheme.typography.bodyMedium)
                    SeccionFotos(onFotoAgregada = { ruta -> viewModel.agregarEvidencia(tarea, ruta) })
                    Button(
                        onClick = { viewModel.entregarTarea(tarea, comentario) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Entregar tarea")
                    }
                }
                if (tarea.estado == "Pendiente de validación") {
                    Text(text = "🕒 Tu tarea fue entregada y espera la validación del docente.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // ----- Acciones del administrador -----
            if (esAdmin) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { onEditar(tarea) }, modifier = Modifier.weight(1f)) { Text("Editar") }
                    OutlinedButton(onClick = { confirmarEliminar = true }, modifier = Modifier.weight(1f)) { Text("Eliminar") }
                }
            }

            MensajeEstado(viewModel.mensaje, viewModel.mensajeExito)

            if (confirmarEliminar) {
                DialogoConfirmar(
                    titulo = "Eliminar tarea",
                    texto = "Se eliminará \"${tarea.titulo}\" con sus evidencias. ¿Continuar?",
                    onConfirmar = {
                        confirmarEliminar = false
                        viewModel.eliminarTarea(tarea)
                        onBack()
                    },
                    onCancelar = { confirmarEliminar = false }
                )
            }
        }

        BotonVolver(texto = "Volver a la lista", onClick = onBack)
    }
}
