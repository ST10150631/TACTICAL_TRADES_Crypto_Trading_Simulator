package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

data class StockItem(
    val stockId: String,
    val name: String,
    val imageRes: Int,
    val upDown: Boolean,
    val currentPrice: String,
    val priceDifference: String
)
