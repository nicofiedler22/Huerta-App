package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.ui.components.BotonVolver
import cl.duoc.huertoescolarapp.ui.components.Encabezado
import cl.duoc.huertoescolarapp.ui.components.EstadoChip
import cl.duoc.huertoescolarapp.ui.components.EstadoVacio
import cl.duoc.huertoescolarapp.ui.theme.colorEstadoTarea
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel

// Historial de actividades y validaciones (el estudiante solo ve el de sus tareas)
@Composable
fun HistorialScreen(
    viewModel: TareaViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val tareas by viewModel.listaTareas.collectAsState()
    val validaciones by viewModel.listaValidaciones.collectAsState()
    val usuarios by viewModel.listaUsuarios.collectAsState()
    val eventos = viewModel.construirHistorial(tareas, validaciones, usuarios)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Encabezado("🗂️ Historial de actividades")
        Spacer(modifier = Modifier.height(8.dp))

        if (eventos.isEmpty()) {
            EstadoVacio("Todavía no hay actividades registradas.", Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(eventos) { _, evento ->
                    Card(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = evento.titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text(text = evento.fecha, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = evento.detalle, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            EstadoChip(texto = evento.estado, color = colorEstadoTarea(evento.estado))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        BotonVolver(texto = "Volver al Panel", onClick = onBack)
    }
}
