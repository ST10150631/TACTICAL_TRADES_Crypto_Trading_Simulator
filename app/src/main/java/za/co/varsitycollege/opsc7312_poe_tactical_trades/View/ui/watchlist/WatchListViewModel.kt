package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.watchlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller.FirebaseHelper
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem

class WatchListViewModel : ViewModel() {

    private val _watchList = MutableLiveData<List<StockItem>>()
    val watchList: LiveData<List<StockItem>> get() = _watchList
    private val auth: FirebaseAuth by lazy { FirebaseHelper.firebaseAuth }

    init {
        fetchWatchListItemsFromFirebase()
    }

    private fun fetchWatchListItemsFromFirebase() {
        val userId = auth.currentUser

        if (userId != null) {
            val watchlistRef = FirebaseHelper.databaseReference
                .child(userId.uid)
                .child("watchlist")

            watchlistRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val stockItems = mutableListOf<StockItem>()

                    for (stockSnapshot in snapshot.children) {
                        val stockId = stockSnapshot.child("stockId").getValue(String::class.java)
                        val name = stockSnapshot.child("name").getValue(String::class.java)
                        val imageRes = stockSnapshot.child("imageRes").getValue(String::class.java) // Now handled as String (URL)
                        val upDown = stockSnapshot.child("upDown").getValue(Boolean::class.java)
                        val currentPrice = stockSnapshot.child("currentPrice").getValue(String::class.java)
                        val priceDifference = stockSnapshot.child("priceDifference").getValue(String::class.java)

                        // Handling potential Long-to-String conversion for price fields
                        if (stockId != null && name != null && imageRes != null && upDown != null) {
                            val currentPriceValue = currentPrice?.toString() ?: "0" // Ensure it's a String
                            val priceDiffValue = priceDifference?.toString() ?: "0"

                            stockItems.add(
                                StockItem(
                                    stockId,
                                    name,
                                    imageRes,
                                    upDown,
                                    currentPriceValue,
                                    priceDiffValue
                                )
                            )
                        }
                    }

                    _watchList.value = stockItems
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("WatchListViewModel", "Failed to fetch watchlist: ${error.message}")
                }
            })
        } else {
            Log.e("WatchListViewModel", "User ID is null, cannot fetch watchlist")
        }
    }


    fun updateWatchListItems(items: List<StockItem>) {
        _watchList.value = items
    }
}
