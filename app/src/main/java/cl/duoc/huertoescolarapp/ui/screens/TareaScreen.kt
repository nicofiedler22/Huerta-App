package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.ui.components.BotonVolver
import cl.duoc.huertoescolarapp.ui.components.CampoTexto
import cl.duoc.huertoescolarapp.ui.components.Encabezado
import cl.duoc.huertoescolarapp.ui.components.EstadoVacio
import cl.duoc.huertoescolarapp.ui.components.MensajeEstado
import cl.duoc.huertoescolarapp.ui.components.SelectorOpciones
import cl.duoc.huertoescolarapp.ui.components.TextoError
import cl.duoc.huertoescolarapp.ui.components.rememberSelectorFecha
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel

// Formulario para crear, editar y asignar tareas (solo administrador)
@Composable
fun TareaScreen(
    viewModel: TareaViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val sectores by viewModel.listaSectores.collectAsState()
    val colaboradores by viewModel.listaColaboradores.collectAsState()

    // Selector de fecha nativo para la fecha programada
    val abrirFecha = rememberSelectorFecha { viewModel.fechaProgramada = it }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Encabezado(if (viewModel.idEdicion == null) "📋 Crear y Asignar Tarea" else "✏️ Editar Tarea")

        Text(text = "Seleccione el sector:", style = MaterialTheme.typography.bodyMedium)
        if (sectores.isEmpty()) {
            EstadoVacio("No hay sectores creados aún.")
        } else {
            SelectorOpciones(
                opciones = sectores.map { it.id to it.nombre },
                seleccionado = viewModel.sectorIdSeleccionado,
                onSeleccion = { viewModel.sectorIdSeleccionado = it }
            )
        }
        TextoError(viewModel.errorSector)

        Text(text = "Tipo de tarea:", style = MaterialTheme.typography.bodyMedium)
        SelectorOpciones(
            opciones = viewModel.tiposTarea.map { it to it },
            seleccionado = viewModel.tipo,
            onSeleccion = { viewModel.tipo = it }
        )

        CampoTexto(
            value = viewModel.titulo,
            onValueChange = { viewModel.titulo = it },
            label = "Título de la Tarea",
            error = viewModel.errorTitulo
        )

        CampoTexto(
            value = viewModel.descripcion,
            onValueChange = { viewModel.descripcion = it },
            label = "Instrucciones / Descripción",
            error = viewModel.errorDescripcion,
            singleLine = false
        )

        // Selección del estudiante responsable (solo estudiantes que existen)
        Text(text = "Estudiante responsable:", style = MaterialTheme.typography.bodyMedium)
        SelectorOpciones(
            opciones = colaboradores.map { it.id to it.nombre },
            seleccionado = viewModel.responsableIdSeleccionado,
            onSeleccion = { viewModel.responsableIdSeleccionado = it }
        )
        TextoError(viewModel.errorResponsable)

        // Fecha programada con selector de calendario
        OutlinedButton(
            onClick = abrirFecha,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = if (viewModel.fechaProgramada.isBlank()) "📅 Seleccionar fecha programada"
                else "Fecha programada: ${viewModel.fechaProgramada}"
            )
        }
        TextoError(viewModel.errorFecha)

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.validarYGuardar() }, modifier = Modifier.fillMaxWidth()) {
            Text(text = if (viewModel.idEdicion == null) "Crear y Asignar Tarea" else "Guardar Cambios")
        }

        MensajeEstado(viewModel.mensaje, viewModel.mensajeExito)

        if (viewModel.idEdicion != null) {
            OutlinedButton(onClick = { viewModel.nuevaTarea() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Cancelar edición")
            }
        }

        BotonVolver(onClick = onBack)
    }
}
