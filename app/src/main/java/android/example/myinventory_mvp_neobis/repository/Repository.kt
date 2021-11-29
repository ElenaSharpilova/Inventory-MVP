package android.example.myinventory_mvp_neobis.repository

import android.example.myinventory_mvp_neobis.model.Item
import android.example.myinventory_mvp_neobis.model.database.ItemDao
import androidx.lifecycle.LiveData

class Repository(private val itemDao: ItemDao) {

    fun getAll(): LiveData<List<Item>> {
        return itemDao.getAll()
    }

    suspend fun insert(item: Item) {
        itemDao.insert(item)
    }

    suspend fun delete(item: Item) {
        itemDao.delete(item)
    }

    suspend fun clear() {
        itemDao.clear()
    }

    suspend fun update(item: Item) {
        itemDao.update(item)
    }

}