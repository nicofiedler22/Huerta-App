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

@Composable
fun AdminScreen(
    nombreUsuario: String,
    modifier: Modifier = Modifier,
    onNavigateToSectorForm: () -> Unit,
    onNavigateToSectorList: () -> Unit,
    onNavigateToCultivos: () -> Unit,
    onNavigateToTareas: () -> Unit,
    onNavigateToRevision: () -> Unit,
    onBackToHome: () -> Unit
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
            text = "🛠️ Panel de Administrador",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Bienvenido/a, $nombreUsuario", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = "Gestión completa del Huerto Escolar", color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToSectorForm,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Registrar Nuevo Sector", fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNavigateToSectorList,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Ver Listado de Sectores", fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNavigateToCultivos,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Gestionar Cultivos", fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNavigateToTareas,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Crear y Asignar Tareas", fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onNavigateToRevision,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Revisar Tareas de Estudiantes", fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(text = "Cerrar Sesión", fontSize = 15.sp)
        }
    }
}
