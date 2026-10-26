package com.echo.journal.ui

import android.net.Uri
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navArgument
import com.echo.journal.ui.components.EchoBackground
import com.echo.journal.ui.components.EchoDestination
import com.echo.journal.ui.components.FloatingGlassNavigation
import com.echo.journal.ui.screens.EditorScreen
import com.echo.journal.ui.screens.HomeScreen
import com.echo.journal.ui.screens.InsightsScreen
import com.echo.journal.ui.screens.JournalScreen
import com.echo.journal.ui.screens.ReaderScreen
import com.echo.journal.ui.theme.EchoNight

private object Routes {
    const val Home = "home"
    const val Journal = "journal"
    const val Insights = "insights"
    const val Reader = "reader/{entryId}"
    const val Editor = "editor?entryId={entryId}&prompt={prompt}"

    fun reader(entryId: Long): String = "reader/$entryId"

    fun editor(entryId: Long? = null, prompt: String? = null): String {
        val id = entryId ?: -1L
        val base = "editor?entryId=$id"
        if (prompt.isNullOrBlank()) return base
        return "$base&prompt=${Uri.encode(prompt)}"
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun EchoApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0
    val isMainDestination = EchoDestination.entries.any { destination ->
        backStackEntry?.destination?.hierarchy?.any { it.route == destination.route } == true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EchoNight),
    ) {
        EchoBackground()

        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Routes.Home,
                modifier = Modifier.fillMaxSize(),
                enterTransition = {
                    fadeIn(tween(260)) + slideInHorizontally(
                        animationSpec = tween(360, easing = FastOutSlowInEasing),
                        initialOffsetX = { it / 28 },
                    )
                },
                exitTransition = {
                    fadeOut(tween(180))
                },
                popEnterTransition = {
                    fadeIn(tween(240))
                },
                popExitTransition = {
                    fadeOut(tween(180)) + slideOutHorizontally(
                        animationSpec = tween(300),
                        targetOffsetX = { it / 28 },
                    )
                },
            ) {
                composable(Routes.Home) {
                    HomeScreen(
                        onWrite = { prompt ->
                            navController.navigate(Routes.editor(prompt = prompt))
                        },
                        onOpenEntry = { id -> navController.navigate(Routes.reader(id)) },
                        onSeeAll = { navigateToTab(navController, EchoDestination.Journal) },
                    )
                }

                composable(Routes.Journal) {
                    JournalScreen(
                        onWrite = { navController.navigate(Routes.editor()) },
                        onOpenEntry = { id -> navController.navigate(Routes.reader(id)) },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                    )
                }

                composable(Routes.Insights) {
                    InsightsScreen(
                        onGoToCheckIn = {
                            navigateToTab(navController, EchoDestination.Home)
                        },
                    )
                }

                composable(
                    route = Routes.Reader,
                    arguments = listOf(navArgument("entryId") { type = NavType.LongType }),
                    enterTransition = {
                        fadeIn(tween(280)) + slideInHorizontally(
                            animationSpec = tween(380, easing = FastOutSlowInEasing),
                            initialOffsetX = { it / 18 },
                        )
                    },
                    popExitTransition = {
                        fadeOut(tween(220)) + slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { it / 18 },
                        )
                    },
                ) { entry ->
                    val entryId = entry.arguments?.getLong("entryId") ?: return@composable
                    ReaderScreen(
                        entryId = entryId,
                        onBack = { navController.popBackStack() },
                        onEdit = { id -> navController.navigate(Routes.editor(entryId = id)) },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this@composable,
                    )
                }

                composable(
                    route = Routes.Editor,
                    arguments = listOf(
                        navArgument("entryId") {
                            type = NavType.LongType
                            defaultValue = -1L
                        },
                        navArgument("prompt") {
                            type = NavType.StringType
                            defaultValue = ""
                        },
                    ),
                    enterTransition = {
                        fadeIn(tween(300)) + slideInHorizontally(
                            animationSpec = tween(420, easing = FastOutSlowInEasing),
                            initialOffsetX = { it },
                        )
                    },
                    popExitTransition = {
                        fadeOut(tween(220)) + slideOutHorizontally(
                            animationSpec = tween(340, easing = FastOutSlowInEasing),
                            targetOffsetX = { it },
                        )
                    },
                ) { entry ->
                    val rawId = entry.arguments?.getLong("entryId") ?: -1L
                    val prompt = entry.arguments?.getString("prompt").orEmpty()
                    EditorScreen(
                        entryId = rawId.takeIf { it > 0 },
                        prompt = prompt,
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() },
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isMainDestination && !imeVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            enter = fadeIn(tween(240)),
            exit = fadeOut(tween(160)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                EchoNight.copy(alpha = 0.88f),
                                EchoNight,
                            ),
                        ),
                    ),
            )
        }

        AnimatedVisibility(
            visible = isMainDestination && !imeVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            enter = fadeIn(tween(240)) + slideInVertically(
                animationSpec = tween(320, easing = FastOutSlowInEasing),
                initialOffsetY = { it / 3 },
            ),
            exit = fadeOut(tween(160)) + slideOutVertically(
                animationSpec = tween(220, easing = FastOutSlowInEasing),
                targetOffsetY = { it / 2 },
            ),
        ) {
            FloatingGlassNavigation(
                currentRoute = currentRoute,
                onSelect = { destination -> navigateToTab(navController, destination) },
            )
        }
    }
}

private fun navigateToTab(
    navController: NavHostController,
    destination: EchoDestination,
) {
    navController.navigate(destination.route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
