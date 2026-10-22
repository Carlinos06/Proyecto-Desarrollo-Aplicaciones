package com.example.corpalivapp.ui.perfil

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.corpalivapp.CamaraUriFactory
import com.example.corpalivapp.FotoPerfil
import com.example.corpalivapp.Marfil
import com.example.corpalivapp.TextoPrincipal
import com.example.corpalivapp.TextoSecundario
import com.example.corpalivapp.VerdePrincipal
import com.example.corpalivapp.VerdeSuave
import java.io.File

private enum class TipoImagen { PORTADA, AVATAR }

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val usuario by viewModel.usuario.collectAsState()
    val portada by viewModel.portadaPath.collectAsState()
    val avatar by viewModel.avatarPath.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var menuPortada by remember { mutableStateOf(false) }
    var menuAvatar by remember { mutableStateOf(false) }
    var objetivo by rememberSaveable { mutableStateOf(TipoImagen.PORTADA) }
    var uriPendienteCamara by rememberSaveable { mutableStateOf<Uri?>(null) }

    LaunchedEffect(mensaje) {
        mensaje?.let { texto ->
            snackbarHostState.showSnackbar(texto)
            viewModel.consumirMensaje()
        }
    }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        when (objetivo) {
            TipoImagen.PORTADA -> viewModel.actualizarPortadaDesdeGaleria(uri)
            TipoImagen.AVATAR -> viewModel.actualizarAvatarDesdeGaleria(uri)
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        when (objetivo) {
            TipoImagen.PORTADA -> viewModel.actualizarPortadaDesdeCamara(uriPendienteCamara, exito)
            TipoImagen.AVATAR -> viewModel.actualizarAvatarDesdeCamara(uriPendienteCamara, exito)
        }
        uriPendienteCamara = null
    }

    fun abrirCamara() {
        try {
            val uri = CamaraUriFactory.crearUri(context)
            uriPendienteCamara = uri
            camaraLauncher.launch(uri)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error al abrir la cámara: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamara()
        } else {
            Toast.makeText(
                context,
                "Debes conceder el permiso de cámara en Ajustes.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun tomarFoto(tipo: TipoImagen) {
        objetivo = tipo
        val permisoConcedido = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (permisoConcedido) {
            abrirCamara()
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun elegirArchivo(tipo: TipoImagen) {
        objetivo = tipo
        galeriaLauncher.launch("image/*")
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
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Portada(
                    portada = portada,
                    menuAbierto = menuPortada,
                    onMenuAbiertoChange = { menuPortada = it },
                    onCamara = { tomarFoto(TipoImagen.PORTADA) },
                    onArchivo = { elegirArchivo(TipoImagen.PORTADA) }
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 48.dp)
                ) {
                    Avatar(
                        avatar = avatar,
                        menuAbierto = menuAvatar,
                        onMenuAbiertoChange = { menuAvatar = it },
                        onCamara = { tomarFoto(TipoImagen.AVATAR) },
                        onArchivo = { elegirArchivo(TipoImagen.AVATAR) }
                    )
                }
            }

            Spacer(Modifier.height(62.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = usuario?.nombre ?: "Mi perfil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
                Text(
                    text = usuario?.email.orEmpty(),
                    fontSize = 14.sp,
                    color = TextoSecundario
                )

                Spacer(Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        FilaInfo(
                            icono = Icons.Default.Phone,
                            etiqueta = "Teléfono",
                            valor = usuario?.telefono ?: "—"
                        )
                        FilaInfo(
                            icono = Icons.Default.DateRange,
                            etiqueta = "Fecha de nacimiento",
                            valor = usuario?.fechaNacimiento ?: "—"
                        )
                        FilaInfo(
                            icono = Icons.Default.NotificationsActive,
                            etiqueta = "Notificaciones",
                            valor = if (usuario?.notificaciones == true) {
                                "Activadas"
                            } else {
                                "Desactivadas"
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onCerrarSesion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar sesión", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(28.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun Portada(
    portada: String?,
    menuAbierto: Boolean,
    onMenuAbiertoChange: (Boolean) -> Unit,
    onCamara: () -> Unit,
    onArchivo: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        AnimatedContent(
            targetState = portada,
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(250)) },
            label = "imagenPortada"
        ) { ruta ->
            if (ruta != null) {
                AsyncImage(
                    model = File(ruta),
                    contentDescription = "Imagen de portada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(VerdePrincipal, VerdeSuave)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Imagen de portada",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f))
                    )
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
        ) {
            Button(
                onClick = { onMenuAbiertoChange(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.45f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Cambiar portada", fontSize = 13.sp)
            }

            DropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { onMenuAbiertoChange(false) }
            ) {
                DropdownMenuItem(
                    text = { Text("Tomar foto") },
                    leadingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                    onClick = {
                        onMenuAbiertoChange(false)
                        onCamara()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Elegir de archivos") },
                    leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                    onClick = {
                        onMenuAbiertoChange(false)
                        onArchivo()
                    }
                )
            }
        }
    }
}

@Composable
private fun Avatar(
    avatar: String?,
    menuAbierto: Boolean,
    onMenuAbiertoChange: (Boolean) -> Unit,
    onCamara: () -> Unit,
    onArchivo: () -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(Marfil)
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            FotoPerfil(
                imagenUri = avatar?.let { Uri.fromFile(File(it)) },
                tamano = 102.dp,
                descripcion = "Foto de perfil"
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(36.dp)
                .clip(CircleShape)
                .background(VerdePrincipal)
                .clickable { onMenuAbiertoChange(true) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Cambiar foto de perfil",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        DropdownMenu(
            expanded = menuAbierto,
            onDismissRequest = { onMenuAbiertoChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text("Tomar foto") },
                leadingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                onClick = {
                    onMenuAbiertoChange(false)
                    onCamara()
                }
            )
            DropdownMenuItem(
                text = { Text("Elegir de archivos") },
                leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null) },
                onClick = {
                    onMenuAbiertoChange(false)
                    onArchivo()
                }
            )
        }
    }
}

@Composable
private fun FilaInfo(
    icono: ImageVector,
    etiqueta: String,
    valor: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(VerdeSuave),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = VerdePrincipal,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(etiqueta, fontSize = 12.sp, color = TextoSecundario)
            Text(
                text = valor,
                fontSize = 15.sp,
                color = TextoPrincipal,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
