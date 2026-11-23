package cl.duoc.huertoescolarapp.data.repository

import android.content.Context
import android.util.Log
import cl.duoc.huertoescolarapp.data.entity.*
import cl.duoc.huertoescolarapp.util.fechaHoy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Repositorio REST de HuertApp.
 * La fuente persistente es MySQL a través del backend Spring Boot.
 * Las listas se consultan periódicamente para reflejar cambios hechos desde otros clientes.
 */
class HuertoRepository private constructor() {
    companion object {
        @Volatile private var instancia: HuertoRepository? = null
        fun obtener(context: Context): HuertoRepository =
            instancia ?: synchronized(this) { instancia ?: HuertoRepository().also { instancia = it } }

        private const val TAG = "HuertoAPI"
        // Android Emulator -> localhost del computador donde se ejecuta Spring Boot.
        // En un teléfono físico, reemplazar por la IP LAN del computador.
        private const val BASE_URL = "http://10.0.2.2:8080/api"
        const val USUARIOS = "usuarios"
        const val SECTORES = "sectores"
        const val CULTIVOS = "cultivos"
        const val TAREAS = "tareas"
        const val VALIDACIONES = "validaciones"
        const val EVIDENCIAS = "evidencias"
        const val REPORTES = "reportes"
    }

    private suspend fun request(path: String, method: String = "GET", body: JSONObject? = null): String =
        withContext(Dispatchers.IO) {
            val connection = (URL("$BASE_URL/$path").openConnection() as HttpURLConnection)
            try {
                connection.requestMethod = method
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                connection.setRequestProperty("Accept", "application/json")
                if (body != null) {
                    connection.doOutput = true
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
                }
                val code = connection.responseCode
                val stream = if (code in 200..299) connection.inputStream else connection.errorStream
                val response = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (code !in 200..299) throw IllegalStateException("API $method $path respondió $code: $response")
                response
            } finally { connection.disconnect() }
        }

    private fun json(vararg values: Pair<String, Any?>): JSONObject {
        val obj = JSONObject()
        values.forEach { (key, value) -> obj.put(key, value ?: JSONObject.NULL) }
        return obj
    }
    private fun JSONObject.str(key: String) = if (isNull(key)) "" else optString(key, "")
    private fun JSONObject.long(key: String) = optLong(key, 0L)
    private fun JSONObject.int(key: String) = optInt(key, 0)
    private fun JSONObject.nullableLong(key: String): Long? = if (isNull(key)) null else optLong(key)

    private fun usuario(o: JSONObject) = UsuarioEntity(o.long("id"), o.str("nombre"), o.str("correo"), o.str("clave"), o.str("rol"))
    private fun sector(o: JSONObject) = SectorEntity(o.long("id"), o.str("nombre"), o.str("ubicacion"), o.str("descripcion"), o.str("estado"))
    private fun cultivo(o: JSONObject) = CultivoEntity(o.long("id"), o.long("sectorId"), o.str("nombreCultivo"), o.str("fechaSiembra"), o.str("fechaCosechaEstimada"), o.str("estadoDesarrollo"), o.int("frecuenciaRiegoDias"))
    private fun tarea(o: JSONObject) = TareaEntity(o.long("id"), o.long("sectorId"), o.long("responsableId"), o.str("titulo"), o.str("descripcion"), o.str("tipo"), o.str("fechaProgramada"), o.str("estado"), o.str("comentariosEstudiante"))
    private fun validacion(o: JSONObject) = ValidacionEntity(o.long("id"), o.long("tareaId"), o.long("validadorId"), o.str("resultado"), o.str("motivo"), o.str("fecha"))
    private fun evidencia(o: JSONObject) = EvidenciaEntity(o.long("id"), o.long("tareaId"), o.str("rutaFoto"), o.str("fecha"))
    private fun reporte(o: JSONObject) = ReporteProblemaEntity(o.long("id"), o.long("sectorId"), o.nullableLong("cultivoId"), o.long("reportanteId"), o.str("descripcion"), o.str("fecha"), o.str("estadoAtencion"), o.str("fotoRuta"))

    private fun <T> observar(path: String, convertir: (JSONObject) -> T, ordenar: (List<T>) -> List<T> = { it }): Flow<List<T>> = flow {
        while (true) {
            try {
                val array = JSONArray(request(path))
                val lista = (0 until array.length()).map { convertir(array.getJSONObject(it)) }
                emit(ordenar(lista))
            } catch (e: Exception) {
                // Conserva el último estado válido si el servidor está temporalmente fuera de línea.
                Log.e(TAG, "No se pudo actualizar $path; se conserva el último estado disponible", e)
            }
            delay(2500)
        }
    }

    val usuarios: Flow<List<UsuarioEntity>> = observar("usuarios", ::usuario) { it.sortedBy { x -> x.id } }
    val colaboradores: Flow<List<UsuarioEntity>> = observar("usuarios", ::usuario) { it.filter { x -> x.rol.equals("Colaborador", true) }.sortedBy { x -> x.id } }
    val sectores: Flow<List<SectorEntity>> = observar("sectores", ::sector) { it.sortedBy { x -> x.id } }
    val cultivos: Flow<List<CultivoEntity>> = observar("cultivos", ::cultivo) { it.sortedBy { x -> x.id } }
    val tareas: Flow<List<TareaEntity>> = observar("tareas", ::tarea) { it.sortedBy { x -> x.id } }
    val validaciones: Flow<List<ValidacionEntity>> = observar("validaciones", ::validacion) { it.sortedBy { x -> x.id } }
    val evidencias: Flow<List<EvidenciaEntity>> = observar("evidencias", ::evidencia) { it.sortedBy { x -> x.id } }
    val reportes: Flow<List<ReporteProblemaEntity>> = observar("reportes", ::reporte) { it.sortedByDescending { x -> x.id } }

    suspend fun iniciarSesion(correo: String, clave: String): UsuarioEntity? = try {
        usuariosUnaVez().firstOrNull { it.correo.equals(correo.trim(), true) && it.clave == clave }
    } catch (e: Exception) {
        Log.e(TAG, "No se pudo autenticar contra la API", e)
        null
    }
    private suspend fun usuariosUnaVez() = JSONArray(request("usuarios")).let { a -> (0 until a.length()).map { usuario(a.getJSONObject(it)) } }
    suspend fun obtenerUsuario(id: Long): UsuarioEntity? = try { usuario(JSONObject(request("usuarios/$id"))) } catch (_: Exception) { null }

    private suspend fun insertar(path: String, body: JSONObject): JSONObject = JSONObject(request(path, "POST", body))
    private suspend fun actualizar(path: String, body: JSONObject) { request(path, "PUT", body) }
    private suspend fun borrar(path: String) { request(path, "DELETE") }

    suspend fun guardarSector(x: SectorEntity) { insertar("sectores", json("nombre" to x.nombre, "ubicacion" to x.ubicacion, "descripcion" to x.descripcion, "estado" to x.estado)) }
    suspend fun actualizarSector(x: SectorEntity) { actualizar("sectores/${x.id}", json("id" to x.id, "nombre" to x.nombre, "ubicacion" to x.ubicacion, "descripcion" to x.descripcion, "estado" to x.estado)) }
    suspend fun eliminarSector(x: SectorEntity) { borrar("sectores/${x.id}") }
    fun obtenerSectorPorId(idSector: Long): Flow<SectorEntity?> = flow { while (true) { emit(try { sector(JSONObject(request("sectores/$idSector"))) } catch (_: Exception) { null }); delay(2500) } }

    suspend fun guardarCultivo(x: CultivoEntity) { insertar("cultivos", cultivoJson(x)) }
    suspend fun actualizarCultivo(x: CultivoEntity) { actualizar("cultivos/${x.id}", cultivoJson(x).put("id", x.id)) }
    suspend fun eliminarCultivo(x: CultivoEntity) { borrar("cultivos/${x.id}") }
    fun obtenerCultivosPorSector(idSector: Long): Flow<List<CultivoEntity>> = observar("cultivos", ::cultivo) { it.filter { x -> x.sectorId == idSector }.sortedBy { x -> x.id } }

    suspend fun guardarTarea(x: TareaEntity) { insertar("tareas", tareaJson(x)) }
    suspend fun actualizarTarea(x: TareaEntity) { actualizar("tareas/${x.id}", tareaJson(x).put("id", x.id)) }
    suspend fun eliminarTarea(x: TareaEntity) { borrar("tareas/${x.id}") }

    suspend fun guardarValidacion(x: ValidacionEntity) { insertar("validaciones", validacionJson(x)) }
    suspend fun guardarEvidencia(x: EvidenciaEntity) { insertar("evidencias", evidenciaJson(x)) }
    suspend fun guardarReporte(x: ReporteProblemaEntity) { insertar("reportes", reporteJson(x)) }
    suspend fun actualizarReporte(x: ReporteProblemaEntity) { actualizar("reportes/${x.id}", reporteJson(x).put("id", x.id)) }

    private fun cultivoJson(x: CultivoEntity) = json("sectorId" to x.sectorId, "nombreCultivo" to x.nombreCultivo, "fechaSiembra" to x.fechaSiembra, "fechaCosechaEstimada" to x.fechaCosechaEstimada, "estadoDesarrollo" to x.estadoDesarrollo, "frecuenciaRiegoDias" to x.frecuenciaRiegoDias)
    private fun tareaJson(x: TareaEntity) = json("sectorId" to x.sectorId, "responsableId" to x.responsableId, "titulo" to x.titulo, "descripcion" to x.descripcion, "tipo" to x.tipo, "fechaProgramada" to x.fechaProgramada, "estado" to x.estado, "comentariosEstudiante" to x.comentariosEstudiante)
    private fun validacionJson(x: ValidacionEntity) = json("tareaId" to x.tareaId, "validadorId" to x.validadorId, "resultado" to x.resultado, "motivo" to x.motivo, "fecha" to x.fecha)
    private fun evidenciaJson(x: EvidenciaEntity) = json("tareaId" to x.tareaId, "rutaFoto" to x.rutaFoto, "fecha" to x.fecha)
    private fun reporteJson(x: ReporteProblemaEntity) = json("sectorId" to x.sectorId, "cultivoId" to x.cultivoId, "reportanteId" to x.reportanteId, "descripcion" to x.descripcion, "fecha" to x.fecha, "estadoAtencion" to x.estadoAtencion, "fotoRuta" to x.fotoRuta)

    // Los datos iniciales se crean en MySQL mediante data.sql. Solo se agregan tareas,
    // validaciones y reportes de demostración si la API aún no contiene tareas.
    suspend fun sembrarDatosDemo() {
        try {
        val existentes = JSONArray(request("tareas"))
        if (existentes.length() > 0) return

        val u = usuariosUnaVez()
        val admin = u.firstOrNull { it.rol.equals("Administrador", true) }?.id ?: return
        val estudiantes = u.filter { it.rol.equals("Colaborador", true) }
        if (estudiantes.size < 3) return

        val sectoresApi = JSONArray(request("sectores"))
        if (sectoresApi.length() < 3) return
        val idsSector = (0 until sectoresApi.length()).map { sectoresApi.getJSONObject(it).long("id") }
        val idNorte = idsSector[0]
        val idInvernadero = idsSector[1]
        val idHierbas = idsSector[2]

        // Cultivos iniciales equivalentes a los que usaba Firestore.
        val cultivosDemo = listOf(
            CultivoEntity(sectorId=idNorte, nombreCultivo="Lechuga", fechaSiembra=fechaHoy(-20), fechaCosechaEstimada=fechaHoy(25), estadoDesarrollo="Crecimiento", frecuenciaRiegoDias=2),
            CultivoEntity(sectorId=idNorte, nombreCultivo="Zanahoria", fechaSiembra=fechaHoy(-5), fechaCosechaEstimada=fechaHoy(60), estadoDesarrollo="Germinación", frecuenciaRiegoDias=3),
            CultivoEntity(sectorId=idInvernadero, nombreCultivo="Tomate", fechaSiembra=fechaHoy(-30), fechaCosechaEstimada=fechaHoy(40), estadoDesarrollo="Crecimiento", frecuenciaRiegoDias=2),
            CultivoEntity(sectorId=idHierbas, nombreCultivo="Albahaca", fechaSiembra=fechaHoy(-50), fechaCosechaEstimada=fechaHoy(3), estadoDesarrollo="Cosecha", frecuenciaRiegoDias=1)
        )
        val cultivosCreados = cultivosDemo.map { insertar("cultivos", cultivoJson(it)).getLong("id") }
        val idTomate = cultivosCreados[2]
        val idEst1 = estudiantes[0].id
        val idEst2 = estudiantes[1].id
        val idEst3 = estudiantes[2].id

        fun demo(sec: Long, resp: Long, titulo: String, desc: String, tipo: String, dias: Int, estado: String, comentario: String = "") =
            TareaEntity(sectorId=sec, responsableId=resp, titulo=titulo, descripcion=desc, tipo=tipo,
                fechaProgramada=fechaHoy(dias), estado=estado, comentariosEstudiante=comentario)

        val tareasCreadas = listOf(
            demo(idNorte,idEst1,"Regar lechugas","Regar las dos hileras de lechuga con la manguera.","Riego",1,"Pendiente"),
            demo(idNorte,idEst1,"Sembrar zanahorias","Sembrar una hilera nueva de zanahorias.","Siembra",-3,"En desarrollo"),
            demo(idInvernadero,idEst2,"Revisar plagas en tomates","Revisar hojas y tallos y anotar lo que encuentres.","Observación",2,"Pendiente de validación","Encontré hojas con manchas amarillas."),
            demo(idHierbas,idEst2,"Podar albahaca","Podar las puntas de la albahaca.","Mantenimiento",-6,"Validada","Listo, quedó podada."),
            demo(idInvernadero,idEst3,"Regar tomates","Regar las dos hileras del invernadero.","Riego",-2,"Rechazada","Regué rápido."),
            demo(idNorte,idEst3,"Quitar malezas","Sacar las malezas entre las hileras.","Mantenimiento",5,"Pendiente")
        ).map { insertar("tareas", tareaJson(it)).getLong("id") }

        insertar("validaciones", validacionJson(ValidacionEntity(tareaId=tareasCreadas[3], validadorId=admin, resultado="Validada", motivo="", fecha=fechaHoy(-5))))
        insertar("validaciones", validacionJson(ValidacionEntity(tareaId=tareasCreadas[4], validadorId=admin, resultado="Rechazada", motivo="Faltó regar la segunda hilera.", fecha=fechaHoy(-1))))

        insertar("reportes", reporteJson(ReporteProblemaEntity(sectorId=idInvernadero, cultivoId=idTomate, reportanteId=idEst1,
            descripcion="Hay hojas amarillas en dos plantas.", fecha=fechaHoy(-2), estadoAtencion="Pendiente")))
        insertar("reportes", reporteJson(ReporteProblemaEntity(sectorId=idNorte, cultivoId=null, reportanteId=idEst2,
            descripcion="Se ven caracoles comiendo las hojas de la lechuga.", fecha=fechaHoy(-1), estadoAtencion="En revisión")))
        } catch (e: Exception) {
            Log.e(TAG, "No se pudieron inicializar los datos de demostración; verifica que el backend esté activo", e)
        }
    }
}
