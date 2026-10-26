package com.echo.journal.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.echo.journal.ui.theme.EchoAccent
import com.echo.journal.ui.theme.EchoAccentDeep
import com.echo.journal.ui.theme.EchoAccentSoft
import com.echo.journal.ui.theme.EchoElevated
import com.echo.journal.ui.theme.EchoNight
import com.echo.journal.ui.theme.EchoRadius
import com.echo.journal.ui.theme.EchoSpacing
import com.echo.journal.ui.theme.EchoTextFaint
import com.echo.journal.ui.theme.EchoTextMuted
import com.echo.journal.ui.theme.EchoTextPrimary
import com.echo.journal.ui.theme.EchoGlowViolet

enum class GlassLevel {
    Subtle,
    Card,
    Hero,
    Navigation,
    Button,
}

private data class GlassSpec(
    val topFill: Float,
    val midFill: Float,
    val bottomFill: Float,
    val borderTop: Float,
    val borderBottom: Float,
    val innerGlow: Float,
    val elevation: Dp,
)

private val GlassLevel.spec: GlassSpec
    get() = when (this) {
        GlassLevel.Subtle -> GlassSpec(
            topFill = 0.055f, midFill = 0.03f, bottomFill = 0.018f,
            borderTop = 0.12f, borderBottom = 0.025f, innerGlow = 0.05f, elevation = 4.dp,
        )
        GlassLevel.Card -> GlassSpec(
            topFill = 0.105f, midFill = 0.055f, bottomFill = 0.032f,
            borderTop = 0.19f, borderBottom = 0.035f, innerGlow = 0.10f, elevation = 12.dp,
        )
        GlassLevel.Hero -> GlassSpec(
            topFill = 0.145f, midFill = 0.07f, bottomFill = 0.04f,
            borderTop = 0.24f, borderBottom = 0.045f, innerGlow = 0.15f, elevation = 20.dp,
        )
        GlassLevel.Navigation -> GlassSpec(
            topFill = 0.13f, midFill = 0.065f, bottomFill = 0.045f,
            borderTop = 0.21f, borderBottom = 0.045f, innerGlow = 0.10f, elevation = 28.dp,
        )
        GlassLevel.Button -> GlassSpec(
            topFill = 0.16f, midFill = 0.08f, bottomFill = 0.04f,
            borderTop = 0.24f, borderBottom = 0.05f, innerGlow = 0.12f, elevation = 8.dp,
        )
    }

/**
 * A translucent, layered frosted panel. The fill, internal light and hairline border are painted
 * from cached brushes, so scrolling cards do not allocate gradients per frame. This is a visual
 * approximation of backdrop blur rather than a pixel-sampling blur.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    level: GlassLevel = GlassLevel.Card,
    shape: Shape = RoundedCornerShape(EchoRadius.card),
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(EchoSpacing.lg),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spec = level.spec
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && onClick != null && enabled) 0.986f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "glass-press",
    )

    val interactive = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            enabled = enabled,
            role = Role.Button,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = spec.elevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.28f),
                spotColor = Color.Black.copy(alpha = 0.48f),
            )
            .clip(shape)
            .drawWithCache {
                val base = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = spec.topFill),
                        Color.White.copy(alpha = spec.midFill),
                        Color.White.copy(alpha = spec.bottomFill),
                    ),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height),
                )
                val glow = Brush.radialGradient(
                    colors = listOf(
                        EchoGlowViolet.copy(alpha = spec.innerGlow),
                        EchoAccent.copy(alpha = spec.innerGlow * 0.35f),
                        Color.Transparent,
                    ),
                    center = Offset(size.width * 0.86f, size.height * 0.02f),
                    radius = size.width.coerceAtLeast(1f) * 0.9f,
                )
                val sheen = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = spec.topFill * 0.45f),
                        Color.Transparent,
                    ),
                    startY = 0f,
                    endY = size.height.coerceAtLeast(1f) * 0.42f,
                )
                onDrawBehind {
                    drawRect(base)
                    drawRect(glow)
                    drawRect(sheen)
                }
            }
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = spec.borderTop),
                        Color.White.copy(alpha = spec.borderBottom),
                    ),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                ),
                shape = shape,
            )
            .then(interactive)
            .padding(contentPadding),
        verticalArrangement = verticalArrangement,
        content = content,
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    level: GlassLevel = GlassLevel.Card,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(EchoSpacing.lg),
    content: @Composable ColumnScope.() -> Unit,
) {
    GlassSurface(
        modifier = modifier,
        level = level,
        shape = RoundedCornerShape(EchoRadius.card),
        onClick = onClick,
        contentPadding = contentPadding,
        content = content,
    )
}

enum class GlassButtonStyle { Primary, Secondary }

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: GlassButtonStyle = GlassButtonStyle.Primary,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    loading: Boolean = false,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "button-press",
    )
    val alpha by animateFloatAsState(if (enabled) 1f else 0.42f, label = "button-alpha")
    val shape = RoundedCornerShape(EchoRadius.pill)

    val background: Modifier = when (style) {
        GlassButtonStyle.Primary -> Modifier.background(
            Brush.linearGradient(
                listOf(EchoAccentSoft, EchoAccent, EchoAccentDeep),
                start = Offset.Zero,
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
            ),
            shape,
        )
        GlassButtonStyle.Secondary -> Modifier
            .background(
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.14f),
                        Color.White.copy(alpha = 0.055f),
                    ),
                ),
                shape,
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.045f),
                    ),
                ),
                shape,
            )
    }
    val contentColor = when (style) {
        GlassButtonStyle.Primary -> Color(0xFF1A1230)
        GlassButtonStyle.Secondary -> EchoTextPrimary
    }

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .height(50.dp)
            .clip(shape)
            .then(background)
            .clickable(
                interactionSource = interactionSource,
                enabled = enabled && !loading,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = contentColor,
                strokeWidth = 2.dp,
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        if (!loading) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
            )
        }
    }
}

@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 48.dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "icon-press",
    )
    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.4f
            }
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.13f), Color.White.copy(alpha = 0.045f)),
                ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.14f), CircleShape)
            .clickable(
                interactionSource = interactionSource,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = EchoTextPrimary,
            modifier = Modifier.size(size * 0.46f),
        )
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 15.dp),
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .clip(RoundedCornerShape(EchoRadius.sm))
            .background(Color.White.copy(alpha = 0.045f))
            .border(1.dp, Color.White.copy(alpha = 0.075f), RoundedCornerShape(EchoRadius.sm))
            .padding(contentPadding),
        textStyle = textStyle.copy(color = EchoTextPrimary),
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        cursorBrush = Brush.verticalGradient(listOf(EchoAccentSoft, EchoAccent)),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle,
                        color = if (placeholder.isEmpty()) EchoTextFaint else EchoTextMuted.copy(alpha = 0.72f),
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
fun GlassSpacer(height: Dp) = Spacer(Modifier.height(height))

@Composable
fun GlassDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.09f),
                        Color.Transparent,
                    ),
                ),
            ),
    )
}

@Composable
fun CalloutPill(
    text: String,
    modifier: Modifier = Modifier,
    tint: Color = EchoAccentSoft,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(EchoRadius.pill))
            .background(tint.copy(alpha = 0.10f))
            .border(1.dp, tint.copy(alpha = 0.18f), RoundedCornerShape(EchoRadius.pill))
            .defaultMinSize(minHeight = 28.dp)
            .padding(horizontal = 11.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(13.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = tint,
            maxLines = 1,
        )
    }
}

// Convenience so callers can express a glass surface without importing the elevated token.
val GlassElevatedColor: Color get() = EchoElevated
val GlassDarkColor: Color get() = EchoNight
