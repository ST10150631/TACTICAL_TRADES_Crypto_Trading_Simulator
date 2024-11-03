package za.co.varsitycollege.opsc7312_poe_tactical_trades.Model

import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel

data class User(
    val userId: String? = null,
    var username: String? = null,
    var name: String? = null,
    var email: String? = null,
    var totalBalance: Double? = null,
    var startValue: Double? = null,
    val theme: String? = null,
    val graphTheme: String? = null,
    val language: String? = null,
    val profilePictureUrl: String? = null,
    val notificationsEnabled: Boolean? = null,
    val wallets: MutableList<WalletModel>? = null,
    val watchlistItems: MutableList<StockItem>? = null,
    val difference: Double? = null
)
