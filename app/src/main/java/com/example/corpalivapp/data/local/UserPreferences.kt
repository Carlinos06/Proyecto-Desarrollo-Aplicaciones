package com.example.corpalivapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.corpalivapp.data.model.Cuenta
import com.example.corpalivapp.data.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "corpaliv_prefs"
)

/**
 * Persistencia local con DataStore.
 *
 * Guarda las cuentas registradas, la sesión activa y las rutas de las imágenes
 * de portada y avatar. Las contraseñas se guardan ya cifradas (hash + salt).
 */
class UserPreferences(private val context: Context) {

    private val keyUsuarios = stringPreferencesKey("usuarios_json")
    private val keySesion = stringPreferencesKey("sesion_email")
    private val keyPortada = stringPreferencesKey("portada_path")
    private val keyAvatar = stringPreferencesKey("avatar_path")

    val sesionEmail: Flow<String?> = context.dataStore.data.map { it[keySesion] }
    val portadaPath: Flow<String?> = context.dataStore.data.map { it[keyPortada] }
    val avatarPath: Flow<String?> = context.dataStore.data.map { it[keyAvatar] }

    suspend fun obtenerCuentas(): List<Cuenta> {
        val json = context.dataStore.data.first()[keyUsuarios] ?: return emptyList()
        return runCatching {
            val array = JSONArray(json)
            List(array.length()) { indice -> cuentaDesdeJson(array.getJSONObject(indice)) }
        }.getOrDefault(emptyList())
    }

    suspend fun buscarCuenta(email: String): Cuenta? {
        return obtenerCuentas().firstOrNull {
            it.usuario.email.equals(email.trim(), ignoreCase = true)
        }
    }

    suspend fun guardarCuenta(cuenta: Cuenta) {
        val cuentas = obtenerCuentas()
            .filterNot { it.usuario.email.equals(cuenta.usuario.email, ignoreCase = true) } +
            cuenta
        val array = JSONArray()
        cuentas.forEach { array.put(cuentaAJson(it)) }
        context.dataStore.edit { prefs ->
            prefs[keyUsuarios] = array.toString()
        }
    }

    suspend fun guardarSesion(email: String?) {
        context.dataStore.edit { prefs ->
            if (email.isNullOrBlank()) {
                prefs.remove(keySesion)
            } else {
                prefs[keySesion] = email
            }
        }
    }

    suspend fun guardarPortada(path: String?) {
        context.dataStore.edit { prefs ->
            if (path.isNullOrBlank()) prefs.remove(keyPortada) else prefs[keyPortada] = path
        }
    }

    suspend fun guardarAvatar(path: String?) {
        context.dataStore.edit { prefs ->
            if (path.isNullOrBlank()) prefs.remove(keyAvatar) else prefs[keyAvatar] = path
        }
    }

    private fun cuentaAJson(cuenta: Cuenta): JSONObject = JSONObject().apply {
        put("nombre", cuenta.usuario.nombre)
        put("email", cuenta.usuario.email)
        put("telefono", cuenta.usuario.telefono)
        put("fechaNacimiento", cuenta.usuario.fechaNacimiento)
        put("notificaciones", cuenta.usuario.notificaciones)
        put("passwordHash", cuenta.passwordHash)
        put("salt", cuenta.salt)
    }

    private fun cuentaDesdeJson(json: JSONObject): Cuenta = Cuenta(
        usuario = Usuario(
            nombre = json.getString("nombre"),
            email = json.getString("email"),
            telefono = json.getString("telefono"),
            fechaNacimiento = json.getString("fechaNacimiento"),
            notificaciones = json.optBoolean("notificaciones", true)
        ),
        passwordHash = json.getString("passwordHash"),
        salt = json.getString("salt")
    )
}
