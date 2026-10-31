package lat.pam.kopidannyemil.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Scheme = lightColorScheme(
    primary = Ink,
    onPrimary = Color.White,
    secondary = Caramel,
    background = Cream,
    surface = Color.White,
    onBackground = Ink,
    onSurface = Ink,
)

@Composable
fun KopiDanNyemilTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = Typography, content = content)
}