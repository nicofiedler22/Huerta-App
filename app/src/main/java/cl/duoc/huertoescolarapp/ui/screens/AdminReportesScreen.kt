package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.ui.components.BotonVolver
import cl.duoc.huertoescolarapp.ui.components.Encabezado
import cl.duoc.huertoescolarapp.ui.components.EstadoChip
import cl.duoc.huertoescolarapp.ui.components.EstadoVacio
import cl.duoc.huertoescolarapp.ui.components.MensajeEstado
import cl.duoc.huertoescolarapp.ui.components.VistaPrevia
import cl.duoc.huertoescolarapp.ui.theme.colorEstadoReporte
import cl.duoc.huertoescolarapp.ui.viewmodel.ReporteViewModel

// Consulta de reportes: el administrador ve todos y cambia su estado; el estudiante ve los suyos
@Composable
fun AdminReportesScreen(
    viewModel: ReporteViewModel,
    esAdmin: Boolean,
    modifier: Modifier = Modifier,
    onNuevoReporte: () -> Unit,
    onBack: () -> Unit
) {
    val reportes by viewModel.listaReportes.collectAsState()
    val sectores by viewModel.listaSectores.collectAsState()
    val cultivos by viewModel.listaCultivos.collectAsState()
    val usuarios by viewModel.listaUsuarios.collectAsState()
    val visibles = viewModel.reportesVisibles(reportes)

    LaunchedEffect(Unit) { viewModel.limpiarMensaje() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Encabezado(if (esAdmin) "🐛 Reportes de problemas del huerto" else "🐛 Mis reportes de problemas")
        Text(
            text = if (esAdmin) "Revisa los problemas informados y actualiza su estado de atención."
            else "Aquí ves los problemas que has informado y cómo van.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onNuevoReporte, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Reportar un problema")
        }

        MensajeEstado(viewModel.mensaje, viewModel.mensajeExito)

        Spacer(modifier = Modifier.height(8.dp))

        if (visibles.isEmpty()) {
            EstadoVacio("Todavía no hay reportes registrados.", Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibles, key = { it.id }) { reporte ->
                    Card(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Reporte #${reporte.id}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Problema: ${reporte.descripcion}", style = MaterialTheme.typography.bodyMedium)
                            Text("Fecha: ${reporte.fecha}", style = MaterialTheme.typography.bodyMedium)
                            Text("Sector: ${sectores.find { it.id == reporte.sectorId }?.nombre ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                            if (reporte.cultivoId != null) {
                                Text("Cultivo: ${cultivos.find { it.id == reporte.cultivoId }?.nombreCultivo ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text("Reportado por: ${usuarios.find { it.id == reporte.reportanteId }?.nombre ?: "-"}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            EstadoChip(texto = reporte.estadoAtencion, color = colorEstadoReporte(reporte.estadoAtencion))

                            if (reporte.fotoRuta.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                VistaPrevia(ruta = reporte.fotoRuta)
                            }

                            // Solo el administrador puede cambiar el estado de atención
                            if (esAdmin) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (reporte.estadoAtencion == "Pendiente") {
                                        Button(
                                            onClick = { viewModel.actualizarEstado(reporte, "En revisión") },
                                            modifier = Modifier.weight(1f)
                                        ) { Text("En revisión") }
                                    }
                                    if (reporte.estadoAtencion != "Resuelto") {
                                        Button(
                                            onClick = { viewModel.actualizarEstado(reporte, "Resuelto") },
                                            modifier = Modifier.weight(1f)
                                        ) { Text("Resolver") }
                                    }
                                    if (reporte.estadoAtencion != "Pendiente") {
                                        OutlinedButton(
                                            onClick = { viewModel.actualizarEstado(reporte, "Pendiente") },
                                            modifier = Modifier.weight(1f)
                                        ) { Text("Pendiente") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        BotonVolver(texto = "Volver al panel", onClick = onBack)
    }
}
