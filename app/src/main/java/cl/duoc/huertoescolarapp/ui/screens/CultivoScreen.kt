package cl.duoc.huertoescolarapp.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.huertoescolarapp.ui.viewmodel.CultivoViewModel
import java.util.Calendar

@Composable
fun CultivoScreen(
    viewModel: CultivoViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val sectores by viewModel.listaSectores.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Instancias para los selectores de fecha nativos de Android
    val calendarSiembra = Calendar.getInstance()
    val datePickerDialogSiembra = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            viewModel.fechaSiembra = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
        },
        calendarSiembra.get(Calendar.YEAR),
        calendarSiembra.get(Calendar.MONTH),
        calendarSiembra.get(Calendar.DAY_OF_MONTH)
    )

    val calendarCosecha = Calendar.getInstance()
    val datePickerDialogCosecha = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            viewModel.fechaCosecha = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
        },
        calendarCosecha.get(Calendar.YEAR),
        calendarCosecha.get(Calendar.MONTH),
        calendarCosecha.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌿 Gestión de Cultivos",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Seleccione ID de Sector disponible:", fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (sectores.isEmpty()) {
                Text(text = "No hay sectores creados aún.", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
            } else {
                sectores.forEach { sector ->
                    Button(
                        onClick = { viewModel.sectorIdSeleccionado = sector.id },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.sectorIdSeleccionado == sector.id)
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Sec ${sector.id}", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.nombreCultivo,
            onValueChange = { viewModel.nombreCultivo = it },
            label = { Text("Nombre del Cultivo (ej. Tomate)") },
            isError = viewModel.errorNombre,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.frecuenciaRiego,
            onValueChange = { viewModel.frecuenciaRiego = it },
            label = { Text("Frecuencia de riego (cada cuántos días)") },
            isError = viewModel.errorFrecuencia,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Estado de desarrollo:", fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Germinación", "Crecimiento", "Cosecha").forEach { opcion ->
                Button(
                    onClick = { viewModel.estadoDesarrollo = opcion },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.estadoDesarrollo == opcion)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = opcion, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { datePickerDialogSiembra.show() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = if (viewModel.fechaSiembra.isBlank()) "📅 Seleccionar fecha de siembra"
                else "Fecha de siembra: ${viewModel.fechaSiembra}",
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { datePickerDialogCosecha.show() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = if (viewModel.fechaCosecha.isBlank()) "📅 Seleccionar fecha de cosecha estimada"
                else "Fecha de cosecha estimada: ${viewModel.fechaCosecha}",
                fontSize = 15.sp
            )
        }

        if (viewModel.errorFechas) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Debes seleccionar la fecha de siembra y la fecha de cosecha estimada.",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.validarYGuardar() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Guardar Cultivo", fontSize = 16.sp)
        }

        if (viewModel.registroExitoso) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "¡Cultivo registrado con éxito!", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Volver al Panel", fontSize = 16.sp)
        }
    }
}