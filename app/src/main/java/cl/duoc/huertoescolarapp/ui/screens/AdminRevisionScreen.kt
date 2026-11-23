package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.ui.components.BotonVolver
import cl.duoc.huertoescolarapp.ui.components.CampoTexto
import cl.duoc.huertoescolarapp.ui.components.Encabezado
import cl.duoc.huertoescolarapp.ui.components.EstadoChip
import cl.duoc.huertoescolarapp.ui.components.EstadoVacio
import cl.duoc.huertoescolarapp.ui.components.ListaVistaPrevia
import cl.duoc.huertoescolarapp.ui.components.MensajeEstado
import cl.duoc.huertoescolarapp.ui.components.TextoError
import cl.duoc.huertoescolarapp.ui.theme.ColorError
import cl.duoc.huertoescolarapp.ui.theme.colorEstadoTarea
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel

@Composable
fun AdminRevisionScreen(
    viewModel: TareaViewModel,
    modifier: Modifier = Modifier,
    onAbrirTarea: (Long) -> Unit,
    onBack: () -> Unit
) {
    val tareas by viewModel.listaTareas.collectAsState()
    val usuarios by viewModel.listaUsuarios.collectAsState()
    val evidencias by viewModel.listaEvidencias.collectAsState()
    val tareasPendientesValidacion = tareas.filter { it.estado == "Pendiente de validación" }

    LaunchedEffect(Unit) { viewModel.limpiarMensaje() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Encabezado("👨‍🏫 Panel de Validación de Tareas")

        MensajeEstado(viewModel.mensaje, viewModel.mensajeExito)

        Spacer(modifier = Modifier.height(8.dp))

        if (tareasPendientesValidacion.isEmpty()) {
            EstadoVacio("No hay tareas pendientes de validación.", Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareasPendientesValidacion, key = { it.id }) { tarea ->
                    var motivoRechazoInput by remember { mutableStateOf("") }
                    var mostrarInputRechazo by remember { mutableStateOf(false) }
                    var errorMotivo by remember { mutableStateOf<String?>(null) }
                    var errorAccion by remember { mutableStateOf<String?>(null) }
                    val fotos = evidencias.filter { it.tareaId == tarea.id }.map { it.rutaFoto }

                    Card(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = tarea.titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Responsable: ${viewModel.nombreDe(tarea.responsableId, usuarios)}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Descripción: ${tarea.descripcion}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Fecha programada: ${tarea.fechaProgramada}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            EstadoChip(texto = tarea.estado, color = colorEstadoTarea(tarea.estado))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Comentario del estudiante: ${if (tarea.comentariosEstudiante.isNotBlank()) tarea.comentariosEstudiante else "Sin comentarios"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            if (fotos.isEmpty()) {
                                Text(text = "Sin evidencias fotográficas.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            } else {
                                Spacer(modifier = Modifier.height(6.dp))
                                ListaVistaPrevia(fotos)
                            }

                            // El campo del motivo aparece con animación al pulsar "Rechazar"
                            AnimatedVisibility(visible = mostrarInputRechazo) {
                                Column {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    CampoTexto(
                                        value = motivoRechazoInput,
                                        onValueChange = { motivoRechazoInput = it },
                                        label = "Motivo del rechazo (Obligatorio)",
                                        error = errorMotivo,
                                        singleLine = false
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = {
                                            val error = viewModel.revisarTareaAdmin(tarea, aprobado = false, motivo = motivoRechazoInput)
                                            errorMotivo = error
                                            if (error == null) mostrarInputRechazo = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ColorError),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Confirmar Rechazo")
                                    }
                                }
                            }

                            if (!mostrarInputRechazo) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { errorAccion = viewModel.revisarTareaAdmin(tarea, aprobado = true, motivo = "") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Aprobar")
                                    }

                                    Button(
                                        onClick = { mostrarInputRechazo = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = ColorError)
                                    ) {
                                        Text("Rechazar")
                                    }
                                }
                                TextoError(errorAccion)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(onClick = { onAbrirTarea(tarea.id) }) { Text("Ver detalle de la tarea") }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        BotonVolver(texto = "Volver al Panel", onClick = onBack)
    }
}
