package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel

@Composable
fun AdminRevisionScreen(
    viewModel: TareaViewModel,
    adminId: Long,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val tareas by viewModel.listaTareas.collectAsState()
    val usuarios by viewModel.listaUsuarios.collectAsState()
    val tareasPendientesValidacion = tareas.filter { it.estado == "Pendiente de validación" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "👨‍🏫 Panel de Validación de Tareas (Admin)",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (tareasPendientesValidacion.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No hay tareas pendientes de validación.", color = MaterialTheme.colorScheme.outline)
            }
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
                    val nombreResponsable = usuarios.find { it.id == tarea.responsableId }?.nombre ?: "Sin asignar"

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = tarea.titulo, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Responsable: $nombreResponsable", fontSize = 12.sp)
                            Text(text = "Descripción: ${tarea.descripcion}", fontSize = 12.sp)
                            Text(text = "Fecha programada: ${tarea.fechaProgramada}", fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Comentario del estudiante: ${if (tarea.comentariosEstudiante.isNotBlank()) tarea.comentariosEstudiante else "Sin comentarios"}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            if (mostrarInputRechazo) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = motivoRechazoInput,
                                    onValueChange = { motivoRechazoInput = it },
                                    label = { Text("Motivo del rechazo (Obligatorio)") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = {
                                        if (motivoRechazoInput.isNotBlank()) {
                                            viewModel.revisarTareaAdmin(tarea, aprobado = false, motivo = motivoRechazoInput, validadorId = adminId)
                                            mostrarInputRechazo = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Confirmar Rechazo")
                                }
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.revisarTareaAdmin(tarea, aprobado = true, motivo = "", validadorId = adminId) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Aprobar")
                                    }

                                    Button(
                                        onClick = { mostrarInputRechazo = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Rechazar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
        ) {
            Text(text = "Volver al Panel")
        }
    }
}
