package com.example.corpalivapp.data.model

/**
 * Datos del usuario que se muestran en la aplicación.
 * La contraseña nunca se guarda en texto plano: se almacena su hash y su salt.
 */
data class Usuario(
    val nombre: String,
    val email: String,
    val telefono: String,
    val fechaNacimiento: String, // Formato dd/MM/yyyy
    val notificaciones: Boolean
)

/**
 * Representación interna de una cuenta guardada localmente.
 */
data class Cuenta(
    val usuario: Usuario,
    val passwordHash: String,
    val salt: String
)
