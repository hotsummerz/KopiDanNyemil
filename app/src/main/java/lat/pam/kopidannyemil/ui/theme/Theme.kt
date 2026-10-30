package lat.pam.kopidannyemil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Caramel = Color(0xFFC8742B)
val Cream = Color(0xFFFFF8F0)
val Peach = Color(0xFFF8D5B0)
val PeachSoft = Color(0xFFFFF0E1)
val Ink = Color(0xFF1A1A1A)
val MintOk = Color(0xFFB4F2CC)

private val Scheme = lightColorScheme(
    primary = Ink,
    onPrimary = Color.White,
    secondary = Caramel,
    background = Cream,
    surface = Color.White,
    onBackground = Ink,
    onSurface = Ink
)

@Composable
fun KopiDanNyemilTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = Typography, content = content)
}