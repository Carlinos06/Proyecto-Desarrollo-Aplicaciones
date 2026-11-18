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
import com.example.corpalivapp.Marfil
import com.example.corpalivapp.TextoPrincipal
import com.example.corpalivapp.TextoSecundario
import com.example.corpalivapp.VerdePrincipal
import com.example.corpalivapp.VerdeSuave

private val DarkColorScheme = darkColorScheme(
    primary = VerdePrincipal,
    onPrimary = Color(0xFF00390F),
    primaryContainer = Color(0xFF1F5C31),
    onPrimaryContainer = Color(0xFFD7F5DC),
    secondary = VerdeSuave,
    onSecondary = TextoPrincipal,
    secondaryContainer = Color(0xFF2A3A2D),
    onSecondaryContainer = Color(0xFFD7F5DC),
    tertiary = TextoSecundario,
    onTertiary = Color.White,
    background = Color(0xFF111812),
    onBackground = Color(0xFFE5F0E6),
    surface = Color(0xFF1A231C),
    onSurface = Color(0xFFE5F0E6),
    surfaceVariant = Color(0xFF2A3A2D),
    onSurfaceVariant = Color(0xFFA8B5A9),
    outline = Color(0xFF4A5A4C),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = VerdePrincipal,
    onPrimary = Color.White,
    primaryContainer = VerdeSuave,
    onPrimaryContainer = TextoPrincipal,
    secondary = TextoSecundario,
    onSecondary = Color.White,
    secondaryContainer = VerdeSuave,
    onSecondaryContainer = TextoPrincipal,
    tertiary = TextoPrincipal,
    onTertiary = Color.White,
    background = Marfil,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = VerdeSuave,
    onSurfaceVariant = TextoSecundario,
    outline = VerdeSuave,
    error = Color(0xFFB3261E),
    onError = Color.White
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
