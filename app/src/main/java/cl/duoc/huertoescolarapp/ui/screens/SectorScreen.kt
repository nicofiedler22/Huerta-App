package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.SectorViewModel

@Composable
fun SectorScreen(
    modifier: Modifier = Modifier,
    viewModel: SectorViewModel = viewModel(),
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Registrar Nuevo Sector",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = viewModel.nombre,
            onValueChange = { viewModel.nombre = it },
            label = { Text("Nombre del Sector (Ej: Zona Norte)") },
            isError = viewModel.errorNombre,
            supportingText = {
                if (viewModel.errorNombre) {
                    Text("El nombre es obligatorio")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.ubicacion,
            onValueChange = { viewModel.ubicacion = it },
            label = { Text("Ubicación del sector") },
            isError = viewModel.errorUbicacion,
            supportingText = {
                if (viewModel.errorUbicacion) {
                    Text("La ubicación es obligatoria")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.validarYGuardar() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Sector")
        }

        if (viewModel.registroExitoso) {
            Text(
                text = "¡Sector validado y guardado con éxito!",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}