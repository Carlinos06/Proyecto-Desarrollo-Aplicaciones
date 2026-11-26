package com.example.corpalivapp

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.corpalivapp.ui.auth.AuthViewModel
import com.example.corpalivapp.ui.donacion.DonacionScreen
import com.example.corpalivapp.ui.perfil.PerfilScreen
import com.example.corpalivapp.ui.perfil.PerfilViewModel
import kotlinx.coroutines.launch
import java.io.File

sealed class Destino(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Inicio : Destino("inicio", "Inicio", Icons.Default.Home)
    object QuienesSomos : Destino("quienes_somos", "Quiénes somos", Icons.Default.Info)
    object Programas : Destino("programas", "Programas", Icons.Default.Star)
    object Productos : Destino("productos", "Productos y servicios", Icons.Default.ShoppingCart)
    object Historias : Destino("historias", "Historias de impacto", Icons.Default.ThumbUp)
    object Colaborar : Destino("colaborar", "Formas de colaborar", Icons.Default.Favorite)
    object Donar : Destino("donar", "Donar", Icons.Default.Payments)
    object Contacto : Destino("contacto", "Contacto", Icons.Default.Email)
    object Perfil : Destino("perfil", "Mi perfil", Icons.Default.AccountCircle)
}

private val todosLosDestinos = listOf(
    Destino.Inicio, Destino.QuienesSomos, Destino.Programas, Destino.Productos,
    Destino.Historias, Destino.Colaborar, Destino.Donar, Destino.Contacto, Destino.Perfil
)

@Composable
fun MainNavigation(
    authViewModel: AuthViewModel,
    email: String
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val perfilViewModel: PerfilViewModel = viewModel()
    val avatarPath by perfilViewModel.avatarPath.collectAsState()
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
            ModalDrawerSheet(drawerContainerColor = MoradoCorpaliv) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MoradoOscuro)
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    irA(Destino.Perfil)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            FotoPerfil(
                                imagenUri = avatarPath?.let { Uri.fromFile(File(it)) },
                                tamano = 60.dp,
                                descripcion = "Mi perfil"
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.logo_corpaliv),
                                contentDescription = "Corpaliv",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Text(
                            text = email,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 6.dp)
                        )
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
                                selectedContainerColor = Color.White.copy(alpha = 0.16f),
                                selectedTextColor = Color.White,
                                selectedIconColor = AmarilloCorpaliv,
                                unselectedContainerColor = Color.Transparent,
                                unselectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.72f)
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    )

                    RedesSocialesCorpaliv(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        compacto = true,
                        sobreFondoOscuro = true
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    )

                    NavigationDrawerItem(
                        label = { Text("Cerrar sesión", fontSize = 16.sp) },
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            authViewModel.cerrarSesion()
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.72f)
                        )
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Marfil,
            topBar = {
                BarraSuperior(
                    onMenuClick = abrirMenu,
                    onPerfilClick = { irA(Destino.Perfil) },
                    avatarPath = avatarPath
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        abrirEnlaceExterno(context, EnlacesCorpaliv.WHATSAPP)
                    },
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    containerColor = Color(0xFF25D366),
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_whatsapp),
                        contentDescription = "Escríbenos por WhatsApp",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Destino.Inicio.ruta,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(Destino.Inicio.ruta) {
                    HomeScreen(onIrA = { irA(it) })
                }
                composable(Destino.QuienesSomos.ruta) {

                    QuienesSomosScreen(onIrA = { irA(it) })
                }
                composable(Destino.Programas.ruta) {
                    ProgramasScreen(onIrA = { irA(it) })
                }
                composable(Destino.Productos.ruta) {
                    ProductosScreen(onIrA = { irA(it) })
                }
                composable(Destino.Historias.ruta) {
                    HistoriasScreen(onIrA = { irA(it) })
                }
                composable(Destino.Colaborar.ruta) {
                    ColaborarScreen(onIrA = { irA(it) })
                }
                composable(Destino.Donar.ruta) {
                    DonacionScreen()
                }
                composable(Destino.Contacto.ruta) {
                    ContactoScreen()
                }
                composable(Destino.Perfil.ruta) {
                    PerfilScreen(
                        viewModel = perfilViewModel,
                        onCerrarSesion = { authViewModel.cerrarSesion() }
                    )
                }
            }
        }
    }
}
