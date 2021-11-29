package android.example.myinventory_mvp_neobis.contract

import android.example.myinventory_mvp_neobis.model.Item

interface Delegates {
    interface RecyclerItemClicked{
        fun onItemClick(item: Item)
    }
}