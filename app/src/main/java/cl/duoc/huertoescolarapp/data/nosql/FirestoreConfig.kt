package cl.duoc.huertoescolarapp.data.nosql

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Configuración de Cloud Firestore (base de datos NoSQL orientada a documentos).
 *
 * Reemplaza los 3 valores por los de TU proyecto de Firebase
 * (Consola Firebase > Configuración del proyecto > Tus apps > Android).
 * Con los valores de ejemplo la app funciona igual en modo local (caché sin conexión),
 * pero los datos no se sincronizan con la nube ni se ven en la consola.
 */
object FirestoreConfig {
    private const val API_KEY = "AIzaSyREEMPLAZAR-CON-TU-API-KEY"
    private const val APP_ID = "1:000000000000:android:0000000000000000"
    private const val PROJECT_ID = "huertapp-demo"

    fun obtener(context: Context): FirebaseFirestore {
        if (FirebaseApp.getApps(context).isEmpty()) {
            val opciones = FirebaseOptions.Builder()
                .setApiKey(API_KEY)
                .setApplicationId(APP_ID)
                .setProjectId(PROJECT_ID)
                .build()
            FirebaseApp.initializeApp(context, opciones)
        }
        return FirebaseFirestore.getInstance()
    }
}
