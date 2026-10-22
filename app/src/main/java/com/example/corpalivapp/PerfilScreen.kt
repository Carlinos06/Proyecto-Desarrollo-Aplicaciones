package com.example.corpalivapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun PerfilScreen(
    volver: () -> Unit,
    perfilViewModel: PerfilViewModel
) {
    val context = LocalContext.current
    val imagenUri by perfilViewModel.imagenUri.collectAsState()

    // Indica si el menú del lápiz (galería / cámara) está abierto
    var menuEditarAbierto by remember { mutableStateOf(false) }

    // Uri donde la cámara guardará la foto (sobrevive a rotaciones de pantalla)
    var uriPendienteCamara by rememberSaveable { mutableStateOf<Uri?>(null) }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uriSeleccionado ->
        perfilViewModel.actualizarImagenDesdeGaleria(uriSeleccionado)
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        perfilViewModel.actualizarImagenDesdeCamara(uriPendienteCamara, exito)
        if (!exito) {
            uriPendienteCamara = null
        }
    }

    // Abre la cámara creando primero el archivo temporal donde se guardará la foto
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

    // Solicita el permiso de cámara si todavía no está concedido
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

    // Comprueba el permiso antes de abrir la cámara
    fun tomarFotografia() {
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

    val colorBoton = ButtonDefaults.buttonColors(
        containerColor = VerdePrincipal,
        contentColor = Color.White
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Marfil)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 72.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Mi perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )

            Spacer(Modifier.height(24.dp))

            // Foto grande. El lápiz aparece solo cuando ya hay una foto.
            Box {
                FotoPerfil(
                    imagenUri = imagenUri,
                    tamano = 200.dp,
                    descripcion = if (imagenUri == null) {
                        "Sin foto de perfil"
                    } else {
                        "Foto de perfil"
                    }
                )

                if (imagenUri != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-6).dp, y = (-6).dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(VerdePrincipal)
                                .clickable(role = Role.Button, onClickLabel = "Cambiar foto") {
                                    menuEditarAbierto = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Cambiar foto",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuEditarAbierto,
                            onDismissRequest = { menuEditarAbierto = false },
                            containerColor = Color.White
                        ) {
                            DropdownMenuItem(
                                text = { Text("Foto de la Galería", color = TextoPrincipal) },
                                leadingIcon = {
                                    Icon(Icons.Default.Collections, contentDescription = null, tint = VerdePrincipal)
                                },
                                onClick = {
                                    menuEditarAbierto = false
                                    galeriaLauncher.launch("image/*")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Foto con la cámara", color = TextoPrincipal) },
                                leadingIcon = {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = VerdePrincipal)
                                },
                                onClick = {
                                    menuEditarAbierto = false
                                    tomarFotografia()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            if (imagenUri == null) {
                Text(
                    "Aún no has elegido una foto",
                    fontSize = 16.sp,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { galeriaLauncher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = colorBoton
                ) {
                    Icon(Icons.Default.Collections, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Foto de la Galería", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { tomarFotografia() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = colorBoton
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Foto con la cámara", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Text(
                    "Foto de perfil actualizada. Toca el lápiz para cambiarla.",
                    fontSize = 16.sp,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Botón circular para volver, arriba a la izquierda
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 8.dp)
                .size(48.dp)
                .clip(CircleShape)
                .background(VerdeSuave)
                .clickable(role = Role.Button, onClickLabel = "Volver") { volver() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Undo,
                contentDescription = "Volver",
                tint = VerdePrincipal
            )
        }
    }
}
