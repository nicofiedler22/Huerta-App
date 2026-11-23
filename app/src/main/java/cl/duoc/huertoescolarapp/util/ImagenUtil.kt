package cl.duoc.huertoescolarapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

// Crea un archivo vacío dentro del almacenamiento interno de la app (carpeta "evidencias")
fun crearArchivoImagen(context: Context): File {
    val carpeta = File(context.filesDir, "evidencias")
    if (!carpeta.exists()) carpeta.mkdirs()
    return File(carpeta, "img_${System.currentTimeMillis()}.jpg")
}

// Copia la foto elegida en el selector a la carpeta de la app para no perder el acceso al reiniciar
fun copiarUriAArchivo(context: Context, uri: Uri): String? {
    return try {
        val destino = crearArchivoImagen(context)
        val entrada = context.contentResolver.openInputStream(uri) ?: return null
        entrada.use { origen ->
            destino.outputStream().use { salida -> origen.copyTo(salida) }
        }
        destino.absolutePath
    } catch (e: Exception) {
        null
    }
}

// Carga la imagen reducida para la vista previa (evita gastar memoria con fotos grandes)
fun cargarBitmap(ruta: String, maxLado: Int): Bitmap? {
    return try {
        val archivo = File(ruta)
        if (!archivo.exists()) return null
        val opciones = BitmapFactory.Options()
        opciones.inJustDecodeBounds = true
        BitmapFactory.decodeFile(ruta, opciones)
        var muestra = 1
        while (opciones.outWidth / muestra > maxLado || opciones.outHeight / muestra > maxLado) {
            muestra *= 2
        }
        val opcionesFinales = BitmapFactory.Options()
        opcionesFinales.inSampleSize = muestra
        BitmapFactory.decodeFile(ruta, opcionesFinales)
    } catch (e: Exception) {
        null
    }
}
