package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.data.entity.CultivoEntity
import cl.duoc.huertoescolarapp.ui.components.BotonVolver
import cl.duoc.huertoescolarapp.ui.components.Cargando
import cl.duoc.huertoescolarapp.ui.components.DialogoConfirmar
import cl.duoc.huertoescolarapp.ui.components.Encabezado
import cl.duoc.huertoescolarapp.ui.components.EstadoChip
import cl.duoc.huertoescolarapp.ui.components.EstadoVacio
import cl.duoc.huertoescolarapp.ui.components.MensajeEstado
import cl.duoc.huertoescolarapp.ui.theme.colorEstadoCultivo
import cl.duoc.huertoescolarapp.ui.theme.colorEstadoSector
import cl.duoc.huertoescolarapp.ui.viewmodel.CultivoViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.SectorViewModel

@Composable
fun SectorDetalleScreen(
    sectorId: Long,
    sectorViewModel: SectorViewModel,
    cultivoViewModel: CultivoViewModel,
    modifier: Modifier = Modifier,
    onNuevoCultivo: () -> Unit,
    onEditarCultivo: (CultivoEntity) -> Unit,
    onBack: () -> Unit
) {
    val sectorFlow = remember(sectorId) { sectorViewModel.sectorPorId(sectorId) }
    val cultivosFlow = remember(sectorId) { sectorViewModel.cultivosDelSector(sectorId) }
    val sector by sectorFlow.collectAsState(initial = null)
    val cultivos by cultivosFlow.collectAsState(initial = emptyList())
    var cultivoAEliminar by remember { mutableStateOf<CultivoEntity?>(null) }

    LaunchedEffect(Unit) { cultivoViewModel.limpiarMensaje() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val actual = sector
        if (actual == null) {
            Cargando()
        } else {
            Encabezado("🌿 ${actual.nombre}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ubicación: ${actual.ubicacion}", style = MaterialTheme.typography.bodyMedium)
            if (actual.descripcion.isNotBlank()) {
                Text(text = actual.descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(6.dp))
            EstadoChip(texto = actual.estado, color = colorEstadoSector(actual.estado))

            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Cultivos del sector (${cultivos.size})", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(6.dp))

            Button(onClick = onNuevoCultivo, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Agregar cultivo a este sector")
            }

            MensajeEstado(cultivoViewModel.mensaje, cultivoViewModel.mensajeExito)

            Spacer(modifier = Modifier.height(8.dp))

            if (cultivos.isEmpty()) {
                EstadoVacio("Este sector todavía no tiene cultivos.", Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cultivos, key = { it.id }) { cultivo ->
                        Card(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = cultivo.nombreCultivo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Text(text = "Siembra: ${cultivo.fechaSiembra}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "Cosecha estimada: ${cultivo.fechaCosechaEstimada}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "Riego: cada ${cultivo.frecuenciaRiegoDias} día(s)", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                EstadoChip(texto = cultivo.estadoDesarrollo, color = colorEstadoCultivo(cultivo.estadoDesarrollo))
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { onEditarCultivo(cultivo) }) { Text("Editar") }
                                    OutlinedButton(onClick = { cultivoAEliminar = cultivo }) { Text("Eliminar") }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        BotonVolver(texto = "Volver a Sectores", onClick = onBack)
    }

    val pendiente = cultivoAEliminar
    if (pendiente != null) {
        DialogoConfirmar(
            titulo = "Eliminar cultivo",
            texto = "Se eliminará el cultivo \"${pendiente.nombreCultivo}\". ¿Continuar?",
            onConfirmar = {
                cultivoViewModel.eliminarCultivo(pendiente)
                cultivoAEliminar = null
            },
            onCancelar = { cultivoAEliminar = null }
        )
    }
}
