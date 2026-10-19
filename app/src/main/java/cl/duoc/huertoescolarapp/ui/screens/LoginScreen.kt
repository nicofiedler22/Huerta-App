package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.huertoescolarapp.ui.components.CampoTexto
import cl.duoc.huertoescolarapp.ui.components.MensajeEstado
import cl.duoc.huertoescolarapp.ui.viewmodel.SesionViewModel

@Composable
fun LoginScreen(
    viewModel: SesionViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌱 Huertapp 🌱",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        CampoTexto(
            value = viewModel.correo,
            onValueChange = { viewModel.correo = it },
            label = "Correo",
            error = viewModel.errorCorreo,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(8.dp))

        CampoTexto(
            value = viewModel.clave,
            onValueChange = { viewModel.clave = it },
            label = "Contraseña",
            error = viewModel.errorClave,
            esClave = true
        )

        MensajeEstado(
            mensaje = viewModel.mensajeError.ifBlank { null },
            exito = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.iniciarSesion() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Ingresar", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Cuentas de prueba", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Administrador: docente@huerto.cl / admin123", style = MaterialTheme.typography.labelSmall)
                Text(text = "Estudiante 1: estudiante1@huerto.cl / 1234", style = MaterialTheme.typography.labelSmall)
                Text(text = "Estudiante 2: estudiante2@huerto.cl / 1234", style = MaterialTheme.typography.labelSmall)
                Text(text = "Estudiante 3: estudiante3@huerto.cl / 1234", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
