package za.co.varsitycollege.opsc7312_poe_tactical_trades.View

data class User(
    var username: String? = null,
    var name: String? = null,
    var email: String? = null,
    val theme: String? = null,
    val graphTheme: String? = null,
    val language: String? = null,
    val profilePictureUrl: String? = null
)
