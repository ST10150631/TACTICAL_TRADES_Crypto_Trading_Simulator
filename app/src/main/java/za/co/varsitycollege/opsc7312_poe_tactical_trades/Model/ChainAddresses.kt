package za.co.varsitycollege.opsc7312_poe_tactical_trades.Model

import com.google.gson.annotations.SerializedName


data class ChainAddresses(
  @SerializedName("chain_id") var chainId: String? = null,
  @SerializedName("network_id") var networkId: String? = null,
  @SerializedName("address") var address: String? = null
)