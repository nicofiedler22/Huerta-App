package cl.duoc.huertoescolarapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reportes_problema",
    foreignKeys = [
        ForeignKey(
            entity = SectorEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectorId"],
            onDelete = ForeignKey.CASCADE
        ),
        // El cultivo es opcional: si se borra el cultivo, el reporte queda sin cultivo
        ForeignKey(
            entity = CultivoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cultivoId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["reportanteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sectorId"]), Index(value = ["cultivoId"]), Index(value = ["reportanteId"])]
)
data class ReporteProblemaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val sectorId: Long,
    val cultivoId: Long? = null,
    val reportanteId: Long,
    val descripcion: String,
    val fecha: String,
    val estadoAtencion: String = "Pendiente", // Pendiente, En revisión, Resuelto
    val fotoRuta: String = ""
)
