package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            text = "🌱 HuertApp 🌱",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión para continuar",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = viewModel.correo,
            onValueChange = { viewModel.correo = it },
            label = { Text("Correo") },
            isError = viewModel.errorCorreo,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.clave,
            onValueChange = { viewModel.clave = it },
            label = { Text("Contraseña") },
            isError = viewModel.errorClave,
            modifier = Modifier.fillMaxWidth()
        )

        if (viewModel.mensajeError.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = viewModel.mensajeError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.iniciarSesion() },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Ingresar", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Cuentas ficticias para probar la app
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Cuentas de prueba", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Administrador: docente@huerto.cl / admin123", fontSize = 12.sp)
                Text(text = "Estudiante 1: estudiante1@huerto.cl / 1234", fontSize = 12.sp)
                Text(text = "Estudiante 2: estudiante2@huerto.cl / 1234", fontSize = 12.sp)
                Text(text = "Estudiante 3: estudiante3@huerto.cl / 1234", fontSize = 12.sp)
            }
        }
    }
}
