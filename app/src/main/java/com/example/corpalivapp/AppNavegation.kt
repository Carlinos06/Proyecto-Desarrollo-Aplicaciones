package com.example.corpalivapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

sealed class Destino(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Inicio : Destino("inicio", "Inicio", Icons.Default.Home)
    object QuienesSomos : Destino("quienes_somos", "Quiénes somos", Icons.Default.Info)
    object Programas : Destino("programas", "Programas", Icons.Default.Star)
    object Productos : Destino("productos", "Productos y servicios", Icons.Default.ShoppingCart)
    object Historias : Destino("historias", "Historias de impacto", Icons.Default.ThumbUp)
    object Colaborar : Destino("colaborar", "Formas de colaborar", Icons.Default.Favorite)
    object Contacto : Destino("contacto", "Contacto", Icons.Default.Email)
}

private val todosLosDestinos = listOf(
    Destino.Inicio, Destino.QuienesSomos, Destino.Programas, Destino.Productos,
    Destino.Historias, Destino.Colaborar, Destino.Contacto
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    fun irA(destino: Destino) {
        navController.navigate(destino.ruta) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val abrirMenu: () -> Unit = { scope.launch { drawerState.open() } }

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

                    todosLosDestinos.forEach { destino ->
                        NavigationDrawerItem(
                            label = { Text(destino.titulo, fontSize = 16.sp) },
                            icon = { Icon(destino.icono, contentDescription = null) },
                            selected = rutaActual == destino.ruta,
                            onClick = {
                                scope.launch { drawerState.close() }
                                irA(destino)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = VerdeSuave,
                                selectedTextColor = TextoPrincipal,
                                selectedIconColor = VerdePrincipal,
                                unselectedContainerColor = Marfil,
                                unselectedTextColor = TextoPrincipal,
                                unselectedIconColor = TextoSecundario
                            )
                        )
                    }
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Destino.Inicio.ruta,
            modifier = Modifier
                .fillMaxSize()
                .background(Marfil)
        ) {
            composable(Destino.Inicio.ruta) {
                HomeScreen(onMenuClick = abrirMenu, onIrA = { irA(it) })
            }
            composable(Destino.QuienesSomos.ruta) {
                PantallaSimple("Quiénes somos", "Aquí explicaremos qué es CORPALIV y el papel de Alba Lab.", abrirMenu)
            }
            composable(Destino.Programas.ruta) {
                PantallaSimple("Programas", "Aquí mostraremos los programas de inclusión y su impacto social.", abrirMenu)
            }
            composable(Destino.Productos.ruta) {
                PantallaSimple("Productos y servicios", "Aquí exploraremos la oferta de Alba Lab.", abrirMenu)
            }
            composable(Destino.Historias.ruta) {
                PantallaSimple("Historias de impacto", "Aquí irán ejemplos ficticios, claramente marcados.", abrirMenu)
            }
            composable(Destino.Colaborar.ruta) {
                PantallaSimple("Formas de colaborar", "Aquí presentaremos donaciones, voluntariado, alianzas y más.", abrirMenu)
            }
            composable(Destino.Contacto.ruta) {
                PantallaSimple("Contacto", "Aquí irá el formulario de solicitud de colaboración.", abrirMenu)
            }
        }
    }
}





/*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class Destino(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Pantalla1 : Destino("pantalla1", "Inicio", Icons.Default.Home)
    object Pantalla2 : Destino("pantalla2", "Apoyo", Icons.Default.Favorite)
    object Pantalla3 : Destino("pantalla3", "Usuario", Icons.Default.Person)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val destinos = listOf(Destino.Pantalla1, Destino.Pantalla2, Destino.Pantalla3)
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    Scaffold(
        containerColor = Color(0xFFFFF8F1),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                destinos.forEach { destino ->
                    NavigationBarItem(
                        selected = rutaActual == destino.ruta,
                        onClick = {
                            navController.navigate(destino.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destino.icono, contentDescription = destino.titulo) },
                        label = { Text(destino.titulo) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF294A34),
                            selectedTextColor = Color(0xFF294A34),
                            indicatorColor = Color(0xFFDDE9D8),
                            unselectedIconColor = Color(0xFF657064),
                            unselectedTextColor = Color(0xFF657064)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destino.Pantalla1.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destino.Pantalla1.ruta) { HomeScreen() }
            composable(Destino.Pantalla2.ruta) { PantallaSimple("Pantalla 2") }
            composable(Destino.Pantalla3.ruta) { PantallaSimple("Pantalla 3") }
        }
    }
}

@Composable
private fun PantallaSimple(titulo: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(titulo, fontSize = 28.sp, color = Color(0xFF294A34))
    }
}
*/
