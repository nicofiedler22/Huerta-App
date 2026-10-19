package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.ui.components.EstadoChip
import cl.duoc.huertoescolarapp.ui.theme.ColorError
import cl.duoc.huertoescolarapp.ui.theme.ColorInfo
import cl.duoc.huertoescolarapp.ui.theme.ColorTareaPorValidar
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel

@Composable
fun EstudianteTareaScreen(
    viewModel: TareaViewModel,
    nombreEstudiante: String,
    modifier: Modifier = Modifier,
    onVerTareas: () -> Unit,
    onReportarProblema: () -> Unit,
    onVerReportes: () -> Unit,
    onVerHistorial: () -> Unit,
    onBack: () -> Unit
) {
    val tareas by viewModel.listaTareas.collectAsState()
    val porHacer = viewModel.contarPorHacer(tareas)
    val atrasadas = viewModel.contarAtrasadas(tareas)
    val porValidar = viewModel.tareasVisibles(tareas).count { it.estado == "Pendiente de validación" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌱 Mi Panel de Colaborador",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Bienvenido/a, $nombreEstudiante",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Avisos", style = MaterialTheme.typography.titleMedium)
                EstadoChip(texto = "📌 $porHacer tareas por hacer", color = ColorInfo)
                EstadoChip(texto = "⏰ $atrasadas tareas atrasadas", color = if (atrasadas > 0) ColorError else ColorInfo)
                EstadoChip(texto = "🕒 $porValidar esperando validación", color = ColorTareaPorValidar)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val botones = listOf(
            "Mis Tareas Asignadas" to onVerTareas,
            "Reportar un Problema" to onReportarProblema,
            "Mis Reportes" to onVerReportes,
            "Mi Historial" to onVerHistorial
        )
        botones.forEach { (texto, accion) ->
            Button(onClick = accion, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Text(text = texto)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text(text = "Cerrar Sesión")
        }
    }
}
