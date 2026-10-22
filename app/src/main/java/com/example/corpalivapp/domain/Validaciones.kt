package com.example.corpalivapp.domain

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Reglas de validación separadas de la interfaz.
 * Cada función devuelve null cuando el valor es válido o un mensaje de error.
 */
object Validaciones {

    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val TELEFONO_REGEX = Regex("^\\d{8,9}$")
    private val FECHA_REGEX = Regex("^\\d{2}/\\d{2}/\\d{4}$")
    private val FORMATO_FECHA = "dd/MM/yyyy"

    fun nombre(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "El nombre es obligatorio."
            texto.length < 3 -> "Ingresa al menos 3 caracteres."
            texto.any { !it.isLetter() && !it.isWhitespace() && it != '-' && it != '\'' } ->
                "El nombre solo puede contener letras, espacios, guiones y apóstrofes."
            else -> null
        }
    }

    fun email(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "El correo es obligatorio."
            !EMAIL_REGEX.matches(texto) -> "Ingresa un correo válido (ejemplo@dominio.cl)."
            else -> null
        }
    }

    fun telefono(valor: String): String? {
        val texto = valor.trim()
        return when {
            texto.isEmpty() -> "El teléfono es obligatorio."
            !TELEFONO_REGEX.matches(texto) -> "Ingresa entre 8 y 9 dígitos, sin espacios."
            else -> null
        }
    }

    fun fechaNacimiento(valor: String): String? {
        val texto = valor.trim()
        if (texto.isEmpty()) return "La fecha de nacimiento es obligatoria."
        if (!FECHA_REGEX.matches(texto)) return "Usa el formato dd/MM/yyyy."

        val formato = SimpleDateFormat(FORMATO_FECHA, Locale.getDefault()).apply {
            isLenient = false
        }
        val fecha = runCatching { formato.parse(texto) }.getOrNull()
            ?: return "La fecha ingresada no es válida."
        if (fecha.after(Date())) return "La fecha no puede ser futura."

        val hoy = Calendar.getInstance()
        val nacimiento = Calendar.getInstance().apply { time = fecha }
        var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) edad--

        return when {
            edad < 14 -> "Debes tener al menos 14 años para registrarte."
            edad > 120 -> "Revisa la fecha ingresada."
            else -> null
        }
    }

    fun password(valor: String): String? {
        return when {
            valor.isEmpty() -> "La contraseña es obligatoria."
            valor.length < 8 -> "Debe tener al menos 8 caracteres."
            valor.none { it.isUpperCase() } -> "Debe incluir al menos una mayúscula."
            valor.none { it.isLowerCase() } -> "Debe incluir al menos una minúscula."
            valor.none { it.isDigit() } -> "Debe incluir al menos un número."
            else -> null
        }
    }

    fun confirmarPassword(password: String, confirmacion: String): String? {
        return when {
            confirmacion.isEmpty() -> "Confirma tu contraseña."
            password != confirmacion -> "Las contraseñas no coinciden."
            else -> null
        }
    }

    fun terminos(acepta: Boolean): String? {
        return if (acepta) null else "Debes aceptar los términos para continuar."
    }

    fun fortalezaPassword(valor: String): Int {
        if (valor.isEmpty()) return 0
        var puntos = 0
        if (valor.length >= 8) puntos++
        if (valor.any { it.isUpperCase() } && valor.any { it.isLowerCase() }) puntos++
        if (valor.any { it.isDigit() } && valor.any { !it.isLetterOrDigit() }) puntos++
        return puntos.coerceIn(0, 3)
    }
}
