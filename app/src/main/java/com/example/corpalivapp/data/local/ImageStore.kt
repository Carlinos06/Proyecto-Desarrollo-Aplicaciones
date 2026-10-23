package com.example.corpalivapp.data.local

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Copia imágenes seleccionadas o capturadas a almacenamiento interno de la app.
 * Así la imagen sigue disponible aunque el URI original deje de ser accesible.
 */
object ImageStore {

    fun copiarImagen(context: Context, uri: Uri, prefijo: String): String? {
        return runCatching {
            val directorio = File(context.filesDir, "imagenes").apply { mkdirs() }
            val destino = File(directorio, "${prefijo}_${System.currentTimeMillis()}.jpg")
            val entrada = context.contentResolver.openInputStream(uri) ?: return null
            entrada.use { flujoEntrada ->
                destino.outputStream().use { flujoSalida ->
                    flujoEntrada.copyTo(flujoSalida)
                }
            }
            destino.absolutePath
        }.getOrNull()
    }

    fun eliminar(path: String?) {
        if (path.isNullOrBlank()) return
        runCatching {
            val archivo = File(path)
            if (archivo.exists()) archivo.delete()
        }
    }
}
