package cl.duoc.huertoescolarapp.ui.viewmodel

import cl.duoc.huertoescolarapp.data.entity.UsuarioEntity

// Guarda quién tiene la sesión iniciada para que los ViewModels puedan comprobar permisos
// (así la seguridad no depende solo de ocultar botones en la pantalla).
object SesionActual {
    var usuario: UsuarioEntity? = null

    fun esAdmin(): Boolean = usuario?.rol == "Administrador"
    fun esColaborador(): Boolean = usuario?.rol == "Colaborador"
}
