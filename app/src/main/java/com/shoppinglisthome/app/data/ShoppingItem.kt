package com.shoppinglisthome.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: ItemCategory,
    val quantity: String? = null,
    val unit: String? = null,
    val isChecked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
