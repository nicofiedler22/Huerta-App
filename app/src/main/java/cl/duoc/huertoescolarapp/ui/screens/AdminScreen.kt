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
import cl.duoc.huertoescolarapp.ui.theme.ColorAdvertencia
import cl.duoc.huertoescolarapp.ui.theme.ColorError
import cl.duoc.huertoescolarapp.ui.theme.ColorInfo
import cl.duoc.huertoescolarapp.ui.viewmodel.ReporteViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel

@Composable
fun AdminScreen(
    nombreUsuario: String,
    tareaViewModel: TareaViewModel,
    reporteViewModel: ReporteViewModel,
    modifier: Modifier = Modifier,
    onNavigateToSectorForm: () -> Unit,
    onNavigateToSectorList: () -> Unit,
    onNavigateToCultivos: () -> Unit,
    onNavigateToTareas: () -> Unit,
    onNavigateToTareaLista: () -> Unit,
    onNavigateToRevision: () -> Unit,
    onNavigateToReportes: () -> Unit,
    onNavigateToHistorial: () -> Unit,
    onBackToHome: () -> Unit
) {
    val scrollState = rememberScrollState()
    val tareas by tareaViewModel.listaTareas.collectAsState()
    val reportes by reporteViewModel.listaReportes.collectAsState()

    val porValidar = tareaViewModel.contarPorValidar(tareas)
    val atrasadas = tareaViewModel.contarAtrasadas(tareas)
    val reportesSinResolver = reportes.count { it.estadoAtencion != "Resuelto" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🛠️ Panel de Administrador",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Bienvenido/a, $nombreUsuario", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = "Gestión completa del Huerto Escolar", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Avisos", style = MaterialTheme.typography.titleMedium)
                EstadoChip(texto = "📝 $porValidar tareas por validar", color = ColorAdvertencia)
                EstadoChip(texto = "⏰ $atrasadas tareas atrasadas", color = if (atrasadas > 0) ColorError else ColorInfo)
                EstadoChip(texto = "🐛 $reportesSinResolver reportes sin resolver", color = if (reportesSinResolver > 0) ColorError else ColorInfo)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val botones = listOf(
            "Registrar Nuevo Sector" to onNavigateToSectorForm,
            "Ver Listado de Sectores" to onNavigateToSectorList,
            "Gestionar Cultivos" to onNavigateToCultivos,
            "Crear y Asignar Tareas" to onNavigateToTareas,
            "Ver Tareas (con filtros)" to onNavigateToTareaLista,
            "Revisar Tareas de Estudiantes" to onNavigateToRevision,
            "Revisar Reportes de Problemas" to onNavigateToReportes,
            "Historial de Actividades" to onNavigateToHistorial
        )
        botones.forEach { (texto, accion) ->
            Button(
                onClick = accion,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(text = texto)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Cerrar Sesión")
        }
    }
}
