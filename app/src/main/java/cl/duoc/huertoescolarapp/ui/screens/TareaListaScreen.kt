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
import cl.duoc.huertoescolarapp.ui.components.BotonVolver
import cl.duoc.huertoescolarapp.ui.components.Encabezado
import cl.duoc.huertoescolarapp.ui.components.EstadoChip
import cl.duoc.huertoescolarapp.ui.components.EstadoVacio
import cl.duoc.huertoescolarapp.ui.components.SelectorOpciones
import cl.duoc.huertoescolarapp.ui.components.rememberSelectorFecha
import cl.duoc.huertoescolarapp.ui.theme.ColorError
import cl.duoc.huertoescolarapp.ui.theme.colorEstadoTarea
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel
import cl.duoc.huertoescolarapp.util.esAtrasada

// Listado de tareas con filtros por estado, fecha y responsable.
// El estudiante solo ve las suyas; el administrador ve todas.
@Composable
fun TareaListaScreen(
    viewModel: TareaViewModel,
    esAdmin: Boolean,
    modifier: Modifier = Modifier,
    onAbrirTarea: (Long) -> Unit,
    onNuevaTarea: () -> Unit,
    onBack: () -> Unit
) {
    val tareas by viewModel.listaTareas.collectAsState()
    val usuarios by viewModel.listaUsuarios.collectAsState()
    val colaboradores by viewModel.listaColaboradores.collectAsState()
    val sectores by viewModel.listaSectores.collectAsState()
    val abrirFecha = rememberSelectorFecha { viewModel.filtroFecha = it }

    val tareasFiltradas = viewModel.filtrar(tareas)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Encabezado(if (esAdmin) "📋 Tareas del Huerto" else "📋 Mis Tareas Asignadas")

        Text(text = "Filtrar por estado:", style = MaterialTheme.typography.bodyMedium)
        SelectorOpciones(
            opciones = viewModel.estadosFiltro.map { it to it },
            seleccionado = viewModel.filtroEstado,
            onSeleccion = { viewModel.filtroEstado = it }
        )

        if (esAdmin) {
            Text(text = "Filtrar por responsable:", style = MaterialTheme.typography.bodyMedium)
            SelectorOpciones(
                opciones = listOf(0L to "Todos") + colaboradores.map { it.id to it.nombre },
                seleccionado = viewModel.filtroResponsableId ?: 0L,
                onSeleccion = { viewModel.filtroResponsableId = if (it == 0L) null else it }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = abrirFecha, modifier = Modifier.weight(1f)) {
                Text(text = if (viewModel.filtroFecha.isBlank()) "📅 Filtrar por fecha" else "Fecha: ${viewModel.filtroFecha}")
            }
            OutlinedButton(onClick = { viewModel.limpiarFiltros() }) {
                Text(text = "Limpiar")
            }
        }

        if (esAdmin) {
            Button(onClick = onNuevaTarea, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Crear nueva tarea")
            }
        }

        if (tareasFiltradas.isEmpty()) {
            EstadoVacio("No hay tareas que coincidan con los filtros.", Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareasFiltradas, key = { it.id }) { tarea ->
                    val atrasada = esAtrasada(tarea.estado, tarea.fechaProgramada)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .clickable { onAbrirTarea(tarea.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = tarea.titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Tipo: ${tarea.tipo} | Sector: ${sectores.find { it.id == tarea.sectorId }?.nombre ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(text = "Responsable: ${viewModel.nombreDe(tarea.responsableId, usuarios)}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Fecha programada: ${tarea.fechaProgramada}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                EstadoChip(texto = tarea.estado, color = colorEstadoTarea(tarea.estado))
                                if (atrasada) EstadoChip(texto = "⏰ Atrasada", color = ColorError)
                            }
                        }
                    }
                }
            }
        }

        BotonVolver(texto = "Volver al Panel", onClick = onBack)
    }
}
