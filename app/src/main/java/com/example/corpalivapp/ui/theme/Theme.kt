package com.example.corpalivapp.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.corpalivapp.AmarilloCorpaliv
import com.example.corpalivapp.BordeSuave
import com.example.corpalivapp.LavandaSuave
import com.example.corpalivapp.MagentaCorpaliv
import com.example.corpalivapp.Marfil
import com.example.corpalivapp.MoradoCorpaliv
import com.example.corpalivapp.MoradoMedio
import com.example.corpalivapp.MoradoOscuro
import com.example.corpalivapp.RojoCorpaliv
import com.example.corpalivapp.RosaCorpaliv
import com.example.corpalivapp.TextoPrincipal
import com.example.corpalivapp.TextoSecundario

private val DarkColorScheme = darkColorScheme(
    primary = MoradoMedio,
    onPrimary = Color(0xFF2A0045),
    primaryContainer = MoradoCorpaliv,
    onPrimaryContainer = Color.White,
    secondary = RosaCorpaliv,
    onSecondary = Color(0xFF3A005C),
    secondaryContainer = MoradoOscuro,
    onSecondaryContainer = Color.White,
    tertiary = AmarilloCorpaliv,
    onTertiary = Color(0xFF3A005C),
    background = Color(0xFF14001F),
    onBackground = Color(0xFFF5EEFA),
    surface = Color(0xFF1F0030),
    onSurface = Color(0xFFF5EEFA),
    surfaceVariant = Color(0xFF2E0045),
    onSurfaceVariant = Color(0xFFD5C2E6),
    outline = Color(0xFF6B4A85),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = MoradoCorpaliv,
    onPrimary = Color.White,
    primaryContainer = LavandaSuave,
    onPrimaryContainer = MoradoOscuro,
    secondary = MagentaCorpaliv,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE4F0),
    onSecondaryContainer = TextoPrincipal,
    tertiary = RosaCorpaliv,
    onTertiary = Color(0xFF3A005C),
    tertiaryContainer = Color(0xFFFDE7F1),
    onTertiaryContainer = MoradoOscuro,
    background = Marfil,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = LavandaSuave,
    onSurfaceVariant = TextoSecundario,
    outline = BordeSuave,
    outlineVariant = BordeSuave,
    error = RojoCorpaliv,
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B)
)

@Composable
fun CorpalivAppTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
