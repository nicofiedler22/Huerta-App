package cl.duoc.huertoescolarapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nombre: String,
    val correo: String,
    val clave: String,
    val rol: String // Administrador o Colaborador
)
