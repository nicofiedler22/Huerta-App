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
fun TareaScreen(
    viewModel: TareaViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val sectores by viewModel.listaSectores.collectAsState()
    val tareas by viewModel.listaTareas.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "📋 Gestión y Asignación de Tareas",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Selección rápida de Sector ID
        Text(text = "Seleccione ID de Sector:", fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            sectores.forEach { sector ->
                Button(
                    onClick = { viewModel.sectorIdSeleccionado = sector.id },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.sectorIdSeleccionado == sector.id)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(text = "Sec ${sector.id}")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.titulo,
            onValueChange = { viewModel.titulo = it },
            label = { Text("Título de la Tarea") },
            isError = viewModel.errorTitulo,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.descripcion,
            onValueChange = { viewModel.descripcion = it },
            label = { Text("Instrucciones / Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.responsable,
            onValueChange = { viewModel.responsable = it },
            label = { Text("Estudiante Responsable") },
            isError = viewModel.errorResponsable,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.fechaProgramada,
            onValueChange = { viewModel.fechaProgramada = it },
            label = { Text("Fecha Programada (ej. 20/10/2026)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { viewModel.validarYGuardar() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Crear y Asignar Tarea")
        }

        if (viewModel.registroExitoso) {
            Text(text = "¡Tarea asignada con éxito!", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Tareas Existentes en el Sistema:", fontSize = 16.sp)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(tareas) { tarea ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = tarea.titulo, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Asignado a: ${tarea.responsable} | Estado: ${tarea.estado}", fontSize = 12.sp)
                        Text(text = "Fecha: ${tarea.fechaProgramada}", fontSize = 12.sp)
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