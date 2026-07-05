package com.shoppinglisthome.app.data

import kotlinx.coroutines.flow.Flow

class ShoppingRepository(private val dao: ShoppingItemDao) {

    fun observeItems(): Flow<List<ShoppingItem>> = dao.observeAll()

    suspend fun addItem(name: String, category: ItemCategory, quantity: String?, unit: String?) {
        dao.insert(ShoppingItem(name = name, category = category, quantity = quantity, unit = unit))
    }

    suspend fun toggleItem(item: ShoppingItem) {
        dao.update(item.copy(isChecked = !item.isChecked))
    }

    suspend fun deleteItem(item: ShoppingItem) {
        dao.delete(item)
    }

    suspend fun removeByName(name: String, items: List<ShoppingItem>) {
        items.firstOrNull { it.name.equals(name, ignoreCase = true) && !it.isChecked }
            ?.let { dao.delete(it) }
    }

    suspend fun clearChecked(items: List<ShoppingItem>) {
        items.filter { it.isChecked }.forEach { dao.delete(it) }
    }
}
