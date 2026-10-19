package cl.duoc.huertoescolarapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cultivos",
    // Clave foránea: cada cultivo pertenece a un sector que existe
    foreignKeys = [
        ForeignKey(
            entity = SectorEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sectorId"])]
)
data class CultivoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sectorId: Long, // Relación con el sector
    val nombreCultivo: String,
    val fechaSiembra: String,
    val fechaCosechaEstimada: String,
    val estadoDesarrollo: String, // Ej: Germinación, Crecimiento, Cosecha
    val frecuenciaRiegoDias: Int
)
