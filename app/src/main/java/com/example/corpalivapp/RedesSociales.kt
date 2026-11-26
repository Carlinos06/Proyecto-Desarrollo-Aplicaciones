package com.example.corpalivapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Enlaces oficiales de CORPALIV usados por los accesos rápidos de la app.
 */
object EnlacesCorpaliv {
    const val SITIO_WEB = "https://corpaliv.cl/"
    const val FACEBOOK = "https://www.facebook.com/corpaliv/"
    const val INSTAGRAM = "https://www.instagram.com/corpaliv/"
    const val LINKEDIN = "https://www.linkedin.com/company/corpaliv-chile/"
    const val WHATSAPP = "https://wa.me/56982205900"
    const val DONACIONES = "https://corpaliv.donando.cl/"
    const val CORREO_CONTACTO = "mailto:comunicaciones@corpaliv.cl"
    const val CORREO_ADMINISTRACION = "mailto:administracionyfinanzas@corpaliv.cl"
}

/**
 * Abre un enlace externo con el navegador o la app correspondiente.
 */
fun abrirEnlaceExterno(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "No se pudo abrir el enlace.", Toast.LENGTH_SHORT).show()
    } catch (_: SecurityException) {
        Toast.makeText(context, "No se pudo abrir el enlace.", Toast.LENGTH_SHORT).show()
    }
}

private data class RedSocial(
    val nombre: String,
    val url: String,
    val color: Color,
    val iconoRes: Int? = null,
    val inicial: String? = null
)

private val redesSociales = listOf(
    RedSocial(
        nombre = "Facebook",
        url = EnlacesCorpaliv.FACEBOOK,
        color = Color(0xFF1877F2),
        inicial = "f"
    ),
    RedSocial(
        nombre = "Instagram",
        url = EnlacesCorpaliv.INSTAGRAM,
        color = Color(0xFFE4405F),
        iconoRes = R.drawable.ic_instagram
    ),
    RedSocial(
        nombre = "LinkedIn",
        url = EnlacesCorpaliv.LINKEDIN,
        color = Color(0xFF0A66C2),
        inicial = "in"
    )
)

/**
 * Fila de accesos directos a las redes oficiales de CORPALIV.
 */
@Composable
fun RedesSocialesCorpaliv(
    modifier: Modifier = Modifier,
    compacto: Boolean = false,
    sobreFondoOscuro: Boolean = false
) {
    val context = LocalContext.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Síguenos en redes",
            fontSize = if (compacto) 14.sp else 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (sobreFondoOscuro) Color.White else MoradoCorpaliv
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            redesSociales.forEach { red ->
                Card(
                    onClick = { abrirEnlaceExterno(context, red.url) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (sobreFondoOscuro) {
                            Color.Transparent
                        } else {
                            Color.White
                        }
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (sobreFondoOscuro) 0.dp else 2.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (compacto) 34.dp else 42.dp)
                                .clip(CircleShape)
                                .background(red.color),
                            contentAlignment = Alignment.Center
                        ) {
                            val iconoRes = red.iconoRes
                            if (iconoRes != null) {
                                Icon(
                                    painter = painterResource(iconoRes),
                                    contentDescription = red.nombre,
                                    tint = Color.White,
                                    modifier = Modifier.size(if (compacto) 18.dp else 22.dp)
                                )
                            } else {
                                Text(
                                    text = red.inicial.orEmpty(),
                                    color = Color.White,
                                    fontSize = if (compacto) 15.sp else 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Text(
                            text = red.nombre,
                            fontSize = if (compacto) 11.sp else 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (sobreFondoOscuro) Color.White else TextoPrincipal
                        )
                    }
                }
            }
        }
    }
}
