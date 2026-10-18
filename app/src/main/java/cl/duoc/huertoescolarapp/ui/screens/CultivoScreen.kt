package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.huertoescolarapp.ui.viewmodel.CultivoViewModel

@Composable
fun CultivoScreen(
    viewModel: CultivoViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val sectores by viewModel.listaSectores.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🌿 Gestión de Cultivos",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Selector simple de sector ID (puedes adaptarlo con un Dropdown o botones)
        Text(text = "Seleccione ID de Sector disponible:", fontSize = 14.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            sectores.forEach { sector ->
                Button(
                    onClick = { viewModel.sectorIdSeleccionado = sector.id },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.sectorIdSeleccionado == sector.id)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(text = "Sector ${sector.id}: ${sector.nombre}")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.nombreCultivo,
            onValueChange = { viewModel.nombreCultivo = it },
            label = { Text("Nombre del Cultivo (ej. Tomate)") },
            isError = viewModel.errorNombre,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.fechaSiembra,
            onValueChange = { viewModel.fechaSiembra = it },
            label = { Text("Fecha Siembra (ej. 01/10/2026)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.fechaCosecha,
            onValueChange = { viewModel.fechaCosecha = it },
            label = { Text("Fecha Cosecha Estimada (ej. 15/12/2026)") },
            isError = viewModel.errorFechas,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.validarYGuardar() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar Cultivo")
        }

        if (viewModel.registroExitoso) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "¡Cultivo registrado con éxito!", color = MaterialTheme.colorScheme.primary)
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