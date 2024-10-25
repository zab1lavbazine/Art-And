package art.example

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


// Define the colors
private val ToxicYellow = Color(0xFFB0FF00) // Replace with your toxic yellow/green color
private val Purple = Color(0xFF6200EE) // Purple color for text
private val LightBackground = Color.White // White background
private val DarkBackground = Color(0xFF121212) // Dark background for dark theme

private val LightColorPalette = lightColorScheme(
    primary = Purple,                // Purple for text
    onPrimary = LightBackground,      // White text on purple
    background = LightBackground,      // White background
    surface = LightBackground,         // White surface
    onBackground = ToxicYellow,        // Toxic yellow/green text on white
    onSurface = DarkBackground,           // Toxic yellow/green text on white surface
    error = Color(0xFFB00020),
    onError = Color.White
)

private val DarkColorPalette = darkColorScheme(
    primary = ToxicYellow,             // Toxic yellow/green for text
    onPrimary = Purple,                // Purple text on toxic yellow/green
    background = DarkBackground,        // Dark background
    surface = DarkBackground,           // Dark surface
    onBackground = Purple,              // Purple text on dark background
    onSurface = Purple,                 // Purple text on dark surface
    error = Color(0xFFCF6679),
    onError = Color.Black
)



@Composable
fun MyAppTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorPalette else LightColorPalette

    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}