package cl.duoc.huertoescolarapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cultivos")
data class CultivoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sectorId: Long, // Relación con el sector
    val nombreCultivo: String,
    val fechaSiembra: String,
    val fechaCosechaEstimada: String,
    val estadoDesarrollo: String // Ej: Germinación, Crecimiento, Cosecha
)