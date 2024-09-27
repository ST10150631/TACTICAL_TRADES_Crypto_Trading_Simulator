package za.co.varsitycollege.opsc7312_poe_tactical_trades.Model

import java.time.Instant

data class OHLCV(
    val timePeriodStart: Instant,
    val timePeriodEnd: Instant,
    val timeOpen: Instant,
    val timeClose: Instant,
    val priceOpen: Double,
    val priceHigh: Double,
    val priceLow: Double,
    val priceClose: Double,
    val volumeTraded: Double,
    val tradesCount: Int
)
