@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.shoppinglisthome.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shoppinglisthome.app.R
import com.shoppinglisthome.app.data.SettingsRepository
import com.shoppinglisthome.app.data.ThemePreference
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(settingsRepository: SettingsRepository, onClose: () -> Unit) {
    val scope = rememberCoroutineScope()
    val theme by settingsRepository.theme.collectAsState(initial = ThemePreference.SYSTEM)
    val language by settingsRepository.language.collectAsState(initial = "pt")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(
                text = stringResource(R.string.settings_appearance),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ThemeOption(
                    label = stringResource(R.string.settings_theme_system),
                    selected = theme == ThemePreference.SYSTEM,
                    onClick = { scope.launch { settingsRepository.setTheme(ThemePreference.SYSTEM) } }
                )
                ThemeOption(
                    label = stringResource(R.string.settings_theme_light),
                    selected = theme == ThemePreference.LIGHT,
                    onClick = { scope.launch { settingsRepository.setTheme(ThemePreference.LIGHT) } }
                )
                ThemeOption(
                    label = stringResource(R.string.settings_theme_dark),
                    selected = theme == ThemePreference.DARK,
                    onClick = { scope.launch { settingsRepository.setTheme(ThemePreference.DARK) } }
                )
            }

            Text(
                text = stringResource(R.string.settings_language),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ThemeOption(
                    label = "🇵🇹 Português",
                    selected = language == "pt",
                    onClick = { scope.launch { settingsRepository.setLanguage("pt") } }
                )
                ThemeOption(
                    label = "🇬🇧 English",
                    selected = language == "en",
                    onClick = { scope.launch { settingsRepository.setLanguage("en") } }
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}
