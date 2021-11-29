package android.example.myinventory_mvp_neobis.view

import android.app.Application
import android.example.myinventory_mvp_neobis.model.database.ItemDatabase

class InventoryApplication: Application() {
    val database: ItemDatabase by lazy { ItemDatabase.getInstance(this) }
}