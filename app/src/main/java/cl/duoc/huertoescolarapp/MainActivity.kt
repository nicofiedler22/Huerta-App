package cl.duoc.huertoescolarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.huertoescolarapp.ui.components.AccesoDenegado
import cl.duoc.huertoescolarapp.ui.screens.*
import cl.duoc.huertoescolarapp.ui.theme.HuertoEscolarAppTheme
import cl.duoc.huertoescolarapp.ui.viewmodel.CultivoViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.SectorViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.SesionViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.TareaViewModel
import cl.duoc.huertoescolarapp.ui.viewmodel.ReporteViewModel

class MainActivity : ComponentActivity() {

    // Inicializamos nuestros ViewModels de manera segura
    private val sesionViewModel: SesionViewModel by viewModels()
    private val sectorViewModel: SectorViewModel by viewModels()
    private val cultivoViewModel: CultivoViewModel by viewModels()
    private val tareaViewModel: TareaViewModel by viewModels()
    private val reporteViewModel: ReporteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HuertoEscolarAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Respeta la barra de estado, la barra de navegación, el notch y el teclado.
                    // El contenido se centra y limita su ancho para verse bien en tablets y horizontal.
                    Box(
                        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(modifier = Modifier.widthIn(max = 640.dp).fillMaxSize()) {
                            // Control de navegación principal de la app (se conserva al girar la pantalla)
                            var pantalla by rememberSaveable { mutableStateOf("dashboard") }
                            var volverA by rememberSaveable { mutableStateOf("dashboard") }
                            var idSeleccionado by rememberSaveable { mutableStateOf(0L) }
                            val usuario = sesionViewModel.usuarioActual

                            // Cambia de pantalla indicando a cuál se vuelve y, si hace falta, un id
                            val ir: (String, String, Long) -> Unit = { destino, volver, id ->
                                pantalla = destino
                                volverA = volver
                                idSeleccionado = id
                            }

                            BackHandler(enabled = usuario != null && pantalla != "dashboard") {
                                // Los detalles vuelven siempre a su listado
                                pantalla = when (pantalla) {
                                    "sector_detalle" -> "sector_list"
                                    "tarea_detalle" -> "tarea_lista"
                                    else -> volverA
                                }
                            }

                            if (usuario == null) {
                                // Nadie ha iniciado sesión: solo se ve el login
                                LoginScreen(viewModel = sesionViewModel)
                            } else {
                                val esAdmin = usuario.rol == "Administrador"
                                val alInicio = { ir("dashboard", "dashboard", 0L) }

                                // Transición animada entre pantallas
                                Crossfade(targetState = pantalla, label = "navegacion") { destino ->
                                    when (destino) {
                                        "dashboard" -> if (esAdmin) {
                                            AdminScreen(
                                                nombreUsuario = usuario.nombre,
                                                tareaViewModel = tareaViewModel,
                                                reporteViewModel = reporteViewModel,
                                                onNavigateToSectorForm = {
                                                    sectorViewModel.nuevoSector()
                                                    ir("sector_form", "dashboard", 0L)
                                                },
                                                onNavigateToSectorList = { ir("sector_list", "dashboard", 0L) },
                                                onNavigateToCultivos = {
                                                    cultivoViewModel.nuevoCultivo()
                                                    ir("cultivo_form", "dashboard", 0L)
                                                },
                                                onNavigateToTareas = {
                                                    tareaViewModel.nuevaTarea()
                                                    ir("tarea_form", "dashboard", 0L)
                                                },
                                                onNavigateToTareaLista = { ir("tarea_lista", "dashboard", 0L) },
                                                onNavigateToRevision = { ir("admin_revision", "dashboard", 0L) },
                                                onNavigateToReportes = { ir("reportes", "dashboard", 0L) },
                                                onNavigateToHistorial = { ir("historial", "dashboard", 0L) },
                                                onBackToHome = {
                                                    sesionViewModel.cerrarSesion()
                                                    ir("dashboard", "dashboard", 0L)
                                                }
                                            )
                                        } else {
                                            EstudianteTareaScreen(
                                                viewModel = tareaViewModel,
                                                nombreEstudiante = usuario.nombre,
                                                onVerTareas = { ir("tarea_lista", "dashboard", 0L) },
                                                onReportarProblema = {
                                                    reporteViewModel.nuevoReporte()
                                                    ir("reporte_form", "dashboard", 0L)
                                                },
                                                onVerReportes = { ir("reportes", "dashboard", 0L) },
                                                onVerHistorial = { ir("historial", "dashboard", 0L) },
                                                onBack = {
                                                    sesionViewModel.cerrarSesion()
                                                    ir("dashboard", "dashboard", 0L)
                                                }
                                            )
                                        }

                                        // ----- Pantallas compartidas (cada una aplica sus propios permisos) -----
                                        "tarea_lista" -> TareaListaScreen(
                                            viewModel = tareaViewModel,
                                            esAdmin = esAdmin,
                                            onAbrirTarea = { id -> ir("tarea_detalle", "tarea_lista", id) },
                                            onNuevaTarea = {
                                                tareaViewModel.nuevaTarea()
                                                ir("tarea_form", "tarea_lista", 0L)
                                            },
                                            onBack = { alInicio() }
                                        )

                                        "tarea_detalle" -> TareaDetalleScreen(
                                            tareaId = idSeleccionado,
                                            viewModel = tareaViewModel,
                                            esAdmin = esAdmin,
                                            onEditar = { tarea ->
                                                tareaViewModel.editarTarea(tarea)
                                                ir("tarea_form", "tarea_detalle", tarea.id)
                                            },
                                            onBack = { ir("tarea_lista", "dashboard", 0L) }
                                        )

                                        "reportes" -> AdminReportesScreen(
                                            viewModel = reporteViewModel,
                                            esAdmin = esAdmin,
                                            onNuevoReporte = {
                                                reporteViewModel.nuevoReporte()
                                                ir("reporte_form", "reportes", 0L)
                                            },
                                            onBack = { alInicio() }
                                        )

                                        "reporte_form" -> ReporteFormScreen(
                                            viewModel = reporteViewModel,
                                            onBack = { ir("reportes", "dashboard", 0L) }
                                        )

                                        "historial" -> HistorialScreen(
                                            viewModel = tareaViewModel,
                                            onBack = { alInicio() }
                                        )

                                        // ----- Pantallas exclusivas del administrador -----
                                        else -> if (!esAdmin) {
                                            AccesoDenegado(onBack = { alInicio() })
                                        } else {
                                            when (destino) {
                                                "sector_form" -> SectorScreen(
                                                    viewModel = sectorViewModel,
                                                    onBack = { ir(volverA, "dashboard", idSeleccionado) }
                                                )
                                                "sector_list" -> SectorListScreen(
                                                    viewModel = sectorViewModel,
                                                    onNuevo = {
                                                        sectorViewModel.nuevoSector()
                                                        ir("sector_form", "sector_list", 0L)
                                                    },
                                                    onAbrir = { sector -> ir("sector_detalle", "sector_list", sector.id) },
                                                    onEditar = { sector ->
                                                        sectorViewModel.editarSector(sector)
                                                        ir("sector_form", "sector_list", 0L)
                                                    },
                                                    onBack = { alInicio() }
                                                )
                                                "sector_detalle" -> SectorDetalleScreen(
                                                    sectorId = idSeleccionado,
                                                    sectorViewModel = sectorViewModel,
                                                    cultivoViewModel = cultivoViewModel,
                                                    onNuevoCultivo = {
                                                        cultivoViewModel.nuevoCultivo(idSeleccionado)
                                                        ir("cultivo_form", "sector_detalle", idSeleccionado)
                                                    },
                                                    onEditarCultivo = { cultivo ->
                                                        cultivoViewModel.editarCultivo(cultivo)
                                                        ir("cultivo_form", "sector_detalle", cultivo.sectorId)
                                                    },
                                                    onBack = { ir("sector_list", "dashboard", 0L) }
                                                )
                                                "cultivo_form" -> CultivoScreen(
                                                    viewModel = cultivoViewModel,
                                                    onBack = { ir(volverA, "dashboard", idSeleccionado) }
                                                )
                                                "tarea_form" -> TareaScreen(
                                                    viewModel = tareaViewModel,
                                                    onBack = { ir(volverA, "dashboard", idSeleccionado) }
                                                )
                                                "admin_revision" -> AdminRevisionScreen(
                                                    viewModel = tareaViewModel,
                                                    onAbrirTarea = { id -> ir("tarea_detalle", "admin_revision", id) },
                                                    onBack = { alInicio() }
                                                )
                                                else -> AccesoDenegado(onBack = { alInicio() })
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
