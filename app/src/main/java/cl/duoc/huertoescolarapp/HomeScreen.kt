package cl.duoc.fotomotion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToAdmin: () -> Unit,
    onNavigateToStudent: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            text = "Selecciona tu perfil para ingresar",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onNavigateToAdmin,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Ingresar como Administrador", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateToStudent,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Ingresar como Estudiante", fontSize = 16.sp)
        }
    }
}