package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel
object WalletRepository {
    fun findWalletByType(walletType: String): WalletModel? {
        return wallets.firstOrNull {
            it.walletType.equals(walletType, ignoreCase = true)
        }
    }

    // Mutable list to store wallets
    val wallets = mutableListOf<WalletModel>()
}
//------------------------END OF FILE-------------------------------//