package com.shoppinglisthome.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shoppinglisthome.app.data.ThemePreference
import com.shoppinglisthome.app.ui.additem.AddItemScreen
import com.shoppinglisthome.app.ui.catalog.CatalogScreen
import com.shoppinglisthome.app.ui.home.HomeScreen
import com.shoppinglisthome.app.ui.home.HomeViewModel
import com.shoppinglisthome.app.ui.settings.SettingsScreen
import com.shoppinglisthome.app.ui.splash.SplashScreen
import com.shoppinglisthome.app.ui.theme.ShoppingListHomeTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as ShoppingListHomeApp
        setContent {
            ShoppingListHomeRoot(app)
        }
    }
}

@Composable
private fun ShoppingListHomeRoot(app: ShoppingListHomeApp) {
    val theme by app.settingsRepository.theme.collectAsState(initial = ThemePreference.SYSTEM)
    val language by app.settingsRepository.language.collectAsState(initial = "pt")

    LaunchedEffect(language) {
        val locales = LocaleListCompat.forLanguageTags(if (language == "en") "en" else "pt-PT")
        AppCompatDelegate.setApplicationLocales(locales)
    }

    val darkTheme = when (theme) {
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
    }

    ShoppingListHomeTheme(darkTheme = darkTheme) {
        var showSplash by remember { mutableStateOf(true) }
        val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(app.repository))
        var showCatalog by remember { mutableStateOf(false) }
        var showAddItem by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }

        if (showSplash) {
            SplashScreen(onFinished = { showSplash = false })
        } else {
            HomeScreen(
                viewModel = homeViewModel,
                onOpenCatalog = { showCatalog = true },
                onOpenAddItem = { showAddItem = true },
                onOpenSettings = { showSettings = true }
            )

            if (showCatalog) {
                Dialog(
                    onDismissRequest = { showCatalog = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    CatalogScreen(viewModel = homeViewModel, onClose = { showCatalog = false })
                }
            }

            if (showAddItem) {
                AddItemScreen(viewModel = homeViewModel, onDismiss = { showAddItem = false })
            }

            if (showSettings) {
                Dialog(
                    onDismissRequest = { showSettings = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    SettingsScreen(settingsRepository = app.settingsRepository, onClose = { showSettings = false })
                }
            }
        }
    }
}
