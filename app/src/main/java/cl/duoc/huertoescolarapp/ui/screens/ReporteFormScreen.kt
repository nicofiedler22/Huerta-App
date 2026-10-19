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
import cl.duoc.huertoescolarapp.ui.components.EstadoVacio
import cl.duoc.huertoescolarapp.ui.components.MensajeEstado
import cl.duoc.huertoescolarapp.ui.components.SeccionFotos
import cl.duoc.huertoescolarapp.ui.components.SelectorOpciones
import cl.duoc.huertoescolarapp.ui.components.TextoError
import cl.duoc.huertoescolarapp.ui.components.VistaPrevia
import cl.duoc.huertoescolarapp.ui.viewmodel.ReporteViewModel

@Composable
fun ReporteFormScreen(
    viewModel: ReporteViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val sectores by viewModel.listaSectores.collectAsState()
    val cultivos by viewModel.listaCultivos.collectAsState()
    val cultivosDelSector = cultivos.filter { it.sectorId == viewModel.sectorIdSeleccionado }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Encabezado("🐛 Reportar un problema")

        Text(text = "Sector donde ocurre:", style = MaterialTheme.typography.bodyMedium)
        if (sectores.isEmpty()) {
            EstadoVacio("No hay sectores registrados.")
        } else {
            SelectorOpciones(
                opciones = sectores.map { it.id to it.nombre },
                seleccionado = viewModel.sectorIdSeleccionado,
                onSeleccion = {
                    viewModel.sectorIdSeleccionado = it
                    viewModel.cultivoIdSeleccionado = null
                }
            )
        }
        TextoError(viewModel.errorSector)

        if (viewModel.sectorIdSeleccionado != null) {
            Text(text = "Cultivo afectado (opcional):", style = MaterialTheme.typography.bodyMedium)
            SelectorOpciones(
                opciones = listOf(0L to "Ninguno") + cultivosDelSector.map { it.id to it.nombreCultivo },
                seleccionado = viewModel.cultivoIdSeleccionado ?: 0L,
                onSeleccion = { viewModel.cultivoIdSeleccionado = if (it == 0L) null else it }
            )
        }

        CampoTexto(
            value = viewModel.descripcion,
            onValueChange = { viewModel.descripcion = it },
            label = "Describe el problema",
            error = viewModel.errorDescripcion,
            singleLine = false
        )

        Text(text = "Foto del problema (opcional):", style = MaterialTheme.typography.bodyMedium)
        SeccionFotos(onFotoAgregada = { viewModel.fotoRuta = it })
        if (viewModel.fotoRuta.isNotBlank()) {
            VistaPrevia(ruta = viewModel.fotoRuta)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.guardarReporte() }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Enviar reporte")
        }

        MensajeEstado(viewModel.mensaje, viewModel.mensajeExito)

        BotonVolver(onClick = onBack)
    }
}
