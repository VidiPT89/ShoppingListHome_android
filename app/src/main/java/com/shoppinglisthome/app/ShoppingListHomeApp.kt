package com.shoppinglisthome.app

import android.app.Application
import com.shoppinglisthome.app.data.AppDatabase
import com.shoppinglisthome.app.data.SettingsRepository
import com.shoppinglisthome.app.data.ShoppingRepository

class ShoppingListHomeApp : Application() {

    lateinit var repository: ShoppingRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = ShoppingRepository(database.shoppingItemDao())
        settingsRepository = SettingsRepository(this)
    }
}
