package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller

import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.Article
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.ArticlesResponse // Import your ArticlesResponse model
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class NewsAPIHelper {
    private val API_KEY ="c51cb993-3810-4bdb-9188-55943c08be69"
    //private val API_KEY = "98d693dc-f140-4265-ac44-1c7836966f4a"
    private val baseURL = "https://eventregistry.org/api/v1/article/getArticles"
    private val PARAM_API_KEY = "apiKey"
    private val LOGGING_TAG = "URLWECREATED"
    private val newsList = mutableListOf<Article>()

    fun getArticles(): List<Article> {
        val buildUri: Uri = Uri.parse(baseURL).buildUpon()
            .appendQueryParameter(PARAM_API_KEY, API_KEY)
            .appendQueryParameter("keyword", "Cryptocurrency")
            .appendQueryParameter("articlesCount", "5")
            .build()

        var url: URL? = null
        try {
            url = URL(buildUri.toString())
        } catch (e: MalformedURLException) {
            Log.e(LOGGING_TAG, "Malformed URL: ${e.message}")
            return emptyList()
        }

        Log.i(LOGGING_TAG, "Building URL for news: $url")

        val connection = url?.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val newsJSON = connection.inputStream.bufferedReader().use { it.readText() }
            val gson = Gson()
            return try {
                // Parse the JSON response to ArticlesResponse
                val articlesResponse: ArticlesResponse = gson.fromJson(newsJSON, ArticlesResponse::class.java)

                // Clear the newsList and add the articles
                newsList.clear()
                newsList.addAll(articlesResponse.articles.results)
                newsList // Return the updated list
            } catch (e: Exception) {
                Log.e(LOGGING_TAG, "Error parsing JSON: ${e.message}")
                emptyList() // Return an empty list on error
            }
        } else {
            Log.e(LOGGING_TAG, "Error: Received response code $responseCode")
            return emptyList() // Return an empty list on error
        }
    }
}
