package com.example.corpalivapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onIrA: (Destino) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Marfil)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Imagen provisional: reemplazar por foto institucional autorizada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(VerdeSuave, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Foto institucional (provisoria)", color = TextoSecundario, fontSize = 14.sp)
        }

        Text(
            "Juntos creamos oportunidades",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )
        Text(
            "Conoce nuestro impacto social y descubre cómo puedes ser parte del cambio.",
            fontSize = 16.sp,
            color = TextoSecundario
        )
        Text(
            "INCLUSIÓN · AUTONOMÍA · OPORTUNIDADES",
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            color = VerdePrincipal
        )

        Button(
            onClick = { onIrA(Destino.Programas) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdePrincipal,
                contentColor = Color.White
            )
        ) {
            Text("Conoce nuestro impacto", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "Cada aporte puede transformar vidas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )
        Text(
            "Conoce nuestros programas y las distintas formas de colaborar.",
            fontSize = 16.sp,
            color = TextoSecundario
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaInfo(
                Icons.Default.Person, "Inclusión laboral", "Más oportunidades de participación",
                onClick = { onIrA(Destino.Programas) },
                modifier = Modifier.weight(1f)
            )
            TarjetaInfo(
                Icons.Default.Favorite, "Colabora", "Encuentra una forma de participar",
                onClick = { onIrA(Destino.Colaborar) },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TarjetaInfo(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icono, contentDescription = null, tint = VerdePrincipal)
            Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
            Text(descripcion, fontSize = 14.sp, color = TextoSecundario)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}