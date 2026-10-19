package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.huertoescolarapp.ui.viewmodel.SectorViewModel

@Composable
fun SectorScreen(
    viewModel: SectorViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🌱 Registrar Nuevo Sector",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.nombre,
            onValueChange = { viewModel.nombre = it },
            label = { Text("Nombre del Sector") },
            isError = viewModel.errorNombre,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.ubicacion,
            onValueChange = { viewModel.ubicacion = it },
            label = { Text("Ubicación") },
            isError = viewModel.errorUbicacion,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.descripcion,
            onValueChange = { viewModel.descripcion = it },
            label = { Text("Descripción (Opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.validarYGuardar() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar Sector")
        }

        if (viewModel.registroExitoso) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "¡Sector guardado con éxito!", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
        ) {
            Text(text = "Volver")
        }
    }
}