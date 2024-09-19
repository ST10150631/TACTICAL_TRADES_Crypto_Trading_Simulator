package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

import java.io.Serializable

data class StockItem(
    val stockId: String = "",
    val name: String = "",
    val imageRes: Int = 0,
    val upDown: Boolean = false,
    val currentPrice: String = "",
    val priceDifference: String = ""
) : Serializable
