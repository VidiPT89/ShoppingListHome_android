package com.shoppinglisthome.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromCategory(category: ItemCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): ItemCategory = ItemCategory.valueOf(value)
}
