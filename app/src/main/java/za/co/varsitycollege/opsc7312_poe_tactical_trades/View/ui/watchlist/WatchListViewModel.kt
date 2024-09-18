package za.co.varsitycollege.opsc7312_poe_tactical_trades.View.ui.watchlist


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem

class WatchListViewModel : ViewModel() {

    private val _watchList = MutableLiveData<List<StockItem>>()

    val watchList: LiveData<List<StockItem>> get() = _watchList

    init {
        _watchList.value = getWatchListItems()
    }

    fun getWatchListItems(): List<StockItem> {
        return listOf(
            StockItem("AMZN", "Amazon Inc", R.drawable.amazon_icon, true, "$113.22", "+9.77%"),
            StockItem("TSLA", "Tesla LLC", R.drawable.tesla_icon, false, "$681.79", "-0.34%"),
            StockItem("GOOGL", "Alphabet Inc", R.drawable.google_icon, false, "$2,174.75", "-0.21%")
        )
    }

    fun updateWatchListItems(items: List<StockItem>) {
        _watchList.value = items
    }
}
