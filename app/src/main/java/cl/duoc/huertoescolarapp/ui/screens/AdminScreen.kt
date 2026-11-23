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
import cl.duoc.huertoescolarapp.ui.components.PanelInicio
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
    val tareas by tareaViewModel.listaTareas.collectAsState()
    val reportes by reporteViewModel.listaReportes.collectAsState()

    val porValidar = tareaViewModel.contarPorValidar(tareas)
    val atrasadas = tareaViewModel.contarAtrasadas(tareas)
    val reportesSinResolver = reportes.count { it.estadoAtencion != "Resuelto" }

    PanelInicio(
        nombre = nombreUsuario,
        rol = "Administrador",
        modifier = modifier,
        avisos = listOf(
            Triple(porValidar, "Por validar", ColorAdvertencia),
            Triple(atrasadas, "Atrasadas", if (atrasadas > 0) ColorError else ColorInfo),
            Triple(reportesSinResolver, "Reportes", if (reportesSinResolver > 0) ColorError else ColorInfo)
        ),
        opciones = listOf(
            Triple("➕", "Nuevo sector", onNavigateToSectorForm),
            Triple("🗺️", "Sectores", onNavigateToSectorList),
            Triple("🌿", "Cultivos", onNavigateToCultivos),
            Triple("📋", "Asignar tareas", onNavigateToTareas),
            Triple("🔎", "Ver tareas", onNavigateToTareaLista),
            Triple("✅", "Revisar tareas", onNavigateToRevision),
            Triple("🐛", "Reportes", onNavigateToReportes),
            Triple("🕘", "Historial", onNavigateToHistorial)
        ),
        onCerrarSesion = onBackToHome
    )
}
