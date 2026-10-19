package cl.duoc.huertoescolarapp.data.repository

import cl.duoc.huertoescolarapp.data.database.AppDatabase
import cl.duoc.huertoescolarapp.data.entity.CultivoEntity
import cl.duoc.huertoescolarapp.data.entity.EvidenciaEntity
import cl.duoc.huertoescolarapp.data.entity.ReporteProblemaEntity
import cl.duoc.huertoescolarapp.data.entity.SectorEntity
import cl.duoc.huertoescolarapp.data.entity.TareaEntity
import cl.duoc.huertoescolarapp.data.entity.UsuarioEntity
import cl.duoc.huertoescolarapp.data.entity.ValidacionEntity
import cl.duoc.huertoescolarapp.util.fechaHoy
import kotlinx.coroutines.flow.Flow

// Repository: es el intermediario entre los ViewModels y los DAO (la fuente de datos).
// Los ViewModels ya no hablan directo con la base de datos.
class HuertoRepository(db: AppDatabase) {

    private val usuarioDao = db.usuarioDao()
    private val sectorDao = db.sectorDao()
    private val cultivoDao = db.cultivoDao()
    private val tareaDao = db.tareaDao()
    private val validacionDao = db.validacionDao()
    private val evidenciaDao = db.evidenciaDao()
    private val reporteDao = db.reporteProblemaDao()

    // ---------- Listas reactivas ----------
    val usuarios: Flow<List<UsuarioEntity>> = usuarioDao.obtenerUsuarios()
    val colaboradores: Flow<List<UsuarioEntity>> = usuarioDao.obtenerUsuariosPorRol("Colaborador")
    val sectores: Flow<List<SectorEntity>> = sectorDao.obtenerSectores()
    val cultivos: Flow<List<CultivoEntity>> = cultivoDao.obtenerTodosLosCultivos()
    val tareas: Flow<List<TareaEntity>> = tareaDao.obtenerTodasLasTareas()
    val validaciones: Flow<List<ValidacionEntity>> = validacionDao.obtenerValidaciones()
    val evidencias: Flow<List<EvidenciaEntity>> = evidenciaDao.obtenerEvidencias()
    val reportes: Flow<List<ReporteProblemaEntity>> = reporteDao.obtenerReportes()

    // ---------- Usuarios ----------
    suspend fun iniciarSesion(correo: String, clave: String): UsuarioEntity? {
        return usuarioDao.iniciarSesion(correo, clave)
    }

    suspend fun obtenerUsuario(id: Long): UsuarioEntity? {
        return usuarioDao.obtenerPorId(id)
    }

    // ---------- Sectores ----------
    suspend fun guardarSector(sector: SectorEntity) {
        sectorDao.insertarSector(sector)
    }

    suspend fun actualizarSector(sector: SectorEntity) {
        sectorDao.actualizarSector(sector)
    }

    suspend fun eliminarSector(sector: SectorEntity) {
        sectorDao.eliminarSector(sector)
    }

    fun obtenerSectorPorId(idSector: Long): Flow<SectorEntity?> {
        return sectorDao.obtenerSectorPorId(idSector)
    }

    // ---------- Cultivos ----------
    suspend fun guardarCultivo(cultivo: CultivoEntity) {
        cultivoDao.insertarCultivo(cultivo)
    }

    suspend fun actualizarCultivo(cultivo: CultivoEntity) {
        cultivoDao.actualizarCultivo(cultivo)
    }

    suspend fun eliminarCultivo(cultivo: CultivoEntity) {
        cultivoDao.eliminarCultivo(cultivo)
    }

    fun obtenerCultivosPorSector(idSector: Long): Flow<List<CultivoEntity>> {
        return cultivoDao.obtenerCultivosPorSector(idSector)
    }

    // ---------- Tareas ----------
    suspend fun guardarTarea(tarea: TareaEntity) {
        tareaDao.insertarTarea(tarea)
    }

    suspend fun actualizarTarea(tarea: TareaEntity) {
        tareaDao.actualizarTarea(tarea)
    }

    suspend fun eliminarTarea(tarea: TareaEntity) {
        tareaDao.eliminarTarea(tarea)
    }

    // ---------- Validaciones, evidencias y reportes ----------
    suspend fun guardarValidacion(validacion: ValidacionEntity) {
        validacionDao.insertarValidacion(validacion)
    }

    suspend fun guardarEvidencia(evidencia: EvidenciaEntity) {
        evidenciaDao.insertarEvidencia(evidencia)
    }

    suspend fun guardarReporte(reporte: ReporteProblemaEntity) {
        reporteDao.insertarReporte(reporte)
    }

    suspend fun actualizarReporte(reporte: ReporteProblemaEntity) {
        reporteDao.actualizarReporte(reporte)
    }

    // ---------- Datos ficticios para demostrar todos los flujos ----------
    // Solo se cargan la primera vez (cuando todavía no hay usuarios)
    suspend fun sembrarDatosDemo() {
        if (usuarioDao.contarUsuarios() > 0) return

        // 1 administrador y 3 estudiantes
        val idAdmin = usuarioDao.insertarUsuario(
            UsuarioEntity(nombre = "Profesora Carolina Soto", correo = "docente@huerto.cl", clave = "admin123", rol = "Administrador")
        )
        val idEst1 = usuarioDao.insertarUsuario(
            UsuarioEntity(nombre = "Estudiante 1", correo = "estudiante1@huerto.cl", clave = "1234", rol = "Colaborador")
        )
        val idEst2 = usuarioDao.insertarUsuario(
            UsuarioEntity(nombre = "Estudiante 2", correo = "estudiante2@huerto.cl", clave = "1234", rol = "Colaborador")
        )
        val idEst3 = usuarioDao.insertarUsuario(
            UsuarioEntity(nombre = "Estudiante 3", correo = "estudiante3@huerto.cl", clave = "1234", rol = "Colaborador")
        )

        // Sectores
        val idNorte = sectorDao.insertarSector(
            SectorEntity(nombre = "Huerto Norte", ubicacion = "Patio principal", descripcion = "Hortalizas de hoja y raíz", estado = "Activo")
        )
        val idInvernadero = sectorDao.insertarSector(
            SectorEntity(nombre = "Invernadero", ubicacion = "Costado del gimnasio", descripcion = "Tomates y pimentones", estado = "Activo")
        )
        val idHierbas = sectorDao.insertarSector(
            SectorEntity(nombre = "Jardín de hierbas", ubicacion = "Entrada del colegio", descripcion = "Hierbas aromáticas", estado = "En descanso")
        )

        // Cultivos
        cultivoDao.insertarCultivo(
            CultivoEntity(sectorId = idNorte, nombreCultivo = "Lechuga", fechaSiembra = fechaHoy(-20), fechaCosechaEstimada = fechaHoy(25), estadoDesarrollo = "Crecimiento", frecuenciaRiegoDias = 2)
        )
        cultivoDao.insertarCultivo(
            CultivoEntity(sectorId = idNorte, nombreCultivo = "Zanahoria", fechaSiembra = fechaHoy(-5), fechaCosechaEstimada = fechaHoy(60), estadoDesarrollo = "Germinación", frecuenciaRiegoDias = 3)
        )
        val idTomate = cultivoDao.insertarCultivo(
            CultivoEntity(sectorId = idInvernadero, nombreCultivo = "Tomate", fechaSiembra = fechaHoy(-30), fechaCosechaEstimada = fechaHoy(40), estadoDesarrollo = "Crecimiento", frecuenciaRiegoDias = 2)
        )
        cultivoDao.insertarCultivo(
            CultivoEntity(sectorId = idHierbas, nombreCultivo = "Albahaca", fechaSiembra = fechaHoy(-50), fechaCosechaEstimada = fechaHoy(3), estadoDesarrollo = "Cosecha", frecuenciaRiegoDias = 1)
        )

        // Tareas en todos los estados del flujo
        tareaDao.insertarTarea(
            TareaEntity(sectorId = idNorte, responsableId = idEst1, titulo = "Regar lechugas", descripcion = "Regar las dos hileras de lechuga con la manguera.", tipo = "Riego", fechaProgramada = fechaHoy(1), estado = "Pendiente")
        )
        tareaDao.insertarTarea(
            TareaEntity(sectorId = idNorte, responsableId = idEst1, titulo = "Sembrar zanahorias", descripcion = "Sembrar una hilera nueva de zanahorias.", tipo = "Siembra", fechaProgramada = fechaHoy(-3), estado = "En desarrollo")
        )
        tareaDao.insertarTarea(
            TareaEntity(sectorId = idInvernadero, responsableId = idEst2, titulo = "Revisar plagas en tomates", descripcion = "Revisar hojas y tallos y anotar lo que encuentres.", tipo = "Observación", fechaProgramada = fechaHoy(2), estado = "Pendiente de validación", comentariosEstudiante = "Encontré hojas con manchas amarillas.")
        )
        val idTareaValidada = tareaDao.insertarTarea(
            TareaEntity(sectorId = idHierbas, responsableId = idEst2, titulo = "Podar albahaca", descripcion = "Podar las puntas de la albahaca.", tipo = "Mantenimiento", fechaProgramada = fechaHoy(-6), estado = "Validada", comentariosEstudiante = "Listo, quedó podada.")
        )
        val idTareaRechazada = tareaDao.insertarTarea(
            TareaEntity(sectorId = idInvernadero, responsableId = idEst3, titulo = "Regar tomates", descripcion = "Regar las dos hileras del invernadero.", tipo = "Riego", fechaProgramada = fechaHoy(-2), estado = "Rechazada", comentariosEstudiante = "Regué rápido.")
        )
        tareaDao.insertarTarea(
            TareaEntity(sectorId = idNorte, responsableId = idEst3, titulo = "Quitar malezas", descripcion = "Sacar las malezas entre las hileras.", tipo = "Mantenimiento", fechaProgramada = fechaHoy(5), estado = "Pendiente")
        )

        // Historial de validaciones
        validacionDao.insertarValidacion(
            ValidacionEntity(tareaId = idTareaValidada, validadorId = idAdmin, resultado = "Validada", motivo = "", fecha = fechaHoy(-5))
        )
        validacionDao.insertarValidacion(
            ValidacionEntity(tareaId = idTareaRechazada, validadorId = idAdmin, resultado = "Rechazada", motivo = "Faltó regar la segunda hilera.", fecha = fechaHoy(-1))
        )

        // Un reporte de problema de ejemplo
        reporteDao.insertarReporte(
            ReporteProblemaEntity(sectorId = idInvernadero, cultivoId = idTomate, reportanteId = idEst1, descripcion = "Hay hojas amarillas en dos plantas.", fecha = fechaHoy(-2), estadoAtencion = "Pendiente")
        )
        reporteDao.insertarReporte(
            ReporteProblemaEntity(sectorId = idNorte, cultivoId = null, reportanteId = idEst2, descripcion = "Se ven caracoles comiendo las hojas de la lechuga.", fecha = fechaHoy(-1), estadoAtencion = "En revisión")
        )
    }
}
