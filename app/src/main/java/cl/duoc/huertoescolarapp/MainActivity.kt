package cl.duoc.huertoescolarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cl.duoc.huertoescolarapp.ui.screens.*
import cl.duoc.huertoescolarapp.ui.theme.HuertoEscolarAppTheme
import cl.duoc.huertoescolarapp.ui.viewmodel.CultivoViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.SectorViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.SesionViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel

class MainActivity : ComponentActivity() {

    // Inicializamos nuestros ViewModels de manera segura
    private val sesionViewModel: SesionViewModel by viewModels()
    private val sectorViewModel: SectorViewModel by viewModels()
    private val cultivoViewModel: CultivoViewModel by viewModels()
    private val tareaViewModel: TareaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuertoEscolarAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Respeta la barra de estado, la barra de navegación, el notch y el teclado
                    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                        // Control de navegación principal de la app
                        var currentScreen by remember { mutableStateOf("dashboard") }
                        val usuario = sesionViewModel.usuarioActual

                        if (usuario == null) {
                            // Nadie ha iniciado sesión: solo se ve el login
                            LoginScreen(viewModel = sesionViewModel)
                        } else if (usuario.rol == "Administrador") {
                            // Pantallas exclusivas del administrador
                            when (currentScreen) {
                                "dashboard" -> AdminScreen(
                                    nombreUsuario = usuario.nombre,
                                    onNavigateToSectorForm = { currentScreen = "sector_form" },
                                    onNavigateToSectorList = { currentScreen = "sector_list" },
                                    onNavigateToCultivos = { currentScreen = "cultivo_form" },
                                    onNavigateToTareas = { currentScreen = "tarea_form" },
                                    onNavigateToRevision = { currentScreen = "admin_revision" },
                                    onBackToHome = {
                                        sesionViewModel.cerrarSesion()
                                        currentScreen = "dashboard"
                                    }
                                )
                                "sector_form" -> SectorScreen(
                                    viewModel = sectorViewModel,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "sector_list" -> SectorListScreen(
                                    viewModel = sectorViewModel,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "cultivo_form" -> CultivoScreen(
                                    viewModel = cultivoViewModel,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "tarea_form" -> TareaScreen(
                                    viewModel = tareaViewModel,
                                    onBack = { currentScreen = "dashboard" }
                                )
                                "admin_revision" -> AdminRevisionScreen(
                                    viewModel = tareaViewModel,
                                    adminId = usuario.id,
                                    onBack = { currentScreen = "dashboard" }
                                )
                            }
                        } else {
                            // Pantalla del estudiante: solo ve y ejecuta sus propias tareas
                            EstudianteTareaScreen(
                                viewModel = tareaViewModel,
                                usuarioId = usuario.id,
                                nombreEstudiante = usuario.nombre,
                                onBack = {
                                    sesionViewModel.cerrarSesion()
                                    currentScreen = "dashboard"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
