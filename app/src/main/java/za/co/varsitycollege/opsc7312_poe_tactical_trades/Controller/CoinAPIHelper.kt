package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller

import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinsAssets
import java.net.MalformedURLException
import java.net.URL


const val API_KEY = "fd0612a2-3ef6-48aa-824a-1c025b0e12e9" // Replace with your API key
class CoinAPIHelper {
/*
    fun fetchAssets(): String? {
        // Initialize OkHttpClient
        val client = OkHttpClient()

        // Build the Request
        val request = Request.Builder()
            .url("https://rest.coinapi.io/v1/assets")
            .get() // Specify GET method without a body
            .addHeader("Accept", "application/json")
            .addHeader("X-CoinAPI-Key", API_KEY)
            .build()
            val response = client.newCall(request).execute()
              return  response.body?.string()
    }
*/
    private val coin_URL = "https://rest.coinapi.io/v1/assets"
    private val PARAM_API_KEY ="apikey"
    var coinList = mutableListOf<CoinAsset>()
    // Array of the top 25 cryptocurrency IDs
    val top25CryptoIds = arrayOf(
        "BTC",  // Bitcoin
        "ETH",  // Ethereum
        "USDT", // Tether
        "BNB",  // Binance Coin
        "SOL",  // Solana
        "USDC", // USD Coin
        "XRP",  // XRP
        "STETH", // Lido Staked Ether
        "DOGE", // Dogecoin
        "TON",  // Toncoin
        "TRX",  // TRON
        "ADA",  // Cardano
        "AVAX", // Avalanche
        "WSTETH", // Wrapped stETH
        "WBTC", // Wrapped Bitcoin
        "SHIB", // Shiba Inu
        "WETH", // Wrapped Ethereum
        "LINK", // Chainlink
        "BCH",  // Bitcoin Cash
        "DOT",  // Polkadot
        "LEO",  // LEO Token
        "DAI",  // Dai
        "UNI",  // Uniswap
        "LTC",  // Litecoin
        "NEAR"  // NEAR Protocol
    )


    fun top25FromAPI(): List<CoinAsset> {
        val coinAssets = mutableListOf<CoinAsset>()

        for (coin in top25CryptoIds) {
            // Construct the URL correctly using the asset ID
            val coinUrl = "https://rest.coinapi.io/v1/assets/$coin"
            val buildUri: Uri = Uri.parse(coinUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY) // passing in API key
                .build()

            var url: URL? = null
            try {
                url = URL(buildUri.toString())
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                continue // Skip this iteration if the URL is malformed
            }

            Log.i(LOGGING_TAG, "Building URL for coin: $url")

            val coinJSON = try {
                url?.readText()
            } catch (e: Exception) {
                e.printStackTrace()
                continue // Skip this iteration if there's an error reading the URL
            }

            if (coinJSON != null) {
                val gson = Gson()
                try {
                    // Since the response is an array, we parse it as a list
                    val coinData: List<CoinAsset> = gson.fromJson(coinJSON, object : TypeToken<List<CoinAsset>>() {}.type)
                    coinAssets.addAll(coinData) // Add all coins to the list
                } catch (e: Exception) {
                    Log.e(LOGGING_TAG, "Error parsing JSON: ${e.message}")
                }
            }
        }

        return coinAssets // Return the list of CoinAssets
    }



    val LOGGING_TAG = "URLWECREATED"

    fun buildURLforCoin(API_KEY: String): URL?
    {
        val buildUri: Uri = Uri.parse(coin_URL).buildUpon()
            .appendQueryParameter(
                PARAM_API_KEY, API_KEY ) // passing in api key
            .build()
        var url: URL? = null

        try {
            url = URL(buildUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        Log.i(LOGGING_TAG, "buildURLforCoin: $url")
        return url
    }


}





