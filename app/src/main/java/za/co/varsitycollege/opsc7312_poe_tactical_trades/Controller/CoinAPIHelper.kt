package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller

import android.net.Uri
import android.util.Log
import android.view.Choreographer.FrameTimeline
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinAsset
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinList.coins
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.CoinsAssets
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.OHLCV
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

var API_KEY3 = "52470BBE-DFDE-453F-BE9A-E93B6B82D77F"//"fd0612a2-3ef6-48aa-824a-1c025b0e12e9"
const val API_KEY2 ="389E6140-D2F4-4539-BCA2-396577CC3821"
const val API_KEY = "D7460598-F041-4EF0-9AB5-B2BE1679C1A8" // Replace with your API key
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
    //Array of top 25 crypto Icons
    val assetLogoMap = mapOf(
        "BTC" to R.drawable.btc_logo,
        "ETH" to R.drawable.eth_logo,
        "USDT" to R.drawable.usdt_logo,
        "BNB" to R.drawable.bnb_logo,
        "SOL" to R.drawable.sol_logo,
        "USDC" to R.drawable.usdc_logo,
        "XRP" to R.drawable.xrp_logo,
        "DOGE" to R.drawable.doge_logo,
        "TRX" to R.drawable.trx_logo,
        "ADA" to R.drawable.ada_logo,
        "AVAX" to R.drawable.avax_logo,
        "LTC" to R.drawable.ltc_logo,
        "LINK" to R.drawable.link_logo,
        "BCH" to R.drawable.bch_logo,
        "DOT" to R.drawable.dot_logo,
        "LEO" to R.drawable.leo_logo,
        "DAI" to R.drawable.dai_logo,
        "UNI" to R.drawable.uni_logo,
        "WBTC" to R.drawable.wbtc_logo,
        "STETH" to R.drawable.steth_logo,
        "NEAR" to R.drawable.near_logo,
        "SHIB" to R.drawable.shib_logo,
        "TON" to R.drawable.ton_logo
    )
    val assetURLMap = mapOf(
        "BTC" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BTC_USD/history",
        "ETH" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_ETH_USD/history",
        "USDT" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_USDT_USD/history",
        "BNB" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BNB_USD/history",
        "SOL" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_SOL_USD/history",
        "USDC" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_USDC_USD/history",
        "XRP" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_XRP_USD/history",
        "DOGE" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_DOGE_USD/history",
        "TRX" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_TRX_USD/history",
        "ADA" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_ADA_USD/history",
        "AVAX" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_AVAX_USD/history",
        "LTC" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_LTC_USD/history",
        "LINK" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_LINK_USD/history",
        "BCH" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_BCH_USD/history",
        "DOT" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_DOT_USD/history",
        "LEO" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_LEO_USD/history",
        "DAI" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_DAI_USD/history",
        "UNI" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_UNI_USD/history",
        "WBTC" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_WBTC_USD/history",
        "STETH" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_STETH_USD/history",
        "NEAR" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_NEAR_USD/history",
        "SHIB" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_SHIB_USD/history",
        "TON" to "https://rest.coinapi.io/v1/ohlcv/BITSTAMP_SPOT_TON_USD/history"
    )
    // Array of the top 25 cryptocurrency IDs
    val top25CryptoIds ="BTC;ETH;USDT;BNB;SOL;USDC;XRP;DOGE;STETH;TON;TRX;ADA;AVAX;WSTETH;WBTC;SHIB;LINK;BCH;DOT;LEO;0DAI;UNI;LTC;NEAR"




/* INITIAL ONE

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
                url.readText()
            } catch (e: Exception) {
                e.printStackTrace()
                continue // Skip this iteration if there's an error reading the URL
            }

            if (coinJSON != null) {
                val gson = Gson()
                try {
                    // Since the response is an array, we parse it as a list
                    val coinData: List<CoinAsset> = gson.fromJson(coinJSON, object : TypeToken<List<CoinAsset>>() {}.type)
                    coinData.forEach {
                        it.logo = top25CryptoIcons[top25CryptoIds.indexOf(it.assetId)]
                    }
                    coinAssets.addAll(coinData) // Add all coins to the list
                } catch (e: Exception) {
                    Log.e(LOGGING_TAG, "Error parsing JSON: ${e.message}")
                }
            }
        }

        return coinAssets // Return the list of CoinAssets
    }
 */
    fun top25FromAPI(): List<CoinAsset> {

        if(coins.isEmpty()){
                // Construct the URL correctly using the asset ID
                val coinUrl ="https://rest.coinapi.io/v1/assets?filter_asset_id=$top25CryptoIds"  //https://rest.coinapi.io/v1/assets/$coin
                val buildUri: Uri = Uri.parse(coinUrl).buildUpon()
                    .appendQueryParameter(PARAM_API_KEY, API_KEY) // passing in API key
                    .build()

                var url: URL? = null
                try {
                    url = URL(buildUri.toString())
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }

                Log.i(LOGGING_TAG, "Building URL for coin: $url")

                val connection = url?.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("X-CoinAPI-Key", API_KEY)

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val coinJSON = connection.inputStream.bufferedReader().use { it.readText() }
                    val gson = Gson()
                    try {
                        // Since the response is an array, we parse it as a list
                        val coinData: List<CoinAsset> = gson.fromJson(coinJSON, object : TypeToken<List<CoinAsset>>() {}.type)
                        coinData.forEach {asset ->
                            asset.logo = assignLogo(asset.assetId)
                        }
                        coins.addAll(coinData.sortedByDescending { it.priceUsd })// Add all coins to the list
                    } catch (e: Exception) {
                        Log.e(LOGGING_TAG, "Error parsing JSON: ${e.message}")
                        var retry = retryTop25FromAPI(API_KEY2)
                        if (retry.isEmpty()){
                            retry = retryTop25FromAPI(API_KEY3)
                        }
                        return retry
                    }
                } else {
                    Log.e(LOGGING_TAG, "Error: Received response code $responseCode")
                    var retry = retryTop25FromAPI(API_KEY2)
                    if (retry.isEmpty()){
                        retry = retryTop25FromAPI(API_KEY3)
                    }
                    return retry

                }


            return coins // Return the list of CoinAssets
        }
    else
        return coins

    }

    fun retryTop25FromAPI(apiKey : String): List<CoinAsset> {

        if(coins.isEmpty()){
            // Construct the URL correctly using the asset ID
            val coinUrl ="https://rest.coinapi.io/v1/assets?filter_asset_id=$top25CryptoIds"  //https://rest.coinapi.io/v1/assets/$coin
            val buildUri: Uri = Uri.parse(coinUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, apiKey) // passing in API key
                .build()

            var url: URL? = null
            try {
                url = URL(buildUri.toString())
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

            Log.i(LOGGING_TAG, "Building URL for coin: $url")

            val connection = url?.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("X-CoinAPI-Key", apiKey)

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val coinJSON = connection.inputStream.bufferedReader().use { it.readText() }
                val gson = Gson()
                try {
                    // Since the response is an array, we parse it as a list
                    val coinData: List<CoinAsset> = gson.fromJson(coinJSON, object : TypeToken<List<CoinAsset>>() {}.type)
                    coinData.forEach {asset ->
                        asset.logo = assignLogo(asset.assetId)
                    }
                    coins.addAll(coinData.sortedByDescending { it.priceUsd })// Add all coins to the list
                } catch (e: Exception) {
                    Log.e(LOGGING_TAG, "Error parsing JSON: ${e.message}")
                }
            } else {
                Log.e(LOGGING_TAG, "Error: Received response code $responseCode")
                /*
                var retry = retryTop25FromAPI(API_KEY2)
                if (retry.isEmpty()){
                    retry = retryTop25FromAPI(API_KEY3)
                }
                return retry

                 */
            }


            return coins // Return the list of CoinAssets
        }
        else
        return coins

    }

    fun getOHLCVData(assetId: String, timeStart: String): List<OHLCV> {
        val ohclvs = mutableListOf<OHLCV>()
        val symbol = "BITSTAMP_SPOT_${assetId}_USD"
        // Get the current time for timeEnd
        val timeEnd = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        // Construct the URL correctly using the asset ID
        val coinUrl = assetURLMap[assetId].toString() // Return an empty list if the asset ID is not found

        // Build the URI with the required parameters
        val buildUri: Uri = Uri.parse(coinUrl).buildUpon()
            .appendQueryParameter("period_id", "1DAY") // Example period
            .appendQueryParameter("time_start", timeStart)
            .appendQueryParameter("time_end", timeEnd)
            .appendQueryParameter("limit","365")
            .build()

        Log.i(LOGGING_TAG, "Building URL for OHLCV: ${buildUri.toString()}")

        var connection: HttpURLConnection? = null
        try {
            val url = URL(buildUri.toString())
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("X-CoinAPI-Key", API_KEY3)

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val ohlcvJSON = connection.inputStream.bufferedReader().use { it.readText()}
                val gson = Gson()
                // Since the response is an array, we parse it as a list
                val ohclvData: List<OHLCV> = gson.fromJson(ohlcvJSON, object : TypeToken<List<OHLCV>>() {}.type)
                ohclvs.addAll(ohclvData.sortedByDescending { it.timeOpen }) // Add all OHLCV data to the list
                return ohclvData
            } else {
                Log.e(LOGGING_TAG, "Error: Response Code $responseCode")
            }
        } catch (e: Exception) {
            Log.e(LOGGING_TAG, "Exception occurred: ${e.message}")
        } finally {
            connection?.disconnect() // Ensure the connection is closed
        }

        return ohclvs // Return the collected data
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
    // Function to assign the logo based on the asset ID
    private fun assignLogo(assetId: String?): Int {
        return assetLogoMap[assetId] ?: 0 // Return a default icon if not found
    }

}





