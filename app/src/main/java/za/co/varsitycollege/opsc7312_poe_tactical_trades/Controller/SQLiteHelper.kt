package za.co.varsitycollege.opsc7312_poe_tactical_trades.Controller
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.UserWithRelatedData
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.Wallet
import za.co.varsitycollege.opsc7312_poe_tactical_trades.Model.WatchlistItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.StockItem
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.User
import za.co.varsitycollege.opsc7312_poe_tactical_trades.View.WalletModel

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "TacticalTrades.db"
        private const val DATABASE_VERSION = 5


        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_NAME = "name"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_TOTAL_BALANCE = "total_balance"
        const val COLUMN_NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val COLUMN_PROFILE_PICTURE_URL = "profile_picture_url"
        const val COLUMN_GRAPH_THEME = "graph_theme"
        const val COLUMN_LANGUAGE = "language"

        const val TABLE_WALLETS = "wallets"
        const val COLUMN_WALLET_ID = "wallet_id"
        const val COLUMN_WALLET_USER_ID = "user_id"
        const val COLUMN_WALLET_TYPE = "wallet_type"
        const val COLUMN_WALLET_AMOUNT = "amount_in_coin"
        const val COLUMN_WALLET_COLOR = "color"
        const val COLUMN_WALLET_PERCENTAGE = "percentage"
        const val COLUMN_WALLET_GRADIENT = "wallet_gradient"
        const val COLUMN_WALLET_IMAGE = "wallet_image"


        const val TABLE_WATCHLIST = "watchlist"
        const val COLUMN_WATCHLIST_ID = "watchlist_id"
        const val COLUMN_WATCHLIST_USER_ID = "user_id"
        const val COLUMN_STOCK_ID = "stock_id"
        const val COLUMN_STOCK_NAME = "name"
        const val COLUMN_STOCK_IMAGE = "image_res"
        const val COLUMN_CURRENT_PRICE = "current_price"
        const val COLUMN_PRICE_DIFFERENCE = "price_difference"
        const val COLUMN_UP_DOWN = "up_down"
    }

    override fun onCreate(db: SQLiteDatabase) {
        Create(db)
    }

    fun Create(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID TEXT PRIMARY KEY,
                $COLUMN_EMAIL TEXT,
                $COLUMN_NAME TEXT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_TOTAL_BALANCE REAL,
                $COLUMN_NOTIFICATIONS_ENABLED INTEGER,
                $COLUMN_PROFILE_PICTURE_URL TEXT,
                $COLUMN_GRAPH_THEME TEXT,
                $COLUMN_LANGUAGE TEXT
            )
        """.trimIndent()
        db.execSQL(createUsersTable)

        // Create Wallets Table

        val createWalletsTable = """
        CREATE TABLE $TABLE_WALLETS (
            $COLUMN_WALLET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_WALLET_USER_ID TEXT,
            $COLUMN_WALLET_TYPE TEXT,
            $COLUMN_WALLET_AMOUNT TEXT,
            $COLUMN_WALLET_COLOR INTEGER,
            $COLUMN_WALLET_PERCENTAGE TEXT,
            $COLUMN_WALLET_GRADIENT INTEGER,
            $COLUMN_WALLET_IMAGE INTEGER,
            FOREIGN KEY($COLUMN_WALLET_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
        )
    """.trimIndent()
        db.execSQL(createWalletsTable)

        // Create Watchlist Table
        val createWatchlistTable = """
        CREATE TABLE $TABLE_WATCHLIST (
            $COLUMN_WATCHLIST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_WATCHLIST_USER_ID TEXT,
            $COLUMN_STOCK_ID TEXT,
            $COLUMN_STOCK_NAME TEXT,
            $COLUMN_STOCK_IMAGE TEXT,
            $COLUMN_CURRENT_PRICE TEXT,
            $COLUMN_PRICE_DIFFERENCE TEXT,
            $COLUMN_UP_DOWN INTEGER,
            FOREIGN KEY($COLUMN_WATCHLIST_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
        )
    """.trimIndent()
        db.execSQL(createWatchlistTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WATCHLIST")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WALLETS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(
        userId: String,
        email: String,
        name: String,
        username: String,
        totalBalance: Double,
        notificationsEnabled: Boolean,
        profilePictureUrl: String,
        graphTheme: String,
        language: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_EMAIL, email)
            put(COLUMN_NAME, name)
            put(COLUMN_USERNAME, username)
            put(COLUMN_TOTAL_BALANCE, totalBalance)
            put(COLUMN_NOTIFICATIONS_ENABLED, if (notificationsEnabled) 1 else 0)
            put(COLUMN_PROFILE_PICTURE_URL, profilePictureUrl)
            put(COLUMN_GRAPH_THEME, graphTheme)
            put(COLUMN_LANGUAGE, language)
        }
        db.insert(TABLE_USERS, null, values)
        db.close()
    }

    fun clearUsers() {
        val db = writableDatabase
        try {
            db.execSQL("DELETE FROM $TABLE_USERS")
            Log.d("SQLiteHelper", "Users table cleared")
        } catch (e: SQLiteException) {
            Log.e("SQLiteHelper", "Error clearing users: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun addWallet(wallet: WalletModel, userId: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WALLET_USER_ID, userId)
            put(COLUMN_WALLET_TYPE, wallet.walletType)
            put(COLUMN_WALLET_AMOUNT, wallet.amountInCoin)
            put(COLUMN_WALLET_COLOR, wallet.color)
            put(COLUMN_WALLET_PERCENTAGE, wallet.percentage)
            put(COLUMN_WALLET_GRADIENT, wallet.walletGradient)
            put(COLUMN_WALLET_IMAGE, wallet.walletImage)
        }
        db.insert(TABLE_WALLETS, null, values)
        db.close()
    }

    fun clearWallets() {
        val db = writableDatabase
        try {
            db.execSQL("DELETE FROM $TABLE_WALLETS")
            Log.d("SQLiteHelper", "Wallets table cleared")
        } catch (e: SQLiteException) {
            Log.e("SQLiteHelper", "Error clearing wallets: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun addWatchlistItem(stockItem: StockItem, userId: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WATCHLIST_USER_ID, userId)
            put(COLUMN_STOCK_ID, stockItem.stockId)
            put(COLUMN_STOCK_NAME, stockItem.name)
            put(COLUMN_STOCK_IMAGE, stockItem.imageRes)
            put(COLUMN_CURRENT_PRICE, stockItem.currentPrice)
            put(COLUMN_PRICE_DIFFERENCE, stockItem.priceDifference)
            put(COLUMN_UP_DOWN, if (stockItem.upDown) 1 else 0)
        }
        db.insert(TABLE_WATCHLIST, null, values)
        db.close()
    }

    fun clearWatchlist() {
        val db = writableDatabase
        try {
            db.execSQL("DELETE FROM $TABLE_WATCHLIST")
            Log.d("SQLiteHelper", "Watchlist table cleared")
        } catch (e: SQLiteException) {
            Log.e("SQLiteHelper", "Error clearing watchlist: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun isUserExists(userId: String): Boolean {
        val db = readableDatabase
        val cursor =
            db.query(TABLE_USERS, null, "$COLUMN_USER_ID=?", arrayOf(userId), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun updateUser(
        userId: String,
        email: String,
        name: String,
        username: String,
        totalBalance: Double,
        notificationsEnabled: Boolean,
        profilePictureUrl: String,
        graphTheme: String,
        language: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_NAME, name)
            put(COLUMN_USERNAME, username)
            put(COLUMN_TOTAL_BALANCE, totalBalance)
            put(COLUMN_NOTIFICATIONS_ENABLED, if (notificationsEnabled) 1 else 0)
            put(COLUMN_PROFILE_PICTURE_URL, profilePictureUrl)
            put(COLUMN_GRAPH_THEME, graphTheme)
            put(COLUMN_LANGUAGE, language)
        }
        db.update(TABLE_USERS, values, "$COLUMN_USER_ID=?", arrayOf(userId))
        db.close()
    }

    fun isWalletExists(walletId: String, userId: String): Boolean {

        val db = readableDatabase

        if (!db.isOpen) {
            return false
        }


        val cursor = db.query(
            TABLE_WALLETS,
            null,
            "$COLUMN_WALLET_USER_ID=? AND $COLUMN_WALLET_ID=?",
            arrayOf(userId, walletId),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun updateWallet(walletId: String, wallet: WalletModel, userId: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_WALLET_USER_ID, userId)
            put(COLUMN_WALLET_TYPE, wallet.walletType)
            put(COLUMN_WALLET_AMOUNT, wallet.amountInCoin)
            put(COLUMN_WALLET_COLOR, wallet.color)
            put(COLUMN_WALLET_PERCENTAGE, wallet.percentage)
            put(COLUMN_WALLET_GRADIENT, wallet.walletGradient)
            put(COLUMN_WALLET_IMAGE, wallet.walletImage)
        }
        db.update(TABLE_WALLETS, values, "$COLUMN_WALLET_ID=?", arrayOf(walletId))
        db.close()
    }

    fun isStockInWatchlist(stockId: String, userId: String): Boolean {
        val db = readableDatabase
        if (!db.isOpen) {
            return false
        }


        val cursor = db.query(
            TABLE_WATCHLIST,
            null,
            "$COLUMN_WATCHLIST_USER_ID=? AND $COLUMN_STOCK_ID=?",
            arrayOf(userId, stockId),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun updateWatchlistItem(stockId: String, stockItem: StockItem, userId: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STOCK_NAME, stockItem.name)
            put(COLUMN_STOCK_IMAGE, stockItem.imageRes)
            put(COLUMN_CURRENT_PRICE, stockItem.currentPrice)
            put(COLUMN_PRICE_DIFFERENCE, stockItem.priceDifference)
            put(COLUMN_UP_DOWN, if (stockItem.upDown) 1 else 0)
        }
        db.update(
            TABLE_WATCHLIST,
            values,
            "$COLUMN_WATCHLIST_USER_ID=? AND $COLUMN_STOCK_ID=?",
            arrayOf(userId, stockId)
        )
        db.close()
    }

    fun getAllUsersWithRelatedData(): List<UserWithRelatedData> {
        val userList = mutableListOf<UserWithRelatedData>()
        val db = this.readableDatabase
        val query = """
        SELECT u.*, w.*, wl.*
        FROM $TABLE_USERS u
        LEFT JOIN $TABLE_WALLETS w ON u.$COLUMN_USER_ID = w.$COLUMN_WALLET_USER_ID
        LEFT JOIN $TABLE_WATCHLIST wl ON u.$COLUMN_USER_ID = wl.$COLUMN_WATCHLIST_USER_ID
    """.trimIndent()

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val userId = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))
                val email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
                val totalBalance = cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_BALANCE))
                val notificationsEnabled = cursor.getInt(cursor.getColumnIndex(COLUMN_NOTIFICATIONS_ENABLED))
                val profilePictureUrl = cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_PICTURE_URL))
                val graphTheme = cursor.getString(cursor.getColumnIndex(COLUMN_GRAPH_THEME))
                val language = cursor.getString(cursor.getColumnIndex(COLUMN_LANGUAGE))

                val walletId = cursor.getInt(cursor.getColumnIndex(COLUMN_WALLET_ID))
                val walletType = cursor.getString(cursor.getColumnIndex(COLUMN_WALLET_TYPE))
                val walletAmount = cursor.getString(cursor.getColumnIndex(COLUMN_WALLET_AMOUNT))
                val walletColor = cursor.getInt(cursor.getColumnIndex(COLUMN_WALLET_COLOR))
                val walletPercentage = cursor.getString(cursor.getColumnIndex(COLUMN_WALLET_PERCENTAGE))
                val walletGradient = cursor.getInt(cursor.getColumnIndex(COLUMN_WALLET_GRADIENT))
                val walletImage = cursor.getInt(cursor.getColumnIndex(COLUMN_WALLET_IMAGE))

                val watchlistId = cursor.getInt(cursor.getColumnIndex(COLUMN_WATCHLIST_ID))
                val stockId = cursor.getString(cursor.getColumnIndex(COLUMN_STOCK_ID))
                val stockName = cursor.getString(cursor.getColumnIndex(COLUMN_STOCK_NAME))
                val stockImage = cursor.getString(cursor.getColumnIndex(COLUMN_STOCK_IMAGE))
                val currentPrice = cursor.getString(cursor.getColumnIndex(COLUMN_CURRENT_PRICE))
                val priceDifference = cursor.getString(cursor.getColumnIndex(COLUMN_PRICE_DIFFERENCE))
                val upDown = cursor.getInt(cursor.getColumnIndex(COLUMN_UP_DOWN))

                userList.add(UserWithRelatedData(
                    userId, email, name, username, totalBalance, notificationsEnabled, profilePictureUrl,
                    graphTheme, language, Wallet(walletId, walletType, walletAmount, walletColor, walletPercentage, walletGradient, walletImage),
                    WatchlistItem(watchlistId, stockId, stockName, stockImage, currentPrice, priceDifference, upDown)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }


    fun getUserDataByEmail(email: String): User? {
        val db = this.readableDatabase
        val query = """
        SELECT 
            u.user_id,
            u.email,
            u.name,
            u.username,
            u.total_balance,
            u.notifications_enabled,
            w.wallet_id,
            w.wallet_type,
            w.amount_in_coin,
            wl.watchlist_id,
            wl.stock_id,
            wl.name AS stock_name,
            wl.current_price,
            wl.price_difference,
            wl.up_down
        FROM 
            users u
        LEFT JOIN 
            wallets w ON u.user_id = w.user_id
        LEFT JOIN 
            watchlist wl ON u.user_id = wl.user_id
        WHERE 
            u.email = ?
    """

        val cursor = db.rawQuery(query, arrayOf(email))

        var userData: User? = null

        if (cursor.moveToFirst()) {
            userData = User(
                userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                totalBalance = cursor.getDouble(cursor.getColumnIndexOrThrow("total_balance")),
                wallets = mutableListOf(),
                watchlistItems = mutableListOf()
            )

            do {
                // Retrieve wallet data
                var walletType = cursor.getString(cursor.getColumnIndexOrThrow("wallet_type"))
                var percentage= cursor.getString(cursor.getColumnIndexOrThrow("percentage"))
                var amountInCoin  = cursor.getString(cursor.getColumnIndexOrThrow("amount_in_coin"))
                val walletImage  = cursor.getInt(cursor.getColumnIndexOrThrow("wallet_image"))
                val color = cursor.getInt(cursor.getColumnIndexOrThrow("color"))
                val walletGradient = cursor.getInt(cursor.getColumnIndexOrThrow("wallet_gradient"))

                if (walletType != null) {
                    userData.wallets?.add(WalletModel(walletType,percentage,amountInCoin,walletImage,color,walletGradient ))
                }

                val stockId = cursor.getString(cursor.getColumnIndexOrThrow("stock_id"))
                val name  = cursor.getString(cursor.getColumnIndexOrThrow("stock_name"))
                val imageRes = cursor.getString(cursor.getColumnIndexOrThrow("image_res"))
                val upDown = cursor.getInt(cursor.getColumnIndexOrThrow("up_down"))
                val currentPrice = cursor.getString(cursor.getColumnIndexOrThrow("current_price"))
                val priceDifference  = cursor.getString(cursor.getColumnIndexOrThrow("price_difference"))
                var upDownBoolean = true

                if (upDown == 0)
                {
                    upDownBoolean = false
                }
                else
                {
                    upDownBoolean = true
                }

                if (stockId != null) {
                    userData.watchlistItems?.add(
                        StockItem(
                            stockId,
                            name,
                            imageRes,
                            upDownBoolean,
                            currentPrice,
                            priceDifference
                        )
                    )
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return userData
    }

}
