package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.ui.components.BotonVolver
import cl.duoc.huertoescolarapp.ui.components.CampoTexto
import cl.duoc.huertoescolarapp.ui.components.Encabezado
import cl.duoc.huertoescolarapp.ui.components.MensajeEstado
import cl.duoc.huertoescolarapp.ui.components.SelectorOpciones
import cl.duoc.huertoescolarapp.ui.viewmodel.SectorViewModel

// Formulario para crear o editar un sector
@Composable
fun SectorScreen(
    viewModel: SectorViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Encabezado(if (viewModel.idEdicion == null) "🌱 Registrar Nuevo Sector" else "✏️ Editar Sector")

        CampoTexto(
            value = viewModel.nombre,
            onValueChange = { viewModel.nombre = it },
            label = "Nombre del Sector",
            error = viewModel.errorNombre
        )

        CampoTexto(
            value = viewModel.ubicacion,
            onValueChange = { viewModel.ubicacion = it },
            label = "Ubicación",
            error = viewModel.errorUbicacion
        )

        CampoTexto(
            value = viewModel.descripcion,
            onValueChange = { viewModel.descripcion = it },
            label = "Descripción (Opcional)",
            error = viewModel.errorDescripcion,
            singleLine = false
        )

        Text(text = "Estado del sector:", style = MaterialTheme.typography.bodyMedium)
        SelectorOpciones(
            opciones = listOf("Activo" to "Activo", "En descanso" to "En descanso"),
            seleccionado = viewModel.estado,
            onSeleccion = { viewModel.estado = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.validarYGuardar() }, modifier = Modifier.fillMaxWidth()) {
            Text(text = if (viewModel.idEdicion == null) "Guardar Sector" else "Guardar Cambios")
        }

        MensajeEstado(viewModel.mensaje, viewModel.mensajeExito)

        if (viewModel.idEdicion != null) {
            OutlinedButton(onClick = { viewModel.nuevoSector() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Cancelar edición")
            }
        }

        BotonVolver(onClick = onBack)
    }
}
