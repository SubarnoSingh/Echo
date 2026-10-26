package com.echo.journal.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.echo.journal.EchoApplication
import com.echo.journal.data.repository.EchoRepository

@Composable
fun rememberRepository(): EchoRepository {
    val context = LocalContext.current
    return remember(context) {
        (context.applicationContext as EchoApplication).repository
    }
}

@Composable
inline fun <reified VM : ViewModel> echoViewModel(
    key: String? = null,
    crossinline create: () -> VM,
): VM {
    val factory = remember { viewModelFactory { initializer { create() } } }
    return if (key == null) {
        viewModel<VM>(factory = factory)
    } else {
        viewModel<VM>(key = key, factory = factory)
    }
}

@Composable
fun statusBarTopPadding(): Dp =
    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

@Composable
fun navigationBarBottomPadding(): Dp =
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

@Composable
fun mainScreenBottomPadding(): Dp = navigationBarBottomPadding() + 108.dp
