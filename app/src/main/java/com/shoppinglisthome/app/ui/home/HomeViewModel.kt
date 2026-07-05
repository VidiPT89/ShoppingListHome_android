package com.shoppinglisthome.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shoppinglisthome.app.data.ItemCategory
import com.shoppinglisthome.app.data.ShoppingItem
import com.shoppinglisthome.app.data.ShoppingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ShoppingRepository) : ViewModel() {

    val items: StateFlow<List<ShoppingItem>> = repository.observeItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addItem(name: String, category: ItemCategory, quantity: String?, unit: String?) {
        viewModelScope.launch { repository.addItem(name, category, quantity, unit) }
    }

    fun toggleItem(item: ShoppingItem) {
        viewModelScope.launch { repository.toggleItem(item) }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch { repository.deleteItem(item) }
    }

    fun removeByName(name: String) {
        viewModelScope.launch { repository.removeByName(name, items.value) }
    }

    fun clearChecked() {
        viewModelScope.launch { repository.clearChecked(items.value) }
    }

    class Factory(private val repository: ShoppingRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repository) as T
    }
}
