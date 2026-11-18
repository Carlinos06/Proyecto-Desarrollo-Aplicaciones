package com.example.corpalivapp.ui.donacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.corpalivapp.domain.Validaciones
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

enum class MedioPago(val etiqueta: String) {
    TRANSBANK("Transbank"),
    FLOW("Flow")
}

enum class MontoDonacion(val etiqueta: String) {
    DIEZ_MIL("10.000"),
    TREINTA_MIL("30.000"),
    OTRO("Otro monto")
}

data class DonacionUiState(
    val nombre: String = "",
    val rut: String = "",
    val email: String = "",
    val confirmacionEmail: String = "",
    val medioPago: MedioPago = MedioPago.TRANSBANK,
    val monto: MontoDonacion = MontoDonacion.DIEZ_MIL,
    val montoPersonalizado: String = "",
    val nombreError: String? = null,
    val rutError: String? = null,
    val emailError: String? = null,
    val confirmacionEmailError: String? = null,
    val montoError: String? = null,
    val enviando: Boolean = false,
    val mostrarConfirmacion: Boolean = false,
    val mensajeConfirmacion: String = ""
)

/**
 * Estado y validaciones del formulario de donación.
 * En esta versión el envío es local: registra la intención y muestra una confirmación.
 */
class DonacionViewModel : ViewModel() {

    private val _estado = MutableStateFlow(DonacionUiState())
    val estado: StateFlow<DonacionUiState> = _estado.asStateFlow()

    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                nombre = valor,
                nombreError = if (it.nombreError != null) Validaciones.nombre(valor) else null
            )
        }
    }

    fun onRutChange(valor: String) {
        val formateado = formatearRut(valor)
        _estado.update {
            it.copy(
                rut = formateado,
                rutError = if (it.rutError != null) Validaciones.rut(formateado) else null
            )
        }
    }

    fun onEmailChange(valor: String) {
        _estado.update {
            it.copy(
                email = valor,
                emailError = if (it.emailError != null) Validaciones.email(valor) else null,
                confirmacionEmailError = if (it.confirmacionEmailError != null) {
                    Validaciones.confirmarEmail(valor, it.confirmacionEmail)
                } else null
            )
        }
    }

    fun onConfirmacionEmailChange(valor: String) {
        _estado.update {
            it.copy(
                confirmacionEmail = valor,
                confirmacionEmailError = if (it.confirmacionEmailError != null) {
                    Validaciones.confirmarEmail(it.email, valor)
                } else null
            )
        }
    }

    fun onMedioPagoChange(valor: MedioPago) {
        _estado.update { it.copy(medioPago = valor) }
    }

    fun onMontoChange(valor: MontoDonacion) {
        _estado.update {
            it.copy(
                monto = valor,
                montoError = if (valor == MontoDonacion.OTRO && it.montoError != null) {
                    Validaciones.monto(it.montoPersonalizado)
                } else null
            )
        }
    }

    fun onMontoPersonalizadoChange(valor: String) {
        val soloDigitos = valor.filter { it.isDigit() }.take(9)
        _estado.update {
            it.copy(
                montoPersonalizado = soloDigitos,
                montoError = if (it.montoError != null) Validaciones.monto(soloDigitos) else null
            )
        }
    }

    fun enviarDonacion() {
        val actual = _estado.value
        if (actual.enviando) return

        val nombreError = Validaciones.nombre(actual.nombre)
        val rutError = Validaciones.rut(actual.rut)
        val emailError = Validaciones.email(actual.email)
        val confirmacionError = Validaciones.confirmarEmail(actual.email, actual.confirmacionEmail)
        val montoError = if (actual.monto == MontoDonacion.OTRO) {
            Validaciones.monto(actual.montoPersonalizado)
        } else null

        val hayErrores = listOf(
            nombreError,
            rutError,
            emailError,
            confirmacionError,
            montoError
        ).any { it != null }

        if (hayErrores) {
            _estado.update {
                it.copy(
                    nombreError = nombreError,
                    rutError = rutError,
                    emailError = emailError,
                    confirmacionEmailError = confirmacionError,
                    montoError = montoError
                )
            }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(enviando = true) }
            delay(700)

            val montoTexto = if (actual.monto == MontoDonacion.OTRO) {
                "$" + formatearMonto(actual.montoPersonalizado)
            } else {
                "$" + actual.monto.etiqueta
            }

            val mensaje = "Registramos tu intención de donación por $montoTexto " +
                "con ${actual.medioPago.etiqueta}. Te contactaremos en ${actual.email.trim()}."

            _estado.value = DonacionUiState(
                mostrarConfirmacion = true,
                mensajeConfirmacion = mensaje
            )
        }
    }

    fun cerrarConfirmacion() {
        _estado.value = DonacionUiState()
    }

    private fun formatearRut(valor: String): String {
        val limpio = valor
            .filter { it.isDigit() || it == 'k' || it == 'K' }
            .take(9)

        if (limpio.length <= 1) return limpio.uppercase()

        val cuerpo = limpio.dropLast(1)
        val dv = limpio.last().uppercaseChar()
        val conPuntos = cuerpo.reversed().chunked(3).joinToString(".").reversed()
        return "$conPuntos-$dv"
    }

    private fun formatearMonto(valor: String): String {
        val numero = valor.toIntOrNull() ?: return valor
        return NumberFormat.getNumberInstance(Locale.forLanguageTag("es-CL")).format(numero)
    }
}
