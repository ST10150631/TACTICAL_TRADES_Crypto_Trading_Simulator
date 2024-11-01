package za.co.varsitycollege.opsc7312_poe_tactical_trades.Model

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class OHLCV(
    @SerializedName("time_period_start") val timePeriodStart: String,
    @SerializedName("time_period_end") val timePeriodEnd: String,
    @SerializedName("time_open") val timeOpen: String,
    @SerializedName("time_close") val timeClose: String,
    @SerializedName("price_open") val priceOpen: Double,
    @SerializedName("price_high") val priceHigh: Double,
    @SerializedName("price_low") val priceLow: Double,
    @SerializedName("price_close") val priceClose: Double,
    @SerializedName("volume_traded") val volumeTraded: Double,
    @SerializedName("trades_count") val tradesCount: Int
)