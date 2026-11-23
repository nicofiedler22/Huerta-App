package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.data.entity.SectorEntity
import cl.duoc.huertoescolarapp.ui.components.BotonVolver
import cl.duoc.huertoescolarapp.ui.components.DialogoConfirmar
import cl.duoc.huertoescolarapp.ui.components.Encabezado
import cl.duoc.huertoescolarapp.ui.components.EstadoChip
import cl.duoc.huertoescolarapp.ui.components.EstadoVacio
import cl.duoc.huertoescolarapp.ui.components.MensajeEstado
import cl.duoc.huertoescolarapp.ui.theme.colorEstadoSector
import cl.duoc.huertoescolarapp.ui.viewmodel.SectorViewModel

@Composable
fun SectorListScreen(
    viewModel: SectorViewModel,
    modifier: Modifier = Modifier,
    onNuevo: () -> Unit,
    onAbrir: (SectorEntity) -> Unit,
    onEditar: (SectorEntity) -> Unit,
    onBack: () -> Unit
) {
    // Recolectamos la lista de sectores desde el StateFlow del ViewModel de manera reactiva
    val sectores by viewModel.listaSectores.collectAsState()
    var sectorAEliminar by remember { mutableStateOf<SectorEntity?>(null) }

    LaunchedEffect(Unit) { viewModel.limpiarMensaje() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Encabezado("🌱 Listado de Sectores")

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onNuevo, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Registrar Nuevo Sector")
        }

        MensajeEstado(viewModel.mensaje, viewModel.mensajeExito)

        Spacer(modifier = Modifier.height(8.dp))

        if (sectores.isEmpty()) {
            EstadoVacio("No hay sectores registrados aún.", Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sectores, key = { it.id }) { sector ->
                    SectorItemCard(
                        sector = sector,
                        onAbrir = { onAbrir(sector) },
                        onEditar = { onEditar(sector) },
                        onEliminar = { sectorAEliminar = sector }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        BotonVolver(texto = "Volver al Panel", onClick = onBack)
    }

    val pendiente = sectorAEliminar
    if (pendiente != null) {
        DialogoConfirmar(
            titulo = "Eliminar sector",
            texto = "Se eliminará \"${pendiente.nombre}\" junto con sus cultivos, tareas y reportes. ¿Continuar?",
            onConfirmar = {
                viewModel.eliminarSector(pendiente)
                sectorAEliminar = null
            },
            onCancelar = { sectorAEliminar = null }
        )
    }
}

@Composable
fun SectorItemCard(
    sector: SectorEntity,
    onAbrir: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onAbrir() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = sector.nombre, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ubicación: ${sector.ubicacion}", style = MaterialTheme.typography.bodyMedium)
            if (sector.descripcion.isNotBlank()) {
                Text(text = "Descripción: ${sector.descripcion}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(6.dp))
            EstadoChip(texto = sector.estado, color = colorEstadoSector(sector.estado))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAbrir) { Text("Ver detalle") }
                OutlinedButton(onClick = onEditar) { Text("Editar") }
                OutlinedButton(onClick = onEliminar) { Text("Eliminar") }
            }
        }
    }
}
