package com.example.corpalivapp.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.corpalivapp.MainNavigation
import com.example.corpalivapp.Marfil
import com.example.corpalivapp.VerdePrincipal
import com.example.corpalivapp.VerdeSuave
import com.example.corpalivapp.ui.auth.AuthViewModel
import com.example.corpalivapp.ui.auth.LoginScreen
import com.example.corpalivapp.ui.auth.RegisterScreen
import com.example.corpalivapp.ui.auth.SesionEstado

/**
 * Punto de entrada de la aplicación.
 * Decide si mostrar el flujo de autenticación o la aplicación principal.
 */
@Composable
fun AppRoot() {
    val authViewModel: AuthViewModel = viewModel()
    val sesion by authViewModel.sesion.collectAsState()

    AnimatedContent(
        targetState = sesion,
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        label = "estadoSesion"
    ) { estado ->
        when (estado) {
            SesionEstado.Cargando -> PantallaCarga()
            SesionEstado.SinSesion -> AuthNavigation(authViewModel)
            is SesionEstado.Activa -> MainNavigation(
                authViewModel = authViewModel,
                email = estado.email
            )
        }
    }
}

@Composable
private fun AuthNavigation(viewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = {
            fadeIn(tween(250)) + slideInHorizontally(tween(250)) { it / 8 }
        },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(250)) },
        popExitTransition = {
            fadeOut(tween(200)) + slideOutHorizontally(tween(250)) { it / 8 }
        }
    ) {
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onIrARegistro = {
                    viewModel.limpiarRegistro()
                    navController.navigate("registro")
                }
            )
        }
        composable("registro") {
            RegisterScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun PantallaCarga() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Marfil),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(VerdeSuave),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = VerdePrincipal,
                    modifier = Modifier.size(42.dp)
                )
            }
            Text(
                text = "CORPALIV",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = VerdePrincipal
            )
            CircularProgressIndicator(color = VerdePrincipal)
        }
    }
}
