package com.example.corpalivapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ---------------------------------------------------------------------------
// Pantallas
// ---------------------------------------------------------------------------

@Composable
fun QuienesSomosScreen(onIrA: (Destino) -> Unit) {
    PantallaBase(
        titulo = "Quiénes somos",
        introduccion = "CORPALIV es una organización sin fines de lucro que promueve la autonomía, " +
                "la participación y la inclusión social y laboral de personas con discapacidad múltiple."
    ) {
        Subtitulo("Nuestro nombre")
        Text(
            "Corporación de Padres y Amigos por el Limitado Visual (CORPALIV).",
            fontSize = 16.sp,
            color = TextoSecundario
        )

        TarjetaContenido(
            icono = Icons.Default.Favorite,
            titulo = "Nuestro propósito",
            texto = "Que las personas con discapacidad múltiple tengan más autonomía, participen " +
                    "en la sociedad y accedan a oportunidades laborales."
        )
        TarjetaContenido(
            icono = Icons.Default.School,
            titulo = "Escuela Especial Jan Van Dijk",
            texto = "Una de las vías a través de las cuales CORPALIV desarrolla sus programas " +
                    "de formación y preparación para el empleo."
        )
        TarjetaContenido(
            icono = Icons.Default.Groups,
            titulo = "Red Sociolaboral Alba Lab",
            texto = "Impulsa la producción y comercialización de productos, y la vinculación " +
                    "con empresas, familias y donantes."
        )

        BotonPrincipal("Ver formas de colaborar") { onIrA(Destino.Colaborar) }
    }
}

@Composable
fun ProgramasScreen(onIrA: (Destino) -> Unit) {
    PantallaBase(
        titulo = "Programas",
        introduccion = "Estas son las áreas de trabajo con las que CORPALIV busca generar " +
                "inclusión social y laboral."
    ) {
        TarjetaContenido(
            icono = Icons.Default.School,
            titulo = "Formación",
            texto = "Programas para desarrollar habilidades y fortalecer la autonomía."
        )
        TarjetaContenido(
            icono = Icons.Default.Work,
            titulo = "Preparación para el empleo",
            texto = "Acompañamiento para prepararse y participar en el mundo laboral."
        )
        TarjetaContenido(
            icono = Icons.Default.Storefront,
            titulo = "Producción y comercialización",
            texto = "Elaboración y venta de productos con impacto social."
        )
        TarjetaContenido(
            icono = Icons.Default.Business,
            titulo = "RSE y vinculación",
            texto = "Iniciativas de responsabilidad social empresarial y trabajo conjunto con " +
                    "empresas, familias y donantes."
        )

        Subtitulo("Impacto en cifras")
        TarjetaContenido(
            icono = Icons.Default.Info,
            titulo = "Indicadores en preparación",
            texto = "Publicaremos indicadores de impacto cuando CORPALIV los entregue " +
                    "verificados y autorizados.",
            etiqueta = "Próximamente"
        )

        BotonPrincipal("Quiero colaborar") { onIrA(Destino.Colaborar) }
    }
}

@Composable
fun ProductosScreen(onIrA: (Destino) -> Unit) {
    PantallaBase(
        titulo = "Productos y servicios",
        introduccion = "Explora la oferta de productos, servicios e iniciativas con impacto " +
                "social de Alba Lab."
    ) {
        TarjetaContenido(
            icono = Icons.Default.Storefront,
            titulo = "Productos",
            texto = "Productos elaborados en Alba Lab.",
            textoBoton = "Solicitar información"
        ) { onIrA(Destino.Contacto) }

        TarjetaContenido(
            icono = Icons.Default.Build,
            titulo = "Servicios",
            texto = "Servicios que puedes solicitar a Alba Lab.",
            textoBoton = "Solicitar información"
        ) { onIrA(Destino.Contacto) }

        TarjetaContenido(
            icono = Icons.Default.School,
            titulo = "Capacitaciones e iniciativas de RSE",
            texto = "Actividades de formación y responsabilidad social para empresas.",
            textoBoton = "Solicitar información"
        ) { onIrA(Destino.Contacto) }

        Text(
            "No mostramos precios ni disponibilidad hasta contar con el catálogo oficial.",
            fontSize = 14.sp,
            color = TextoSecundario
        )
    }
}

@Composable
fun HistoriasScreen(onIrA: (Destino) -> Unit) {
    PantallaBase(
        titulo = "Historias de impacto",
        introduccion = "Aquí compartiremos historias de participación e inclusión, siempre con " +
                "autorización y cuidando la privacidad de las personas."
    ) {

        TarjetaContenido(
            icono = Icons.Default.Business,
            titulo = "Una empresa se suma",
            texto = "Una empresa conoce Alba Lab, solicita información y coordina con el equipo " +
                    "de vinculación una alianza."
        )
        TarjetaContenido(
            icono = Icons.Default.Groups,
            titulo = "Una familia se involucra",
            texto = "Una familia pregunta cómo participar como voluntaria y recibe orientación " +
                    "del equipo."
        )

        Text(
            "Estas historias son ejemplos inventados para mostrar cómo se verá esta sección. " +
                    "Las historias reales se publicarán solo con autorización y de forma anónima " +
                    "cuando corresponda.",
            fontSize = 14.sp,
            color = TextoSecundario
        )

        BotonPrincipal("Quiero colaborar") { onIrA(Destino.Colaborar) }
    }
}

@Composable
fun ColaborarScreen(onIrA: (Destino) -> Unit) {
    PantallaBase(
        titulo = "Formas de colaborar",
        introduccion = "Hay varias maneras de ser parte. Elige la que mejor se ajuste a ti " +
                "o a tu empresa."
    ) {
        TarjetaContenido(
            icono = Icons.Default.Savings,
            titulo = "Donaciones",
            texto = "Aportes que apoyan las actividades de la organización. CORPALIV informará " +
                    "el destino de los aportes.",
            textoBoton = "Quiero participar"
        ) { onIrA(Destino.Contacto) }

        TarjetaContenido(
            icono = Icons.Default.Business,
            titulo = "Alianzas empresariales",
            texto = "Tu empresa puede colaborar con CORPALIV y Alba Lab en iniciativas conjuntas.",
            textoBoton = "Quiero participar"
        ) { onIrA(Destino.Contacto) }

        TarjetaContenido(
            icono = Icons.Default.Groups,
            titulo = "Voluntariado",
            texto = "Participa con tu tiempo y tus habilidades en las actividades de la organización.",
            textoBoton = "Quiero participar"
        ) { onIrA(Destino.Contacto) }

        TarjetaContenido(
            icono = Icons.Default.ShoppingCart,
            titulo = "Compra de productos",
            texto = "Adquiere productos elaborados en Alba Lab.",
            textoBoton = "Quiero participar"
        ) { onIrA(Destino.Contacto) }

        TarjetaContenido(
            icono = Icons.Default.Build,
            titulo = "Contratación de servicios",
            texto = "Solicita servicios de Alba Lab para tu empresa o institución.",
            textoBoton = "Quiero participar"
        ) { onIrA(Destino.Contacto) }

        TarjetaContenido(
            icono = Icons.Default.School,
            titulo = "Capacitaciones e iniciativas de RSE",
            texto = "Actividades de formación y responsabilidad social empresarial para tu equipo.",
            textoBoton = "Quiero participar"
        ) { onIrA(Destino.Contacto) }
    }
}

@Composable
fun ContactoScreen() {
    PantallaBase(
        titulo = "Contacto",
        introduccion = "Cuéntanos cómo te gustaría colaborar. El equipo de vinculación de " +
                "CORPALIV y Alba Lab dará seguimiento a tu solicitud."
    ) {
        Subtitulo("¿Cómo funciona?")

        TarjetaContenido(
            icono = Icons.Default.Favorite,
            titulo = "1. Elige cómo colaborar",
            texto = "Donación, voluntariado, alianza, compra de productos, servicios o capacitaciones."
        )
        TarjetaContenido(
            icono = Icons.Default.Email,
            titulo = "2. Déjanos tus datos",
            texto = "Tu nombre, un correo de contacto y, si quieres, un mensaje."
        )
        TarjetaContenido(
            icono = Icons.Default.Call,
            titulo = "3. Te contactamos",
            texto = "El equipo de vinculación se pondrá en contacto contigo."
        )

        TarjetaContenido(
            icono = Icons.Default.Info,
            titulo = "Versión de prueba",
            texto = "Mientras pruebas la app, usa solo datos ficticios."
        )
    }
}

// ---------------------------------------------------------------------------
// Componentes reutilizables (privados a este archivo)
// ---------------------------------------------------------------------------

@Composable
private fun PantallaBase(
    titulo: String,
    introduccion: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Marfil)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(titulo, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
        Text(introduccion, fontSize = 16.sp, color = TextoSecundario)
        contenido()
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Subtitulo(texto: String) {
    Text(texto, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
}

@Composable
private fun Etiqueta(texto: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(VerdeSuave)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(texto, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextoPrincipal)
    }
}

@Composable
private fun BotonPrincipal(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = VerdePrincipal,
            contentColor = Color.White
        )
    ) {
        Text(texto, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun TarjetaContenido(
    icono: ImageVector,
    titulo: String,
    texto: String,
    etiqueta: String? = null,
    textoBoton: String? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(VerdeSuave),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icono, contentDescription = null, tint = VerdePrincipal)
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
                    Text(texto, fontSize = 16.sp, color = TextoSecundario)
                }
            }
            if (etiqueta != null) {
                Etiqueta(etiqueta)
            }
            if (textoBoton != null) {
                BotonPrincipal(textoBoton, onClick)
            }
        }
    }
}