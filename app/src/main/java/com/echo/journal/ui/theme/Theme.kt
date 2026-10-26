package com.echo.journal.ui.theme

import android.app.Activity
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val EchoDarkScheme = darkColorScheme(
    primary = EchoAccent,
    onPrimary = EchoNight,
    primaryContainer = Color(0xFF34275C),
    onPrimaryContainer = EchoAccentSoft,
    secondary = EchoAccentSoft,
    onSecondary = EchoNight,
    secondaryContainer = Color(0xFF29213F),
    onSecondaryContainer = EchoTextPrimary,
    tertiary = Color(0xFF8FDDBE),
    onTertiary = EchoNight,
    background = EchoNight,
    onBackground = EchoTextPrimary,
    surface = EchoNightSoft,
    onSurface = EchoTextPrimary,
    surfaceVariant = EchoElevated,
    onSurfaceVariant = EchoTextMuted,
    surfaceContainer = Color(0xFF15151F),
    surfaceContainerHigh = Color(0xFF1A1A26),
    surfaceContainerHighest = Color(0xFF20202D),
    outline = Color(0xFF76768C),
    outlineVariant = Color(0xFF353546),
    error = EchoDanger,
    onError = Color(0xFF3F1212),
    scrim = Color(0xCC050508),
)

val LocalReduceMotion = staticCompositionLocalOf { false }

@Composable
fun rememberReduceMotion(): Boolean {
    val context = LocalContext.current
    var reduced by remember { mutableStateOf(readReducedMotion(context)) }

    DisposableEffect(context) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                reduced = readReducedMotion(context)
            }
        }
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE),
            false,
            observer,
        )
        onDispose { context.contentResolver.unregisterContentObserver(observer) }
    }
    return reduced
}

private fun readReducedMotion(context: android.content.Context): Boolean {
    val scale = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f,
    )
    return scale == 0f
}

@Composable
fun EchoTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    val reduceMotion = rememberReduceMotion()

    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            val window = activity?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalReduceMotion provides reduceMotion,
    ) {
        MaterialTheme(
            colorScheme = EchoDarkScheme,
            typography = EchoTypography,
            shapes = EchoShapes,
            content = content,
        )
    }
}
