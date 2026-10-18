package cl.duoc.huertoescolarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cl.duoc.huertoescolarapp.ui.screens.AdminScreen
import cl.duoc.fotomotion.ui.screens.HomeScreen
import cl.duoc.huertoescolarapp.ui.screens.EstudianteScreen
import cl.duoc.huertoescolarapp.ui.screens.SectorScreen // <--- Asegúrate de importar tu SectorScreen
import cl.duoc.huertoescolarapp.ui.theme.HuertoEscolarAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuertoEscolarAppTheme {
                // Estado para controlar la pantalla actual
                var pantallaActual by remember { mutableStateOf("home") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (pantallaActual) {
                        "home" -> HomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            onNavigateToAdmin = { pantallaActual = "admin" },
                            onNavigateToStudent = { pantallaActual = "estudiante" }
                        )
                        "admin" -> AdminScreen(
                            modifier = Modifier.padding(innerPadding),
                            onNavigateToSectorForm = { pantallaActual = "sector_form" }, // Salta al formulario
                            onBackToHome = { pantallaActual = "home" }
                        )
                        "sector_form" -> SectorScreen(
                            modifier = Modifier.padding(innerPadding),
                            onBack = { pantallaActual = "admin" } // Permite volver al panel de admin
                        )
                        "estudiante" -> EstudianteScreen(
                            modifier = Modifier.padding(innerPadding),
                            onBackToHome = { pantallaActual = "home" }
                        )
                    }
                }
            }
        }
    }
}