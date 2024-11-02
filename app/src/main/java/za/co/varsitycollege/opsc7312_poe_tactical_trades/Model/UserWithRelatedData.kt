package za.co.varsitycollege.opsc7312_poe_tactical_trades.Model

data class UserWithRelatedData(
    val userId: String,
    val email: String,
    val name: String,
    val username: String,
    val totalBalance: Double,
    val notificationsEnabled: Int,
    val profilePictureUrl: String?,
    val graphTheme: String?,
    val language: String?,
    val wallet: Wallet?,
    val watchlistItem: WatchlistItem?
)

data class Wallet(
    val walletId: Int,
    val walletType: String,
    val amountInCoin: String,
    val color: Int,
    val percentage: String,
    val gradient: Int,
    val image: Int
)

data class WatchlistItem(
    val watchlistId: Int,
    val stockId: String,
    val name: String,
    val imageRes: String,
    val currentPrice: String,
    val priceDifference: String,
    val upDown: Int
)
