package com.shoppinglisthome.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoppinglisthome.app.R
import com.shoppinglisthome.app.data.ItemCategory
import com.shoppinglisthome.app.data.ShoppingItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenCatalog: () -> Unit,
    onOpenAddItem: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    var selectedCategory by remember { mutableStateOf<ItemCategory?>(null) }

    val filtered = if (selectedCategory == null) items else items.filter { it.category == selectedCategory }
    val unchecked = filtered.filter { !it.isChecked }
    val checked = filtered.filter { it.isChecked }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                SmallFloatingActionButton(onClick = onOpenAddItem) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.item_new))
                }
                Spacer(modifier = Modifier.height(12.dp))
                FloatingActionButton(onClick = onOpenCatalog) {
                    Icon(Icons.Filled.Checklist, contentDescription = stringResource(R.string.catalog_title))
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (items.any { it.isChecked }) {
                        IconButton(onClick = { viewModel.clearChecked() }) {
                            Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings_title))
                    }
                }
            }

            // Category chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text(stringResource(R.string.home_all)) }
                )
                ItemCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                        label = { Text(stringResource(cat.labelRes)) },
                        leadingIcon = { Icon(cat.icon, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }

            Divider(modifier = Modifier.padding(top = 8.dp))

            if (items.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    if (unchecked.isNotEmpty()) {
                        item { SectionHeader(stringResource(R.string.home_section_tobuy), unchecked.size) }
                        items(unchecked, key = { it.id }) { shoppingItem ->
                            ItemRow(shoppingItem, onToggle = { viewModel.toggleItem(shoppingItem) }, onDelete = { viewModel.deleteItem(shoppingItem) })
                            Divider(modifier = Modifier.padding(start = 58.dp))
                        }
                    }
                    if (checked.isNotEmpty()) {
                        item { SectionHeader(stringResource(R.string.home_section_incart), checked.size) }
                        items(checked, key = { it.id }) { shoppingItem ->
                            ItemRow(shoppingItem, onToggle = { viewModel.toggleItem(shoppingItem) }, onDelete = { viewModel.deleteItem(shoppingItem) })
                            Divider(modifier = Modifier.padding(start = 58.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.padding(start = 4.dp))
        Text(text = "($count)", color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun ItemRow(item: ShoppingItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (item.isChecked) MaterialTheme.colorScheme.primary else Color.Transparent)
                .border(1.5.dp, if (item.isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, CircleShape)
                .clickable(onClick = onToggle)
        ) {
            if (item.isChecked) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontWeight = FontWeight.Medium,
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
            Row {
                Icon(item.category.icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Text(
                    text = stringResource(item.category.labelRes) + (item.quantity?.takeIf { it.isNotBlank() }?.let { " · $it${item.unit?.let { u -> " $u" } ?: ""}" } ?: ""),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(text = stringResource(R.string.home_empty_title), fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Spacer(modifier = Modifier.padding(top = 4.dp))
        Text(
            text = stringResource(R.string.home_empty_subtitle),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}
