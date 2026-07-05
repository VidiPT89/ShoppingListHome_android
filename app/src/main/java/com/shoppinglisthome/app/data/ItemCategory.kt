package com.shoppinglisthome.app.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector
import com.shoppinglisthome.app.R

enum class ItemCategory(val labelRes: Int) {
    CARNE_PEIXE(R.string.cat_carnePeixe),
    VEGETAIS_FRUTA(R.string.cat_vegetaisFruta),
    LATICINIOS(R.string.cat_laticinios),
    PADARIA(R.string.cat_padaria),
    CONSERVAS_SECOS(R.string.cat_conservasSecos),
    LIMPEZA(R.string.cat_limpeza),
    HIGIENE(R.string.cat_higiene),
    BEBIDAS(R.string.cat_bebidas),
    SNACKS(R.string.cat_snacks),
    OUTROS(R.string.cat_outros);

    val icon: ImageVector
        get() = when (this) {
            CARNE_PEIXE -> Icons.Filled.Restaurant
            VEGETAIS_FRUTA -> Icons.Filled.Eco
            LATICINIOS -> Icons.Filled.EmojiFoodBeverage
            PADARIA -> Icons.Filled.Cake
            CONSERVAS_SECOS -> Icons.Filled.Inventory2
            LIMPEZA -> Icons.Filled.CleaningServices
            HIGIENE -> Icons.Filled.Opacity
            BEBIDAS -> Icons.Filled.LocalBar
            SNACKS -> Icons.Filled.Fastfood
            OUTROS -> Icons.Filled.Category
        }
}
