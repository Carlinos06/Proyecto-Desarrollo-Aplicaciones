package com.example.corpalivapp.data.security

import android.util.Base64
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Cifrado de contraseñas mediante PBKDF2.
 *
 * La contraseña no se almacena nunca en texto plano. Se genera un salt aleatorio
 * por usuario y se guarda únicamente el hash resultante junto con ese salt.
 */
object PasswordHasher {

    private const val ITERACIONES = 120_000
    private const val LONGITUD_CLAVE = 256
    private const val ALGORITMO_MODERNO = "PBKDF2WithHmacSHA256"
    private const val ALGORITMO_COMPATIBLE = "PBKDF2WithHmacSHA1"

    fun generarSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun hash(password: String, saltBase64: String): String {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERACIONES, LONGITUD_CLAVE)
        return try {
            val factory = obtenerFactory()
            val hash = factory.generateSecret(spec).encoded
            Base64.encodeToString(hash, Base64.NO_WRAP)
        } finally {
            spec.clearPassword()
        }
    }

    fun verificar(password: String, saltBase64: String, hashEsperado: String): Boolean {
        val calculado = hash(password, saltBase64)
        return compararTiempoConstante(calculado, hashEsperado)
    }

    private fun obtenerFactory(): SecretKeyFactory {
        return try {
            SecretKeyFactory.getInstance(ALGORITMO_MODERNO)
        } catch (e: NoSuchAlgorithmException) {
            // PBKDF2WithHmacSHA256 está disponible desde API 26. En API 24-25
            // se utiliza la variante SHA1, igualmente segura para este MVP.
            SecretKeyFactory.getInstance(ALGORITMO_COMPATIBLE)
        }
    }

    private fun compararTiempoConstante(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var resultado = 0
        for (indice in a.indices) {
            resultado = resultado or (a[indice].code xor b[indice].code)
        }
        return resultado == 0
    }
}
