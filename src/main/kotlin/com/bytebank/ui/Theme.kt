package com.bytebank.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Indigo = Color(0xFF1E2A5E)
private val IndigoLight = Color(0xFF3B4A8C)
private val Teal = Color(0xFF00B8A9)
private val Background = Color(0xFFF4F6FB)
private val Surface = Color(0xFFFFFFFF)
private val SurfaceVariant = Color(0xFFE9ECF6)
private val ErrorRed = Color(0xFFD5484D)
private val TextPrimary = Color(0xFF1B1F3B)
private val TextSecondary = Color(0xFF6B7094)
private val Outline = Color(0xFFD6DAEB)

private val ByteBankColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = IndigoLight,
    onPrimaryContainer = Color.White,
    secondary = Teal,
    onSecondary = Color.White,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    outline = Outline
)

/** Cores semânticas que não fazem parte da paleta padrão do Material3. */
object BankColors {
    val Success = Color(0xFF1F9D55)
    val SuccessContainer = Color(0xFFE3F6EA)
    val Danger = ErrorRed
    val DangerContainer = Color(0xFFFBE7E8)
}

@Composable
fun ByteBankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ByteBankColorScheme,
        typography = Typography(),
        content = content
    )
}
