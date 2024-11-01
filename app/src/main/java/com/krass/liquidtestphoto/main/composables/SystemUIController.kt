package com.krass.liquidtestphoto.main.composables

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

@Composable
fun rememberSystemUiController(window: Window? = findWindow()): SystemUiController {
    val view = LocalView.current

    return remember(view, window) {
        SystemUiController(view, window)
    }
}

@Composable
private fun findWindow(): Window? {
    val window = (LocalView.current.parent as? DialogWindowProvider)?.window

    return window ?: LocalView.current.context.findWindow()
}

private tailrec fun Context.findWindow(): Window? {
    return when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }
}

class SystemUiController(private val view: View, private val window: Window?) {

    private val windowInsetsController = window?.let {
        WindowCompat.getInsetsController(it, view)
    }

    fun setNavigationBarColor(
        color: Color,
        darkIcons: Boolean = color.luminance() > 0.5f,
        navigationBarContrastEnforced: Boolean = true,
        transformColorForLightContent: (Color) -> Color = BlackScrimmed
    ) {
        navigationBarDarkContentEnabled = darkIcons
        isNavigationBarContrastEnforced = navigationBarContrastEnforced

        window?.navigationBarColor = when {
            darkIcons && windowInsetsController?.isAppearanceLightNavigationBars != true -> {
                transformColorForLightContent(color)
            }
            else -> color
        }.toArgb()
    }

    var navigationBarDarkContentEnabled: Boolean
        get() = windowInsetsController?.isAppearanceLightNavigationBars == true
        set(value) {
            windowInsetsController?.isAppearanceLightNavigationBars = value
        }

    var isNavigationBarContrastEnforced: Boolean
        get() = Build.VERSION.SDK_INT >= 29 && window?.isNavigationBarContrastEnforced == true
        set(value) {
            if (Build.VERSION.SDK_INT >= 29) {
                window?.isNavigationBarContrastEnforced = value
            }
        }
}

private val BlackScrim = Color(0f, 0f, 0f, 0.3f) // 30% opaque black

private val BlackScrimmed: (Color) -> Color = { original ->
    BlackScrim.compositeOver(original)
}