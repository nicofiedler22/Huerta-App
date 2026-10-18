package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EstudianteScreen(
    modifier: Modifier = Modifier,
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
            text = "🌱 Panel de Estudiante",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Aquí podrás ver tus tareas asignadas y enviar evidencias.")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "Volver al Inicio")
        }
    }
}