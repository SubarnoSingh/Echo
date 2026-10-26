package com.echo.journal.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echo.journal.ui.theme.EchoAccent
import com.echo.journal.ui.theme.EchoAccentSoft
import com.echo.journal.ui.theme.EchoNightSoft
import com.echo.journal.ui.theme.EchoRadius
import com.echo.journal.ui.theme.EchoTextFaint
import com.echo.journal.ui.theme.EchoTextMuted

enum class EchoDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    Home("home", "Home", Icons.Outlined.Home, Icons.Rounded.Home),
    Journal("journal", "Journal", Icons.Outlined.AutoStories, Icons.Rounded.AutoStories),
    Insights("insights", "Insights", Icons.Outlined.Insights, Icons.Rounded.Insights),
}

@Composable
fun FloatingGlassNavigation(
    currentRoute: String?,
    onSelect: (EchoDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(EchoRadius.nav)
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(EchoNightSoft.copy(alpha = 0.92f), shape),
        level = GlassLevel.Navigation,
        shape = shape,
        contentPadding = PaddingValues(6.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            EchoDestination.entries.forEach { destination ->
                val selected = currentRoute == destination.route
                NavItem(
                    destination = destination,
                    selected = selected,
                    onClick = { onSelect(destination) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    destination: EchoDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val indicator by animateColorAsState(
        targetValue = if (selected) EchoAccent.copy(alpha = 0.14f) else Color.Transparent,
        label = "nav-indicator",
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) EchoAccentSoft else EchoTextMuted,
        label = "nav-icon",
    )
    val labelTint by animateColorAsState(
        targetValue = if (selected) EchoAccentSoft else EchoTextFaint,
        label = "nav-label",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "nav-icon-scale",
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(EchoRadius.pill))
            .background(indicator)
            .selectable(
                selected = selected,
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics {
                stateDescription = if (selected) "Selected" else "Not selected"
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = if (selected) destination.selectedIcon else destination.icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .size(21.dp)
                    .scale(iconScale),
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = destination.label,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.35.sp),
                color = labelTint,
                maxLines = 1,
            )
        }
    }
}
