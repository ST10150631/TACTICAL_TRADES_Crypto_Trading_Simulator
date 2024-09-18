package za.co.varsitycollege.opsc7312_poe_tactical_trades.Model

import com.google.gson.annotations.SerializedName

data class CoinsAssets(
    @SerializedName("AllCoins") var AllCoins: List<CoinAsset> = listOf()
)






