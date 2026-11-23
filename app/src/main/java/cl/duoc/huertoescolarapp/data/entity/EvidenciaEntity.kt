package cl.duoc.huertoescolarapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Foto que el estudiante adjunta a una tarea
@Entity(
    tableName = "evidencias",
    foreignKeys = [
        ForeignKey(
            entity = TareaEntity::class,
            parentColumns = ["id"],
            childColumns = ["tareaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["tareaId"])]
)
data class EvidenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tareaId: Long,
    val rutaFoto: String,
    val fecha: String
)
