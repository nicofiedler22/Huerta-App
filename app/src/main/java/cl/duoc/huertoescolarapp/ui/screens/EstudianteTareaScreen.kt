package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel

@Composable
fun EstudianteTareaScreen(
    viewModel: TareaViewModel,
    nombreEstudianteLogueado: String, // Nombre del estudiante actual
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val tareas by viewModel.listaTareas.collectAsState()
    // Filtramos solo las tareas que le pertenecen al estudiante
    val misTareas = tareas.filter { it.responsable.equals(nombreEstudianteLogueado, ignoreCase = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🌱 Mis Tareas Asignadas ($nombreEstudianteLogueado)",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (misTareas.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text(text = "No tienes tareas asignadas por ahora.", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(misTareas) { tarea ->
                    var comentario by remember { mutableStateOf(tarea.comentariosEstudiante) }

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = tarea.titulo, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Tipo: ${tarea.tipo} | Estado: ${tarea.estado}", fontSize = 12.sp)
                            Text(text = "Instrucciones: ${tarea.descripcion}", fontSize = 12.sp)

                            if (tarea.estado == "Rechazada") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Motivo de rechazo: ${tarea.motivoRechazo}", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = comentario,
                                onValueChange = { comentario = it },
                                label = { Text("Comentarios / Evidencia de entrega") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (tarea.estado == "Pendiente" || tarea.estado == "Rechazada") {
                                    Button(
                                        onClick = { viewModel.actualizarEstadoEstudiante(tarea, "En desarrollo", comentario) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Iniciar")
                                    }
                                }

                                Button(
                                    onClick = { viewModel.actualizarEstadoEstudiante(tarea, "Pendiente de validación", comentario) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("Entregar")
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
            Text(text = "Volver")
        }
    }
}