package com.example.corpalivapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(onMenuClick: () -> Unit) {
    TopAppBar(
        title = {
            Column {
                Text(
                    "CORPALIV",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = VerdePrincipal
                )
                Text("Alba Lab", fontSize = 12.sp, color = TextoSecundario)
            }
        },
        navigationIcon = {
            // Círculo de perfil (imagen provisional: icono de persona). Abre el menú.
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(VerdeSuave)
                    .clickable(role = Role.Button, onClickLabel = "Abrir menú") { onMenuClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Abrir menú",
                    tint = VerdePrincipal,
                    modifier = Modifier.size(30.dp)
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
    BarraSuperior(onMenuClick = {})
}