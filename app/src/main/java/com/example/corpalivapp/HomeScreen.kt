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
    onMenuClick: () -> Unit = {},
    onIrA: (Destino) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Marfil)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EncabezadoApp(onMenuClick)

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









/*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val Marfil = Color(0xFFFFF8F1)
private val VerdePrincipal = Color(0xFF476A4A)
private val VerdeSuave = Color(0xFFDDE9D8)
private val TextoPrincipal = Color(0xFF294A34)
private val TextoSecundario = Color(0xFF657064)

private val secciones = listOf(
    "Inicio", "Quiénes somos", "Programas", "Productos y servicios",
    "Historias de impacto", "Formas de colaborar", "Contacto"
)

@Composable
fun HomeScreen() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Marfil) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VerdeSuave)
                            .statusBarsPadding()
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = VerdePrincipal,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("CORPALIV", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = VerdePrincipal)
                        Text("Alba Lab", fontSize = 14.sp, color = TextoSecundario)
                    }

                    Spacer(Modifier.height(8.dp))

                    secciones.forEach { nombre ->
                        NavigationDrawerItem(
                            label = { Text(nombre, fontSize = 16.sp) },
                            selected = nombre == "Inicio",
                            onClick = {
                                // la navegación real se agrega después
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = VerdeSuave,
                                selectedTextColor = TextoPrincipal,
                                unselectedTextColor = TextoPrincipal,
                                unselectedContainerColor = Marfil
                            )
                        )
                    }
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Marfil)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Encabezado(onMenuClick = { scope.launch { drawerState.open() } })

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
                onClick = { /* la navegación se agrega después */ },
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
                TarjetaInfo(Icons.Default.Person, "Inclusión laboral", "Más oportunidades de participación", Modifier.weight(1f))
                TarjetaInfo(Icons.Default.Favorite, "Colabora", "Encuentra una forma de participar", Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Encabezado(onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Círculo de perfil (imagen provisional: icono de persona)
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(VerdeSuave)
                .clickable { onMenuClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Abrir menú lateral",
                tint = VerdePrincipal,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text("CORPALIV", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = VerdePrincipal)
            Text("Alba Lab", fontSize = 14.sp, color = TextoSecundario)
        }
    }
}

@Composable
private fun TarjetaInfo(icono: ImageVector, titulo: String, descripcion: String, modifier: Modifier = Modifier) {
    Card(
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
*/