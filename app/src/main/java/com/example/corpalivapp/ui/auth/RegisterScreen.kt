package com.example.corpalivapp.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.corpalivapp.Marfil
import com.example.corpalivapp.TextoPrincipal
import com.example.corpalivapp.TextoSecundario
import com.example.corpalivapp.VerdePrincipal
import com.example.corpalivapp.VerdeSuave
import com.example.corpalivapp.domain.Validaciones
import com.example.corpalivapp.ui.auth.components.BotonPrincipal
import com.example.corpalivapp.ui.auth.components.CampoFecha
import com.example.corpalivapp.ui.auth.components.CampoPassword
import com.example.corpalivapp.ui.auth.components.CampoTextoValidado
import com.example.corpalivapp.ui.auth.components.EncabezadoAuth
import com.example.corpalivapp.ui.auth.components.MensajeErrorGeneral
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onVolver: () -> Unit
) {
    val estado by viewModel.registro.collectAsState()
    var visible by remember { mutableStateOf(false) }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fechaAMillis(estado.fechaNacimiento),
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= System.currentTimeMillis()
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year <= Calendar.getInstance().get(Calendar.YEAR)
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            viewModel.onRegistroFechaChange(formatearFecha(millis))
                        }
                        mostrarDatePicker = false
                    }
                ) {
                    Text("Aceptar", color = VerdePrincipal)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) {
                    Text("Cancelar", color = TextoSecundario)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Marfil)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = VerdePrincipal
                    )
                }
                Text(
                    text = "Crear cuenta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
            }

            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(450)) + slideInVertically(tween(450)) { it / 4 }
            ) {
                EncabezadoAuth(
                    titulo = "Únete a CORPALIV",
                    subtitulo = "Completa tus datos para descubrir cómo colaborar con Alba Lab."
                )
            }

            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoTextoValidado(
                    valor = estado.nombre,
                    onValorChange = viewModel::onRegistroNombreChange,
                    etiqueta = "Nombre completo",
                    error = estado.nombreError,
                    icono = Icons.Default.Person,
                    keyboardType = KeyboardType.Text
                )

                CampoTextoValidado(
                    valor = estado.email,
                    onValorChange = viewModel::onRegistroEmailChange,
                    etiqueta = "Correo electrónico",
                    error = estado.emailError,
                    icono = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                CampoTextoValidado(
                    valor = estado.telefono,
                    onValorChange = viewModel::onRegistroTelefonoChange,
                    etiqueta = "Teléfono",
                    error = estado.telefonoError,
                    icono = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone
                )

                CampoFecha(
                    valor = estado.fechaNacimiento,
                    error = estado.fechaError,
                    onClick = { mostrarDatePicker = true }
                )

                CampoPassword(
                    valor = estado.password,
                    onValorChange = viewModel::onRegistroPasswordChange,
                    etiqueta = "Contraseña",
                    error = estado.passwordError
                )

                IndicadorFortaleza(valor = estado.password)

                CampoPassword(
                    valor = estado.confirmarPassword,
                    onValorChange = viewModel::onRegistroConfirmarChange,
                    etiqueta = "Repetir contraseña",
                    error = estado.confirmarPasswordError
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = VerdeSuave.copy(alpha = 0.45f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = VerdePrincipal
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notificaciones",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextoPrincipal
                            )
                            Text(
                                text = "Recibir novedades de programas y formas de colaborar.",
                                fontSize = 12.sp,
                                color = TextoSecundario
                            )
                        }
                        Switch(
                            checked = estado.notificaciones,
                            onCheckedChange = viewModel::onNotificacionesChange
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onTerminosChange(!estado.aceptaTerminos) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = estado.aceptaTerminos,
                        onCheckedChange = viewModel::onTerminosChange
                    )
                    Text(
                        text = "Acepto los términos y la política de privacidad.",
                        fontSize = 13.sp,
                        color = TextoPrincipal
                    )
                }

                AnimatedVisibility(
                    visible = estado.terminosError != null,
                    enter = fadeIn() + expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = Color(0xFFB3261E),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = estado.terminosError.orEmpty(),
                            color = Color(0xFFB3261E),
                            fontSize = 12.sp
                        )
                    }
                }

                MensajeErrorGeneral(mensaje = estado.errorGeneral)

                Spacer(Modifier.height(4.dp))

                BotonPrincipal(
                    texto = "Crear cuenta",
                    cargando = estado.cargando,
                    onClick = viewModel::registrar
                )

                TextButton(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        color = VerdePrincipal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun IndicadorFortaleza(valor: String) {
    if (valor.isEmpty()) return

    val fuerza = Validaciones.fortalezaPassword(valor)
    val color by animateColorAsState(
        targetValue = when (fuerza) {
            1 -> Color(0xFFD32F2F)
            2 -> Color(0xFFF9A825)
            else -> VerdePrincipal
        },
        label = "colorFortaleza"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { indice ->
                val colorSegmento by animateColorAsState(
                    targetValue = if (indice < fuerza) color else Color(0xFFE0E0E0),
                    label = "segmentoFortaleza$indice"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(colorSegmento)
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (fuerza == 3) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = VerdePrincipal,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                text = when (fuerza) {
                    1 -> "Contraseña débil"
                    2 -> "Contraseña media"
                    else -> "Contraseña segura"
                },
                fontSize = 12.sp,
                color = color
            )
        }
    }
}

private fun formatearFecha(millis: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(millis))
}

private fun fechaAMillis(fecha: String): Long? {
    if (fecha.isBlank()) return null
    return runCatching {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .apply {
                isLenient = false
                timeZone = TimeZone.getTimeZone("UTC")
            }
            .parse(fecha)
            ?.time
    }.getOrNull()
}
