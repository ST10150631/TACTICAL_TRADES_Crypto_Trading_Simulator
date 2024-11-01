package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

import android.media.Image

data class WalletModel(
    var walletType: String? = null,
    var percentage: String? = null,
    var amountInCoin: String? = null,
    val walletImage: Int? = null,
    val color: Int? = null,
    val walletGradient: Int? = null
) {
    constructor() : this(null, null, null, null, null, null)
}