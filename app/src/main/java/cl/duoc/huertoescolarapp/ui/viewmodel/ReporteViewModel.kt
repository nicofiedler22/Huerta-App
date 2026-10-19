package cl.duoc.huertoescolarapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.ReporteProblemaEntity
import cl.duoc.huertoescolarapp.data.repository.HuertoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReporteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HuertoRepository(AppDatabase.getDatabase(application))

    val listaReportes: StateFlow<List<ReporteProblemaEntity>> = repository.reportes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun actualizarEstado(reporte: ReporteProblemaEntity, nuevoEstado: String) {
        viewModelScope.launch {
            repository.actualizarReporte(reporte.copy(estadoAtencion = nuevoEstado))
        }
    }
}
