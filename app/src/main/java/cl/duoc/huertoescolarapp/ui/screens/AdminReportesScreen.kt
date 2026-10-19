package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.huertoescolarapp.ui.viewmodel.ReporteViewModel

@Composable
fun AdminReportesScreen(
    viewModel: ReporteViewModel,
    onBack: () -> Unit
) {
    val reportes by viewModel.listaReportes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Reportes de problemas del huerto",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Revisa los problemas informados y actualiza su estado de atención.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (reportes.isEmpty()) {
            Text("Todavía no hay reportes registrados.")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reportes, key = { it.id }) { reporte ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Reporte #${reporte.id}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Problema: ${reporte.descripcion}")
                            Text("Fecha: ${reporte.fecha}")
                            Text("Sector ID: ${reporte.sectorId}")
                            if (reporte.cultivoId != null) {
                                Text("Cultivo ID: ${reporte.cultivoId}")
                            }
                            Text("Reportado por usuario ID: ${reporte.reportanteId}")
                            Text(
                                text = "Estado: ${reporte.estadoAtencion}",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (reporte.estadoAtencion == "Pendiente") {
                                    Button(
                                        onClick = { viewModel.actualizarEstado(reporte, "En revisión") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("En revisión")
                                    }
                                }
                                if (reporte.estadoAtencion != "Resuelto") {
                                    Button(
                                        onClick = { viewModel.actualizarEstado(reporte, "Resuelto") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Resolver")
                                    }
                                }
                                if (reporte.estadoAtencion != "Pendiente") {
                                    OutlinedButton(
                                        onClick = { viewModel.actualizarEstado(reporte, "Pendiente") },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Pendiente")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al panel")
        }
    }
}
