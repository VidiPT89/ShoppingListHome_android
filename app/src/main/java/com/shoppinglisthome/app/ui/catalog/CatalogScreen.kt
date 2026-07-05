@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.shoppinglisthome.app.ui.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoppinglisthome.app.R
import com.shoppinglisthome.app.data.CatalogData
import com.shoppinglisthome.app.data.CatalogItem
import com.shoppinglisthome.app.data.ItemCategory
import com.shoppinglisthome.app.ui.home.HomeViewModel

@Composable
fun CatalogScreen(viewModel: HomeViewModel, onClose: () -> Unit) {
    val items by viewModel.items.collectAsState()
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(setOf<ItemCategory>()) }
    var pendingItem by remember { mutableStateOf<CatalogItem?>(null) }

    fun isInList(item: CatalogItem) = items.any { it.name.equals(item.name, ignoreCase = true) && !it.isChecked }

    val grouped = ItemCategory.entries.associateWith { cat ->
        CatalogData.all.filter { it.category == cat }
            .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
    }.filterValues { it.isNotEmpty() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.catalog_title)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.catalog_close))
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text(stringResource(R.string.catalog_search_placeholder)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true
            )
            Divider()

            if (grouped.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.catalog_no_results, query))
                }
            } else {
                LazyColumn {
                    grouped.forEach { (cat, catItems) ->
                        item(key = "header_${cat.name}") {
                            CategoryHeader(
                                category = cat,
                                count = catItems.count { isInList(it) },
                                isExpanded = expanded.contains(cat),
                                onToggle = {
                                    expanded = if (expanded.contains(cat)) expanded - cat else expanded + cat
                                }
                            )
                        }
                        if (expanded.contains(cat)) {
                            items(catItems, key = { "${cat.name}_${it.name}" }) { catalogItem ->
                                CatalogRow(
                                    item = catalogItem,
                                    isInList = isInList(catalogItem),
                                    onTap = {
                                        if (isInList(catalogItem)) {
                                            viewModel.removeByName(catalogItem.name)
                                        } else {
                                            pendingItem = catalogItem
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    pendingItem?.let { item ->
        QuantityDialog(
            item = item,
            onDismiss = { pendingItem = null },
            onConfirm = { qty, unit ->
                viewModel.addItem(item.name, item.category, qty, unit)
                pendingItem = null
            }
        )
    }
}

@Composable
private fun CategoryHeader(category: ItemCategory, count: Int, isExpanded: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(category.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.padding(start = 10.dp))
        Text(
            text = stringResource(category.labelRes),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        if (count > 0) {
            CountBadge(count)
        }
        Icon(if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
    }
    Divider()
}

@Composable
private fun CountBadge(count: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(20.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
    ) {
        Text(text = "$count", color = Color.White, fontSize = 11.sp)
    }
}

@Composable
private fun CatalogRow(item: CatalogItem, isInList: Boolean, onTap: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isInList) Icons.Filled.Check else Icons.Filled.Add,
            contentDescription = null,
            tint = if (isInList) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.padding(start = 14.dp))
        Text(
            text = item.name,
            color = if (isInList) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun QuantityDialog(item: CatalogItem, onDismiss: () -> Unit, onConfirm: (String, String?) -> Unit) {
    var quantity by remember { mutableStateOf(1) }
    val units = listOf("un.", "kg", "g", "L", "mL", "pct.", "cx.", "dz.")
    var selectedUnit by remember { mutableStateOf(units.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(item.name) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { if (quantity > 1) quantity-- }) {
                        Icon(Icons.Filled.Remove, contentDescription = null)
                    }
                    Text(text = "$quantity", modifier = Modifier.padding(horizontal = 24.dp), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { quantity++ }) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.padding(top = 12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    units.forEach { unit ->
                        val selected = unit == selectedUnit
                        Text(
                            text = unit,
                            modifier = Modifier
                                .clickable { selectedUnit = unit }
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(50)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val unit = if (selectedUnit == "un.") null else selectedUnit
                onConfirm("$quantity", unit)
            }) {
                Text(stringResource(R.string.catalog_add_to_list))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.item_cancel)) }
        }
    )
}
