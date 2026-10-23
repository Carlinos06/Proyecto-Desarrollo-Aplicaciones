package com.example.corpalivapp

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage

/** Círculo con la foto de perfil. Si todavía no hay foto, muestra un icono de persona. */
@Composable
fun FotoPerfil(
    imagenUri: Uri?,
    tamano: Dp,
    modifier: Modifier = Modifier,
    fondo: Color = VerdeSuave,
    descripcion: String? = null
) {
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        if (imagenUri != null) {
            AsyncImage(
                model = imagenUri,
                contentDescription = descripcion,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = descripcion,
                tint = VerdePrincipal,
                modifier = Modifier.size(tamano * 0.6f)
            )
        }
    }
}