package cl.duoc.huertoescolarapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.huertoescolarapp.data.entity.SectorEntity
import cl.duoc.huertoescolarapp.ui.viewmodel.SectorViewModel

@Composable
fun SectorListScreen(
    viewModel: SectorViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    // 📌 Recolectamos la lista de sectores desde el StateFlow del ViewModel de manera reactiva
    val sectores by viewModel.listaSectores.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🌱 Listado de Sectores",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (sectores.isEmpty()) {
            // Estado vacío exigido por la rúbrica si no hay registros
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No hay sectores registrados aún.", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            // LazyColumn optimiza la lista para mostrar muchos elementos fluidamente
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sectores) { sector ->
                    SectorItemCard(sector = sector)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Volver al Panel")
        }
    }
}

@Composable
fun SectorItemCard(sector: SectorEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = sector.nombre, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ubicación: ${sector.ubicacion}", fontSize = 14.sp)
            Text(text = "Estado: ${sector.estado}", fontSize = 14.sp)
            if (sector.descripcion.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Descripción: ${sector.descripcion}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}