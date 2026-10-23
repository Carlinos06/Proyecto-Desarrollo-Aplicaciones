package com.example.corpalivapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.corpalivapp.data.local.UserPreferences
import com.example.corpalivapp.data.model.Usuario
import com.example.corpalivapp.data.repository.AuthRepository
import com.example.corpalivapp.domain.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false
)

data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val fechaNacimiento: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val notificaciones: Boolean = true,
    val aceptaTerminos: Boolean = false,
    val nombreError: String? = null,
    val emailError: String? = null,
    val telefonoError: String? = null,
    val fechaError: String? = null,
    val passwordError: String? = null,
    val confirmarPasswordError: String? = null,
    val terminosError: String? = null,
    val errorGeneral: String? = null,
    val cargando: Boolean = false
)

sealed interface SesionEstado {
    data object Cargando : SesionEstado
    data object SinSesion : SesionEstado
    data class Activa(val email: String) : SesionEstado
}

/**
 * ViewModel de autenticación. Mantiene el estado de los formularios y ejecuta
 * las validaciones antes de llamar al repositorio.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = UserPreferences(application)
    private val repository = AuthRepository(preferences)

    private val _sesion = MutableStateFlow<SesionEstado>(SesionEstado.Cargando)
    val sesion: StateFlow<SesionEstado> = _sesion.asStateFlow()

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login.asStateFlow()

    private val _registro = MutableStateFlow(RegistroUiState())
    val registro: StateFlow<RegistroUiState> = _registro.asStateFlow()

    init {
        viewModelScope.launch {
            repository.sesionEmail().collect { email ->
                _sesion.value = if (email.isNullOrBlank()) {
                    SesionEstado.SinSesion
                } else {
                    SesionEstado.Activa(email)
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    // Login
    // ---------------------------------------------------------------------

    fun onLoginEmailChange(valor: String) {
        _login.update {
            it.copy(
                email = valor,
                emailError = if (it.emailError != null) Validaciones.email(valor) else null,
                errorGeneral = null
            )
        }
    }

    fun onLoginPasswordChange(valor: String) {
        _login.update {
            it.copy(
                password = valor,
                passwordError = if (it.passwordError != null) {
                    if (valor.isEmpty()) "La contraseña es obligatoria." else null
                } else null,
                errorGeneral = null
            )
        }
    }

    fun iniciarSesion() {
        val estado = _login.value
        val emailError = Validaciones.email(estado.email)
        val passwordError = if (estado.password.isEmpty()) "La contraseña es obligatoria." else null

        if (emailError != null || passwordError != null) {
            _login.update {
                it.copy(emailError = emailError, passwordError = passwordError)
            }
            return
        }

        viewModelScope.launch {
            _login.update { it.copy(cargando = true, errorGeneral = null) }
            repository.iniciarSesion(estado.email, estado.password)
                .onSuccess {
                    _login.value = LoginUiState()
                }
                .onFailure { error ->
                    _login.update {
                        it.copy(
                            cargando = false,
                            errorGeneral = error.message ?: "No se pudo iniciar sesión."
                        )
                    }
                }
        }
    }

    fun limpiarLogin() {
        _login.value = LoginUiState()
    }

    // ---------------------------------------------------------------------
    // Registro
    // ---------------------------------------------------------------------

    fun onRegistroNombreChange(valor: String) {
        _registro.update {
            it.copy(
                nombre = valor,
                nombreError = if (it.nombreError != null) Validaciones.nombre(valor) else null,
                errorGeneral = null
            )
        }
    }

    fun onRegistroEmailChange(valor: String) {
        _registro.update {
            it.copy(
                email = valor,
                emailError = if (it.emailError != null) Validaciones.email(valor) else null,
                errorGeneral = null
            )
        }
    }

    fun onRegistroTelefonoChange(valor: String) {
        val soloDigitos = valor.filter { it.isDigit() }.take(9)
        _registro.update {
            it.copy(
                telefono = soloDigitos,
                telefonoError = if (it.telefonoError != null) {
                    Validaciones.telefono(soloDigitos)
                } else null,
                errorGeneral = null
            )
        }
    }

    fun onRegistroFechaChange(valor: String) {
        _registro.update {
            it.copy(
                fechaNacimiento = valor,
                fechaError = if (it.fechaError != null) {
                    Validaciones.fechaNacimiento(valor)
                } else null,
                errorGeneral = null
            )
        }
    }

    fun onRegistroPasswordChange(valor: String) {
        _registro.update {
            it.copy(
                password = valor,
                passwordError = if (it.passwordError != null) {
                    Validaciones.password(valor)
                } else null,
                confirmarPasswordError = if (it.confirmarPasswordError != null) {
                    Validaciones.confirmarPassword(valor, it.confirmarPassword)
                } else null,
                errorGeneral = null
            )
        }
    }

    fun onRegistroConfirmarChange(valor: String) {
        _registro.update {
            it.copy(
                confirmarPassword = valor,
                confirmarPasswordError = if (it.confirmarPasswordError != null) {
                    Validaciones.confirmarPassword(it.password, valor)
                } else null,
                errorGeneral = null
            )
        }
    }

    fun onNotificacionesChange(activado: Boolean) {
        _registro.update { it.copy(notificaciones = activado) }
    }

    fun onTerminosChange(acepta: Boolean) {
        _registro.update {
            it.copy(
                aceptaTerminos = acepta,
                terminosError = if (acepta) null else it.terminosError
            )
        }
    }

    fun registrar() {
        val estado = _registro.value
        val nombreError = Validaciones.nombre(estado.nombre)
        val emailError = Validaciones.email(estado.email)
        val telefonoError = Validaciones.telefono(estado.telefono)
        val fechaError = Validaciones.fechaNacimiento(estado.fechaNacimiento)
        val passwordError = Validaciones.password(estado.password)
        val confirmarError = Validaciones.confirmarPassword(
            estado.password,
            estado.confirmarPassword
        )
        val terminosError = Validaciones.terminos(estado.aceptaTerminos)

        val hayErrores = listOf(
            nombreError,
            emailError,
            telefonoError,
            fechaError,
            passwordError,
            confirmarError,
            terminosError
        ).any { it != null }

        if (hayErrores) {
            _registro.update {
                it.copy(
                    nombreError = nombreError,
                    emailError = emailError,
                    telefonoError = telefonoError,
                    fechaError = fechaError,
                    passwordError = passwordError,
                    confirmarPasswordError = confirmarError,
                    terminosError = terminosError
                )
            }
            return
        }

        viewModelScope.launch {
            _registro.update { it.copy(cargando = true, errorGeneral = null) }
            repository.registrar(
                nombre = estado.nombre,
                email = estado.email,
                telefono = estado.telefono,
                fechaNacimiento = estado.fechaNacimiento,
                notificaciones = estado.notificaciones,
                password = estado.password
            )
                .onSuccess {
                    _registro.value = RegistroUiState()
                }
                .onFailure { error ->
                    _registro.update {
                        it.copy(
                            cargando = false,
                            errorGeneral = error.message ?: "No se pudo crear la cuenta."
                        )
                    }
                }
        }
    }

    fun limpiarRegistro() {
        _registro.value = RegistroUiState()
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            repository.cerrarSesion()
            _login.value = LoginUiState()
            _registro.value = RegistroUiState()
        }
    }
}
