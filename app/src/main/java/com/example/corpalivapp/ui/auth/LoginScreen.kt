package com.example.corpalivapp.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.corpalivapp.Marfil
import com.example.corpalivapp.NegroLogin
import com.example.corpalivapp.TextoPrincipal
import com.example.corpalivapp.TextoSecundario
import com.example.corpalivapp.VerdePrincipal
import com.example.corpalivapp.ui.auth.components.BotonPrincipal
import com.example.corpalivapp.ui.auth.components.CampoPassword
import com.example.corpalivapp.ui.auth.components.CampoTextoValidado
import com.example.corpalivapp.ui.auth.components.EncabezadoAuth
import com.example.corpalivapp.ui.auth.components.MensajeErrorGeneral

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onIrARegistro: () -> Unit
) {
    val estado by viewModel.login.collectAsState()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
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
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 4 }
            ) {
                EncabezadoAuth(
                    titulo = "Inicia sesión",
                    subtitulo = "Accede para conocer nuestro impacto y las formas de colaborar."
                )
            }

            Spacer(Modifier.height(28.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 120)) +
                    slideInVertically(tween(600, delayMillis = 120)) { it / 3 }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        CampoTextoValidado(
                            valor = estado.email,
                            onValorChange = viewModel::onLoginEmailChange,
                            etiqueta = "Correo electrónico",
                            error = estado.emailError,
                            icono = Icons.Default.Email,
                            keyboardType = KeyboardType.Email
                        )

                        Spacer(Modifier.height(8.dp))

                        CampoPassword(
                            valor = estado.password,
                            onValorChange = viewModel::onLoginPasswordChange,
                            etiqueta = "Contraseña",
                            error = estado.passwordError
                        )

                        MensajeErrorGeneral(
                            mensaje = estado.errorGeneral,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        BotonPrincipal(
                            texto = "Iniciar sesión",
                            cargando = estado.cargando,
                            onClick = viewModel::iniciarSesion
                        )

                        Spacer(Modifier.height(4.dp))

                        TextButton(
                            onClick = onIrARegistro,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "¿No tienes cuenta? Regístrate",
                                color = VerdePrincipal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Tus datos se almacenan de forma local y la contraseña se guarda cifrada.",
                fontSize = 12.sp,
                color = TextoSecundario,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Alba Lab · Inclusión y oportunidades",
                fontSize = 12.sp,
                color = TextoPrincipal.copy(alpha = 0.7f)
            )
        }
    }
}
