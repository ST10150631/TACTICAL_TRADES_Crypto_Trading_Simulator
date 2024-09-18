package za.co.varsitycollege.opsc7312_poe_tactical_trades.Model

import com.google.gson.annotations.SerializedName

data class CoinAsset(
  @SerializedName("asset_id") var assetId: String? = null,
  @SerializedName("name") var name: String? = null,
  @SerializedName("type_is_crypto") var typeIsCrypto: Int? = null,
  @SerializedName("data_quote_start") var dataQuoteStart: String? = null,
  @SerializedName("data_quote_end") var dataQuoteEnd: String? = null,
  @SerializedName("data_orderbook_start") var dataOrderbookStart: String? = null,
  @SerializedName("data_orderbook_end") var dataOrderbookEnd: String? = null,
  @SerializedName("data_trade_start") var dataTradeStart: String? = null,
  @SerializedName("data_trade_end") var dataTradeEnd: String? = null,
  @SerializedName("data_symbols_count") var dataSymbolsCount: Int? = null,
  @SerializedName("volume_1hrs_usd") var volume1hrsUsd: Double? = null,
  @SerializedName("volume_1day_usd") var volume1dayUsd: Double? = null,
  @SerializedName("volume_1mth_usd") var volume1mthUsd: Double? = null,
  @SerializedName("price_usd") var priceUsd: Double? = null,
  @SerializedName("chain_addresses") var chainAddresses: List<ChainAddress>? = null,
  @SerializedName("data_start") var dataStart: String? = null,
  @SerializedName("data_end") var dataEnd: String? = null
)
data class ChainAddress(
  @SerializedName("chain_id") var chainId: String? = null,
  @SerializedName("network_id") var networkId: String? = null,
  @SerializedName("address") var address: String? = null
)