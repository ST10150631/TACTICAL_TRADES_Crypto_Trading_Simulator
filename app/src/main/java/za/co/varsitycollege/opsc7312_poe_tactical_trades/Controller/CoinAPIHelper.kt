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
import za.co.varsitycollege.opsc7312_poe_tactical_trades.R
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

const val API_KEY = "fd0612a2-3ef6-48aa-824a-1c025b0e12e9"
//const val API_KEY = "D7460598-F041-4EF0-9AB5-B2BE1679C1A8" // Replace with your API key
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

    var coinAssets = mutableListOf<CoinAsset>()
    //Array of top 25 crypto Icons
    val  top25CryptoIcons = arrayOf(
        R.drawable.btc_logo,
        R.drawable.eth_logo,
        R.drawable.usdt_logo,
        R.drawable.bnb_logo,
        R.drawable.sol_logo,
        R.drawable.usdc_logo,
        R.drawable.xrp_logo,
        //R.drawable.steth_logo,
        R.drawable.doge_logo,
        /*
        R.drawable.ton_logo,
        R.drawable.trx_logo,
        R.drawable.ada_logo,
        R.drawable.avax_logo,
        R.drawable.steth_logo,
        R.drawable.wbtc_logo,
        R.drawable.shib_logo,
        R.drawable.weth_logo,
        R.drawable.link_logo,
        R.drawable.bch_logo,
        R.drawable.dot_logo,
        R.drawable.leo_logo,
        R.drawable.dai_logo,
        R.drawable.uni_logo,

         */
        R.drawable.ltc_logo
       // R.drawable.near_logo

    )
    // Array of the top 25 cryptocurrency IDs
    val top25CryptoIds = arrayOf(
        "BTC",  // Bitcoin
        "ETH",  // Ethereum
        "USDT", // Tether
        "BNB",  // Binance Coin
        "SOL",  // Solana
        "USDC", // USD Coin
        "XRP",  // XRP
        //"STETH", // Lido Staked Ether
        "DOGE", // Dogecoin
        /*
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
        */

        "LTC"  // Litecoin
        //"NEAR"  // NEAR Protocol
    )

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

        if(coinAssets.isEmpty()){
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

                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("X-CoinAPI-Key", API_KEY)

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val coinJSON = connection.inputStream.bufferedReader().use { it.readText() }
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
                } else {
                    Log.e(LOGGING_TAG, "Error: Received response code $responseCode")
                }
            }

            return coinAssets // Return the list of CoinAssets
        }
    else
        return coinAssets

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





