package com.example.corpalivapp

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    onMenuClick: () -> Unit,
    onPerfilClick: () -> Unit,
    avatarPath: String? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "CORPALIV",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VerdePrincipal
                )
                Text(text = "Alba Lab", fontSize = 12.sp, color = TextoSecundario)
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menú",
                    tint = VerdePrincipal
                )
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(46.dp)
                    .clip(CircleShape)
                    .clickable(role = Role.Button, onClickLabel = "Mi perfil") {
                        onPerfilClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                FotoPerfil(
                    imagenUri = avatarPath?.let { Uri.fromFile(File(it)) },
                    tamano = 44.dp,
                    descripcion = "Mi perfil"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Marfil,
            titleContentColor = TextoPrincipal
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun BarraSuperiorPreview() {
    BarraSuperior(
        onMenuClick = {},
        onPerfilClick = {}
    )
}
