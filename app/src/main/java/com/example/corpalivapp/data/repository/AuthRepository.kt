package com.example.corpalivapp.data.repository

import com.example.corpalivapp.data.local.UserPreferences
import com.example.corpalivapp.data.model.Cuenta
import com.example.corpalivapp.data.model.Usuario
import com.example.corpalivapp.data.security.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repositorio de autenticación.
 * Concentra el registro, el inicio de sesión y la sesión activa.
 */
class AuthRepository(private val preferences: UserPreferences) {

    fun sesionEmail(): Flow<String?> = preferences.sesionEmail

    fun portadaPath(): Flow<String?> = preferences.portadaPath

    fun avatarPath(): Flow<String?> = preferences.avatarPath

    suspend fun registrar(
        nombre: String,
        email: String,
        telefono: String,
        fechaNacimiento: String,
        notificaciones: Boolean,
        password: String
    ): Result<Usuario> {
        val correo = email.trim().lowercase()
        if (preferences.buscarCuenta(correo) != null) {
            return Result.failure(Exception("Ya existe una cuenta registrada con este correo."))
        }

        val salt = PasswordHasher.generarSalt()
        val hash = withContext(Dispatchers.Default) {
            PasswordHasher.hash(password, salt)
        }
        val usuario = Usuario(
            nombre = nombre.trim(),
            email = correo,
            telefono = telefono.trim(),
            fechaNacimiento = fechaNacimiento.trim(),
            notificaciones = notificaciones
        )

        preferences.guardarCuenta(
            Cuenta(
                usuario = usuario,
                passwordHash = hash,
                salt = salt
            )
        )
        preferences.guardarSesion(usuario.email)
        return Result.success(usuario)
    }

    suspend fun iniciarSesion(email: String, password: String): Result<Usuario> {
        val cuenta = preferences.buscarCuenta(email)
            ?: return Result.failure(Exception("No existe una cuenta con ese correo."))

        val esValida = withContext(Dispatchers.Default) {
            PasswordHasher.verificar(password, cuenta.salt, cuenta.passwordHash)
        }

        return if (esValida) {
            preferences.guardarSesion(cuenta.usuario.email)
            Result.success(cuenta.usuario)
        } else {
            Result.failure(Exception("La contraseña es incorrecta. Inténtalo nuevamente."))
        }
    }

    suspend fun cerrarSesion() {
        preferences.guardarSesion(null)
    }

    suspend fun obtenerUsuario(email: String): Usuario? {
        return preferences.buscarCuenta(email)?.usuario
    }

    suspend fun guardarPortada(path: String?) = preferences.guardarPortada(path)

    suspend fun guardarAvatar(path: String?) = preferences.guardarAvatar(path)
}
