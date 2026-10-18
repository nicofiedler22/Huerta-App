package cl.duoc.huertoescolarapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sectores")
data class SectorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nombre: String,
    val ubicacion: String,
    val descripcion: String
)