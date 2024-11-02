package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

data class User(
    var username: String? = null,
    var name: String? = null,
    var email: String? = null,
    var totalBalance: Double? = null,
    val theme: String? = null,
    val graphTheme: String? = null,
    val language: String? = null,
    val profilePictureUrl: String? = null,
    val userId: String? = null,
    val wallets: MutableList<WalletModel>? = null,
    val watchlistItems: MutableList<StockItem>? = null
)
