package com.example.corpalivapp

import com.example.corpalivapp.domain.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    @Test
    fun `nombre valido e invalido`() {
        assertNull(Validaciones.nombre("María José"))
        assertNull(Validaciones.nombre("Ana-Paz"))
        assertNotNull(Validaciones.nombre(""))
        assertNotNull(Validaciones.nombre("Al"))
        assertNotNull(Validaciones.nombre("Ana123"))
    }

    @Test
    fun `email valido e invalido`() {
        assertNull(Validaciones.email("correo@dominio.cl"))
        assertNull(Validaciones.email("nombre.apellido@alba-lab.org"))
        assertNotNull(Validaciones.email(""))
        assertNotNull(Validaciones.email("correo"))
        assertNotNull(Validaciones.email("correo@dominio"))
        assertNotNull(Validaciones.email("correo@.cl"))
    }

    @Test
    fun `telefono valido e invalido`() {
        assertNull(Validaciones.telefono("912345678"))
        assertNull(Validaciones.telefono("22345678"))
        assertNotNull(Validaciones.telefono("1234567"))
        assertNotNull(Validaciones.telefono("9123456789"))
        assertNotNull(Validaciones.telefono("9123 4567"))
    }

    @Test
    fun `fecha de nacimiento valida e invalida`() {
        assertNull(Validaciones.fechaNacimiento("01/01/2000"))
        assertNotNull(Validaciones.fechaNacimiento(""))
        assertNotNull(Validaciones.fechaNacimiento("01-01-2000"))
        assertNotNull(Validaciones.fechaNacimiento("31/02/2000"))
        assertNotNull(Validaciones.fechaNacimiento("01/01/2999"))
        assertNotNull(Validaciones.fechaNacimiento("01/01/2020"))
    }

    @Test
    fun `password cumple reglas`() {
        assertNull(Validaciones.password("Abcdef12"))
        assertNotNull(Validaciones.password("Ab1"))
        assertNotNull(Validaciones.password("abcdef12"))
        assertNotNull(Validaciones.password("ABCDEF12"))
        assertNotNull(Validaciones.password("Abcdefgh"))
    }

    @Test
    fun `confirmacion de password`() {
        assertNull(Validaciones.confirmarPassword("Abcdef12", "Abcdef12"))
        assertNotNull(Validaciones.confirmarPassword("Abcdef12", "Abcdef13"))
        assertNotNull(Validaciones.confirmarPassword("Abcdef12", ""))
    }

    @Test
    fun `fortaleza de password`() {
        assertEquals(0, Validaciones.fortalezaPassword(""))
        assertEquals(1, Validaciones.fortalezaPassword("abcdefgh"))
        assertEquals(2, Validaciones.fortalezaPassword("Abcdefgh"))
        assertEquals(3, Validaciones.fortalezaPassword("Abcdef12!"))
    }
}
