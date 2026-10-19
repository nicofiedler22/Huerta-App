package cl.duoc.huertoescolarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.huertoescolarapp.ui.screens.*
import cl.duoc.huertoescolarapp.ui.theme.HuertoEscolarAppTheme
import cl.duoc.huertoescolarapp.ui.viewmodel.CultivoViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.SectorViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel

class MainActivity : ComponentActivity() {

    // Inicializamos nuestros ViewModels de manera segura
    private val sectorViewModel: SectorViewModel by viewModels()
    private val cultivoViewModel: CultivoViewModel by viewModels()
    private val tareaViewModel: TareaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HuertoEscolarAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Control de navegación principal de la app
                    var currentScreen by remember { mutableStateOf("home") }
                    var usuarioLogueado by remember { mutableStateOf("Estudiante 1") }

                    when (currentScreen) {
                        "home" -> HomeScreen(
                            onNavigateToAdmin = { currentScreen = "admin_dashboard" },
                            onNavigateToEstudiante = { nombreEst ->
                                usuarioLogueado = nombreEst
                                currentScreen = "estudiante_dashboard"
                            }
                        )
                        "admin_dashboard" -> AdminScreen(
                            onNavigateToSectores = { currentScreen = "sector_form" },
                            onNavigateToListaSectores = { currentScreen = "sector_list" },
                            onNavigateToCultivos = { currentScreen = "cultivo_form" },
                            onNavigateToTareas = { currentScreen = "tarea_form" },
                            onNavigateToRevision = { currentScreen = "admin_revision" },
                            onBack = { currentScreen = "home" }
                        )
                        "sector_form" -> SectorScreen(
                            viewModel = sectorViewModel,
                            onBack = { currentScreen = "admin_dashboard" }
                        )
                        "sector_list" -> SectorListScreen(
                            viewModel = sectorViewModel,
                            onBack = { currentScreen = "admin_dashboard" }
                        )
                        "cultivo_form" -> CultivoScreen(
                            viewModel = cultivoViewModel,
                            onBack = { currentScreen = "admin_dashboard" }
                        )
                        "tarea_form" -> TareaScreen(
                            viewModel = tareaViewModel,
                            onBack = { currentScreen = "admin_dashboard" }
                        )
                        "admin_revision" -> AdminRevisionScreen(
                            viewModel = tareaViewModel,
                            onBack = { currentScreen = "admin_dashboard" }
                        )
                        "estudiante_dashboard" -> EstudianteTareaScreen(
                            viewModel = tareaViewModel,
                            nombreEstudianteLogueado = usuarioLogueado,
                            onBack = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}

// 📌 Pantalla de Bienvenida / Selección de Roles (HomeScreen)
@Composable
fun HomeScreen(
    onNavigateToAdmin: () -> Unit,
    onNavigateToEstudiante: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌱 Huerto Escolar App",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToAdmin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ingresar como Administrador (Docente)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Seleccionar Estudiante Colaborador:", fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onNavigateToEstudiante("Estudiante 1") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Ingresar como Estudiante 1")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onNavigateToEstudiante("Estudiante 2") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Ingresar como Estudiante 2")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onNavigateToEstudiante("Estudiante 3") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Ingresar como Estudiante 3")
        }
    }
}

// 📌 Panel Principal del Administrador
@Composable
fun AdminScreen(
    onNavigateToSectores: () -> Unit,
    onNavigateToListaSectores: () -> Unit,
    onNavigateToCultivos: () -> Unit,
    onNavigateToTareas: () -> Unit,
    onNavigateToRevision: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚙️ Panel de Administrador",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onNavigateToSectores, modifier = Modifier.fillMaxWidth()) {
            Text("Crear Nuevo Sector")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToListaSectores, modifier = Modifier.fillMaxWidth()) {
            Text("Ver Listado de Sectores")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToCultivos, modifier = Modifier.fillMaxWidth()) {
            Text("Gestionar Cultivos")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToTareas, modifier = Modifier.fillMaxWidth()) {
            Text("Crear y Asignar Tareas")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onNavigateToRevision, modifier = Modifier.fillMaxWidth()) {
            Text("Revisar Tareas de Estudiantes")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline)
        ) {
            Text("Cerrar Sesión")
        }
    }
}