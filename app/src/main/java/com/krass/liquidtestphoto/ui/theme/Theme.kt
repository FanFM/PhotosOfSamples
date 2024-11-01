package com.krass.liquidtestphoto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import com.krass.liquidtestphoto.main.composables.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = Black,
    secondary = SuvaGrey,
    tertiary = SuvaGrey2,
    background = Background,
    surface = Surface,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onTertiary = OnTertiary,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Error,
    onError = Color.White,
    surfaceContainer = Black
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Tertiary,
    background = Background,
    surface = Surface,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onTertiary = OnTertiary,
    onBackground = Color.White,
    onSurface = OnSurface,
    error = Error,
    onError = Color.White,
    surfaceContainer = SurfaceContainer
)

@Composable
fun MainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        /*dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }*/ //TODO commented to take a contrasting screenshot

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, darkTheme, dynamicColor) {
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme,
            navigationBarContrastEnforced = false
        )

        onDispose { }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}