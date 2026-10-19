package cl.duoc.huertoescolarapp.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel
import java.util.Calendar

@Composable
fun TareaScreen(
    viewModel: TareaViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val sectores by viewModel.listaSectores.collectAsState()
    val colaboradores by viewModel.listaColaboradores.collectAsState()
    val usuarios by viewModel.listaUsuarios.collectAsState()
    val tareas by viewModel.listaTareas.collectAsState()
    val context = LocalContext.current

    // Selector de fecha nativo para la fecha programada
    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                viewModel.fechaProgramada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // Toda la pantalla es una sola lista desplazable
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "📋 Gestión y Asignación de Tareas",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Selección de Sector
        item {
            Column {
                Text(text = "Seleccione el sector:", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    sectores.forEach { sector ->
                        Button(
                            onClick = { viewModel.sectorIdSeleccionado = sector.id },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.sectorIdSeleccionado == sector.id)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(text = sector.nombre)
                        }
                    }
                }
            }
        }

        // Selección del tipo de tarea
        item {
            Column {
                Text(text = "Tipo de tarea:", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Riego", "Siembra", "Mantenimiento", "Observación").forEach { opcion ->
                        Button(
                            onClick = { viewModel.tipo = opcion },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.tipo == opcion)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(text = opcion)
                        }
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = viewModel.titulo,
                onValueChange = { viewModel.titulo = it },
                label = { Text("Título de la Tarea") },
                isError = viewModel.errorTitulo,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = viewModel.descripcion,
                onValueChange = { viewModel.descripcion = it },
                label = { Text("Instrucciones / Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Selección del estudiante responsable (solo estudiantes que existen)
        item {
            Column {
                Text(text = "Estudiante responsable:", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    colaboradores.forEach { estudiante ->
                        Button(
                            onClick = { viewModel.responsableIdSeleccionado = estudiante.id },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.responsableIdSeleccionado == estudiante.id)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(text = estudiante.nombre)
                        }
                    }
                }
                if (viewModel.errorResponsable) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Debes seleccionar un estudiante.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Fecha programada con selector de calendario
        item {
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (viewModel.fechaProgramada.isBlank()) "📅 Seleccionar fecha programada"
                    else "Fecha programada: ${viewModel.fechaProgramada}",
                    fontSize = 15.sp
                )
            }
        }

        item {
            Column {
                Button(
                    onClick = { viewModel.validarYGuardar() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Crear y Asignar Tarea")
                }

                if (viewModel.registroExitoso) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "¡Tarea asignada con éxito!", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        item {
            Text(text = "Tareas Existentes en el Sistema:", fontSize = 16.sp)
        }

        items(tareas, key = { it.id }) { tarea ->
            // El responsable ahora se busca por su id
            val nombreResponsable = usuarios.find { it.id == tarea.responsableId }?.nombre ?: "Sin asignar"

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = tarea.titulo, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Tipo: ${tarea.tipo}", fontSize = 12.sp)
                    Text(text = "Asignado a: $nombreResponsable | Estado: ${tarea.estado}", fontSize = 12.sp)
                    Text(text = "Fecha programada: ${tarea.fechaProgramada}", fontSize = 12.sp)
                }
            }
        }

        item {
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
            ) {
                Text(text = "Volver al Panel")
            }
        }
    }
}
