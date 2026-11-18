package com.example.corpalivapp.ui.donacion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.corpalivapp.EnlacesCorpaliv
import com.example.corpalivapp.Marfil
import com.example.corpalivapp.RedesSocialesCorpaliv
import com.example.corpalivapp.TextoPrincipal
import com.example.corpalivapp.TextoSecundario
import com.example.corpalivapp.VerdePrincipal
import com.example.corpalivapp.VerdeSuave
import com.example.corpalivapp.abrirEnlaceExterno
import com.example.corpalivapp.ui.auth.components.BotonPrincipal
import com.example.corpalivapp.ui.auth.components.CampoTextoValidado

@Composable
fun DonacionScreen(
    viewModel: DonacionViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Marfil)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Donar",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )
        Text(
            text = "Tu aporte nos ayuda a seguir creando oportunidades para personas con discapacidad múltiple.",
            fontSize = 16.sp,
            color = TextoSecundario
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = VerdePrincipal)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Donación única",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Cada aporte se destina a los programas educativos y socio-laborales de CORPALIV.",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 14.sp
                )
                Text(
                    text = "También puedes sumarte como socio/a desde la plataforma oficial.",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 12.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Completa tus datos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )

                CampoTextoValidado(
                    valor = estado.nombre,
                    onValorChange = viewModel::onNombreChange,
                    etiqueta = "Nombre completo",
                    error = estado.nombreError,
                    icono = Icons.Default.Person
                )

                CampoTextoValidado(
                    valor = estado.rut,
                    onValorChange = viewModel::onRutChange,
                    etiqueta = "RUT",
                    error = estado.rutError,
                    icono = Icons.Default.CreditCard
                )

                CampoTextoValidado(
                    valor = estado.email,
                    onValorChange = viewModel::onEmailChange,
                    etiqueta = "Email",
                    error = estado.emailError,
                    icono = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                CampoTextoValidado(
                    valor = estado.confirmacionEmail,
                    onValorChange = viewModel::onConfirmacionEmailChange,
                    etiqueta = "Repite tu email",
                    error = estado.confirmacionEmailError,
                    icono = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                SelectorCampo(
                    etiqueta = "Medio de pago",
                    valor = estado.medioPago.etiqueta,
                    opciones = MedioPago.entries.map { it.etiqueta },
                    onSeleccion = { etiqueta ->
                        viewModel.onMedioPagoChange(
                            MedioPago.entries.first { it.etiqueta == etiqueta }
                        )
                    },
                    icono = Icons.Default.CreditCard
                )

                SelectorCampo(
                    etiqueta = "Monto",
                    valor = estado.monto.etiqueta,
                    opciones = MontoDonacion.entries.map { it.etiqueta },
                    onSeleccion = { etiqueta ->
                        viewModel.onMontoChange(
                            MontoDonacion.entries.first { it.etiqueta == etiqueta }
                        )
                    },
                    icono = Icons.Default.Payments
                )

                AnimatedVisibility(
                    visible = estado.monto == MontoDonacion.OTRO,
                    enter = fadeIn() + expandVertically()
                ) {
                    CampoTextoValidado(
                        valor = estado.montoPersonalizado,
                        onValorChange = viewModel::onMontoPersonalizadoChange,
                        etiqueta = "Monto personalizado (CLP)",
                        error = estado.montoError,
                        icono = Icons.Default.AttachMoney,
                        keyboardType = KeyboardType.Number
                    )
                }

                Spacer(Modifier.height(4.dp))

                BotonPrincipal(
                    texto = "Donar",
                    cargando = estado.enviando,
                    onClick = viewModel::enviarDonacion
                )

                Text(
                    text = "Formulario de demostración: la app registra tu intención de donación, " +
                        "pero no procesa pagos reales en esta versión.",
                    fontSize = 12.sp,
                    color = TextoSecundario
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(VerdeSuave),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = VerdePrincipal
                        )
                    }
                    Text(
                        text = "Transferencia bancaria directa",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                }

                DatoDonacion(
                    etiqueta = "Nombre",
                    valor = "Corporación de Padres y Amigos por el Limitado Visual"
                )
                DatoDonacion(etiqueta = "RUT", valor = "73.100.200-2")
                DatoDonacion(etiqueta = "Banco", valor = "Banco de Chile")
                DatoDonacion(etiqueta = "Cuenta corriente", valor = "17323468-02")
                DatoDonacion(
                    etiqueta = "Email para comprobantes",
                    valor = "administracionyfinanzas@corpaliv.cl"
                )

                OutlinedButton(
                    onClick = {
                        abrirEnlaceExterno(context, EnlacesCorpaliv.CORREO_ADMINISTRACION)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePrincipal),
                    border = BorderStroke(1.dp, VerdePrincipal)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Enviar comprobante")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Donar en la plataforma oficial",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
                Text(
                    text = "También puedes hacer tu aporte en línea en corpaliv.donando.cl.",
                    fontSize = 14.sp,
                    color = TextoSecundario
                )
                OutlinedButton(
                    onClick = { abrirEnlaceExterno(context, EnlacesCorpaliv.DONACIONES) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = VerdePrincipal),
                    border = BorderStroke(1.dp, VerdePrincipal)
                ) {
                    Text("Ir a corpaliv.donando.cl")
                }
            }
        }

        RedesSocialesCorpaliv()

        Spacer(Modifier.height(80.dp))
    }

    if (estado.mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = viewModel::cerrarConfirmacion,
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = VerdePrincipal
                )
            },
            title = { Text("¡Gracias por tu aporte!") },
            text = { Text(estado.mensajeConfirmacion) },
            confirmButton = {
                TextButton(onClick = viewModel::cerrarConfirmacion) {
                    Text("Cerrar", color = VerdePrincipal, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}

@Composable
private fun SelectorCampo(
    etiqueta: String,
    valor: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit,
    icono: ImageVector
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expandido = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, TextoSecundario.copy(alpha = 0.45f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoPrincipal)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = VerdePrincipal,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(etiqueta, fontSize = 11.sp, color = TextoSecundario)
                Text(valor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = TextoSecundario
            )
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DatoDonacion(etiqueta: String, valor: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = etiqueta, fontSize = 12.sp, color = TextoSecundario)
        Text(
            text = valor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextoPrincipal
        )
    }
}
