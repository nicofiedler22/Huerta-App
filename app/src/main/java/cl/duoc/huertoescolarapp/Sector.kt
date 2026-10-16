package cl.duoc.huertoescolarapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sectores")
data class Sector(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val estado: String // disponible, en uso, en mantenimiento
)