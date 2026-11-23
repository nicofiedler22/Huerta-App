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

    PanelInicio(
        nombre = nombreEstudiante,
        rol = "Colaborador",
        modifier = modifier,
        avisos = listOf(
            Triple(porHacer, "Por hacer", ColorInfo),
            Triple(atrasadas, "Atrasadas", if (atrasadas > 0) ColorError else ColorInfo),
            Triple(porValidar, "En validación", ColorTareaPorValidar)
        ),
        opciones = listOf(
            Triple("📝", "Mis tareas", onVerTareas),
            Triple("⚠️", "Reportar problema", onReportarProblema),
            Triple("📂", "Mis reportes", onVerReportes),
            Triple("🕘", "Mi historial", onVerHistorial)
        ),
        onCerrarSesion = onBack
    )
}
