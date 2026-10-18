package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminScreen(
    modifier: Modifier = Modifier,
    onNavigateToSectorForm: () -> Unit, // Botón para ir al formulario de sectores
    onBackToHome: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🛠️ Panel de Administrador",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Gestión del Huerto Escolar")

        Spacer(modifier = Modifier.height(32.dp))

        // Botón que lleva al formulario de crear sector
        Button(
            onClick = onNavigateToSectorForm,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Registrar Nuevo Sector", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Volver al Inicio", fontSize = 16.sp)
        }
    }
}