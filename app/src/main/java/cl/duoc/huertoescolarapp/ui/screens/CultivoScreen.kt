package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.ui.components.BotonVolver
import cl.duoc.huertoescolarapp.ui.components.CampoTexto
import cl.duoc.huertoescolarapp.ui.components.Encabezado
import cl.duoc.huertoescolarapp.ui.components.EstadoVacio
import cl.duoc.huertoescolarapp.ui.components.MensajeEstado
import cl.duoc.huertoescolarapp.ui.components.SelectorOpciones
import cl.duoc.huertoescolarapp.ui.components.TextoError
import cl.duoc.huertoescolarapp.ui.components.rememberSelectorFecha
import cl.duoc.huertoescolarapp.ui.viewmodel.CultivoViewModel

@Composable
fun CultivoScreen(
    viewModel: CultivoViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val sectores by viewModel.listaSectores.collectAsState()

    val abrirFechaSiembra = rememberSelectorFecha { viewModel.fechaSiembra = it }
    val abrirFechaCosecha = rememberSelectorFecha { viewModel.fechaCosecha = it }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Encabezado(if (viewModel.idEdicion == null) "🌿 Registrar Cultivo" else "✏️ Editar Cultivo")

        Text(text = "Sector donde se siembra:", style = MaterialTheme.typography.bodyMedium)
        if (sectores.isEmpty()) {
            EstadoVacio("No hay sectores creados aún. Crea un sector primero.")
        } else {
            SelectorOpciones(
                opciones = sectores.map { it.id to it.nombre },
                seleccionado = viewModel.sectorIdSeleccionado,
                onSeleccion = { viewModel.sectorIdSeleccionado = it }
            )
        }
        TextoError(viewModel.errorSector)

        CampoTexto(
            value = viewModel.nombreCultivo,
            onValueChange = { viewModel.nombreCultivo = it },
            label = "Nombre del Cultivo (ej. Tomate)",
            error = viewModel.errorNombre
        )

        CampoTexto(
            value = viewModel.frecuenciaRiego,
            onValueChange = { viewModel.frecuenciaRiego = it },
            label = "Frecuencia de riego (cada cuántos días)",
            error = viewModel.errorFrecuencia,
            keyboardType = KeyboardType.Number
        )

        Text(text = "Estado de desarrollo:", style = MaterialTheme.typography.bodyMedium)
        SelectorOpciones(
            opciones = listOf("Germinación", "Crecimiento", "Cosecha").map { it to it },
            seleccionado = viewModel.estadoDesarrollo,
            onSeleccion = { viewModel.estadoDesarrollo = it }
        )

        OutlinedButton(
            onClick = abrirFechaSiembra,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = if (viewModel.fechaSiembra.isBlank()) "📅 Seleccionar fecha de siembra"
                else "Fecha de siembra: ${viewModel.fechaSiembra}"
            )
        }
        TextoError(viewModel.errorSiembra)

        OutlinedButton(
            onClick = abrirFechaCosecha,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = if (viewModel.fechaCosecha.isBlank()) "📅 Seleccionar fecha de cosecha estimada"
                else "Fecha de cosecha estimada: ${viewModel.fechaCosecha}"
            )
        }
        TextoError(viewModel.errorCosecha)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.validarYGuardar() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = if (viewModel.idEdicion == null) "Guardar Cultivo" else "Guardar Cambios")
        }

        MensajeEstado(viewModel.mensaje, viewModel.mensajeExito)

        if (viewModel.idEdicion != null) {
            OutlinedButton(
                onClick = { viewModel.nuevoCultivo(viewModel.sectorIdSeleccionado) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cancelar edición")
            }
        }

        BotonVolver(onClick = onBack)
    }
}
