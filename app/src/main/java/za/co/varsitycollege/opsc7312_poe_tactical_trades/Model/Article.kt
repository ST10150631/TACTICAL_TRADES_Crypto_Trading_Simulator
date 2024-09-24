package za.co.varsitycollege.opsc7312_poe_tactical_trades.Model

data class ArticlesResponse(
    val articles: Articles
)

data class Articles(
    val results: List<Article>,
    val totalResults: Int,
    val page: Int,
    val count: Int,
    val pages: Int
)

data class Article(
    val uri: String,
    val lang: String,
    val isDuplicate: Boolean,
    val date: String,
    val time: String,
    val dateTime: String,
    val dateTimePub: String,
    val dataType: String,
    val sim: Double,
    val url: String,
    val title: String,
    val body: String,
    val source: Source,
    val authors: List<Author>,
    val concepts: List<Concept>,
    val image: String,
    val eventUri: String?,
    val shares: Map<String, Any>, // Change this type as per the actual structure of "shares"
    val sentiment: Double,
    val wgt: Long,
    val relevance: Int
)

data class Source(
    val uri: String,
    val dataType: String,
    val title: String
)

data class Author(
    val authorName: String
)

data class Concept(
    val uri: String,
    val type: String,
    val score: Int,
    val label: Label,
    val location: Location? = null // Nullable if location is not always present
)

data class Label(
    val eng: String
)

data class Location(
    val type: String,
    val label: Label,
    val country: Country
)

data class Country(
    val type: String,
    val label: Label
)
