package cl.duoc.huertoescolarapp.ui.components

import android.app.DatePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import cl.duoc.huertoescolarapp.ui.theme.ColorError
import cl.duoc.huertoescolarapp.ui.theme.ColorExito
import cl.duoc.huertoescolarapp.util.cargarBitmap
import cl.duoc.huertoescolarapp.util.copiarUriAArchivo
import cl.duoc.huertoescolarapp.util.crearArchivoImagen
import java.io.File
import java.util.Calendar

// Título de cada pantalla
@Composable
fun Encabezado(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary
    )
}

// Campo de texto con mensaje de error debajo e ícono de advertencia
@Composable
fun CampoTexto(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    esClave: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        singleLine = singleLine,
        visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = if (error != null) {
            { Text("⚠️") }
        } else null,
        supportingText = if (error != null) {
            { Text(error) }
        } else null,
        modifier = modifier.fillMaxWidth()
    )
}

// Mensaje de error pequeño (para selectores y fechas que no son campos de texto)
@Composable
fun TextoError(error: String?) {
    AnimatedVisibility(visible = error != null) {
        Text(
            text = "⚠️ ${error ?: ""}",
            color = ColorError,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

// Confirmación de éxito o error de una operación (aparece con animación)
@Composable
fun MensajeEstado(mensaje: String?, exito: Boolean) {
    AnimatedVisibility(visible = mensaje != null) {
        Text(
            text = (if (exito) "✅ " else "⚠️ ") + (mensaje ?: ""),
            color = if (exito) ColorExito else ColorError,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

// Etiqueta de color para los estados (el color cambia con animación)
@Composable
fun EstadoChip(texto: String, color: Color) {
    val colorAnimado by animateColorAsState(targetValue = color, label = "colorEstado")
    Text(
        text = texto,
        color = colorAnimado,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(colorAnimado.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

// Fila de botones para elegir una opción (la seleccionada cambia de color con animación)
@Composable
fun <T> SelectorOpciones(
    opciones: List<Pair<T, String>>,
    seleccionado: T?,
    onSeleccion: (T) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        opciones.forEach { (valor, texto) ->
            val activo = valor == seleccionado
            val colorFondo by animateColorAsState(
                targetValue = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                label = "opcion"
            )
            Button(
                onClick = { onSeleccion(valor) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorFondo,
                    contentColor = if (activo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(text = texto, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun EstadoVacio(texto: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "🌱 $texto", color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun Cargando() {
    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun BotonVolver(texto: String = "Volver", onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(text = texto)
    }
}

@Composable
fun AccesoDenegado(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "⛔ No tienes autorización para ver esta pantalla.", color = ColorError)
        Spacer(modifier = Modifier.height(16.dp))
        BotonVolver(texto = "Volver al inicio", onClick = onBack)
    }
}

@Composable
fun DialogoConfirmar(titulo: String, texto: String, onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = { Text(texto) },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(containerColor = ColorError)
            ) { Text("Eliminar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

// Devuelve una función que abre el selector de fecha nativo y entrega la fecha en dd/MM/yyyy
@Composable
fun rememberSelectorFecha(onFecha: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    val accion by rememberUpdatedState(onFecha)
    val dialogo = remember {
        val calendario = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                accion(String.format("%02d/%02d/%d", dayOfMonth, month + 1, year))
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
    }
    return { dialogo.show() }
}

// Vista previa de una foto guardada en el teléfono
@Composable
fun VistaPrevia(ruta: String, modifier: Modifier = Modifier) {
    val bitmap = remember(ruta) { cargarBitmap(ruta, 400) }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Foto del huerto",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(96.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    } else {
        Text(text = "Imagen no disponible", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ListaVistaPrevia(rutas: List<String>) {
    if (rutas.isEmpty()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rutas.forEach { ruta -> VistaPrevia(ruta = ruta) }
    }
}

// Botones de cámara y galería (Photo Picker). Entrega la ruta de la foto ya guardada en la app.
@Composable
fun SeccionFotos(onFotoAgregada: (String) -> Unit) {
    val context = LocalContext.current
    var aviso by remember { mutableStateOf<String?>(null) }
    var archivoCamara by rememberSaveable { mutableStateOf<String?>(null) }

    // Photo Picker: elegir una foto que ya existe
    val selector = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) {
            aviso = "No se seleccionó ninguna foto."
        } else {
            val ruta = copiarUriAArchivo(context, uri)
            if (ruta == null) {
                aviso = "No se pudo guardar la foto seleccionada."
            } else {
                aviso = null
                onFotoAgregada(ruta)
            }
        }
    }

    // Cámara: tomar una foto nueva
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        val ruta = archivoCamara
        if (exito && ruta != null) {
            aviso = null
            onFotoAgregada(ruta)
        } else {
            // Si se cancela la cámara se borra el archivo vacío
            if (ruta != null) File(ruta).delete()
            aviso = "Se canceló la captura de la foto."
        }
        archivoCamara = null
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    selector.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.weight(1f)
            ) { Text("🖼️ Galería") }

            OutlinedButton(
                onClick = {
                    try {
                        val archivo = crearArchivoImagen(context)
                        archivoCamara = archivo.absolutePath
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
                        camara.launch(uri)
                    } catch (e: Exception) {
                        aviso = "No se pudo abrir la cámara en este dispositivo."
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("📷 Cámara") }
        }
        TextoError(aviso)
    }
}
